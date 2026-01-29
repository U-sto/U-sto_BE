package com.usto.api.item.returning.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.repository.AssetRepository;
import com.usto.api.item.common.model.OperStatus;
import com.usto.api.item.returning.domain.model.ReturningDetail;
import com.usto.api.item.returning.domain.model.ReturningMaster;
import com.usto.api.item.returning.domain.repository.ReturningRepository;
import com.usto.api.item.returning.presentation.dto.request.ReturningRegisterRequest;
import com.usto.api.item.returning.presentation.dto.request.ReturningSearchRequest;
import com.usto.api.item.returning.presentation.dto.response.ReturningItemListResponse;
import com.usto.api.item.returning.presentation.dto.response.ReturningListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReturningService {

    private final ReturningRepository returningRepository;
    private final AssetRepository assetRepository;

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    /**
     * 반납 신청 등록
     * - 여러 물품을 한 번에 반납 신청
     * - 운용(OPER) 상태의 물품만 반납 가능
     */
    @Transactional
    public UUID registerReturning(ReturningRegisterRequest request, String userId, String orgCd) {
        // 1. 반납 신청서(Master) 생성
        ReturningMaster master = ReturningMaster.create(
                userId,
                request.getAplyAt(),
                request.getItemSts(),
                request.getRtrnRsn(),
                orgCd
        );

        ReturningMaster savedMaster = returningRepository.saveMaster(master);

        // 2. 각 물품 검증 및 반납 상세(Detail) 생성
        for (String itmNo : request.getItmNos()) {
            // 2-1. 물품 존재 여부 및 소유권 확인
            Asset asset = assetRepository.findById(itmNo, orgCd)
                    .orElseThrow(() -> new BusinessException("물품을 찾을 수 없습니다: " + itmNo));

            asset.validateOwnership(orgCd);

            // 2-2. 이미 대기 중인 다른 반납 건에 포함되어 있는지 확인
            if (returningRepository.existsInOtherReturning(itmNo, null, orgCd)) {
                throw new BusinessException("이미 다른 반납 신청에 포함된 물품입니다: " + itmNo);
            }

            // 2-3. 운용 상태 확인 (OPER만 반납 가능)
            if (asset.getOperSts() != OperStatus.OPER) {
                throw new BusinessException("운용 중인 물품만 반납할 수 있습니다: " + itmNo);
            }

            // 2-4. 반납 상세 생성
            ReturningDetail detail = ReturningDetail.create(
                    savedMaster.getRtrnMId(),
                    itmNo,
                    asset.getDeptCd(),  // 현재 부서코드 스냅샷
                    orgCd
            );

            returningRepository.saveDetail(detail);
        }

        return savedMaster.getRtrnMId();
    }

    /**
     * 반납 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ReturningListResponse> getReturningList(ReturningSearchRequest searchRequest, String orgCd) {
        return returningRepository.findAllByFilter(searchRequest, orgCd);
    }

    /**
     * 반납물품목록 조회 (특정 반납 신청의 상세 물품 목록)
     */
    @Transactional(readOnly = true)
    public List<ReturningItemListResponse> getReturningItems(UUID rtrnMId, String orgCd) {
        // 마스터 존재 여부 확인
        returningRepository.findMasterById(rtrnMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 반납 신청입니다."));

        return returningRepository.findItemsByMasterId(rtrnMId, orgCd);
    }

    /**
     * 반납 신청 수정
     */
    @Transactional
    public void updateReturning(UUID rtrnMId, ReturningRegisterRequest request, String orgCd) {
        // 1. 마스터 조회 및 상태 검증
        ReturningMaster master = returningRepository.findMasterById(rtrnMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 반납 신청입니다."));

        master.validateOwnership(orgCd);
        master.validateModifiable();

        // 2. 마스터 기본 정보 업데이트 (사유, 상태 등)
        master.updateInfo(request.getItemSts(), request.getRtrnRsn());
        returningRepository.saveMaster(master);

        // 3. 기존 상세 내역(물품 목록) 전체 삭제
        returningRepository.deleteAllDetailsByMasterId(rtrnMId);

        // 4. 새로운 Detail 생성 (물품 목록 전체 교체)
        for (String itmNo : request.getItmNos()) {
            Asset asset = assetRepository.findById(itmNo, orgCd)
                    .orElseThrow(() -> new BusinessException("물품을 찾을 수 없습니다: " + itmNo));

            asset.validateOwnership(orgCd);

            if (asset.getOperSts() != OperStatus.OPER) {
                throw new BusinessException("운용 중인 물품만 반납할 수 있습니다: " + itmNo);
            }
            // "현재 수정 중인 이 신청서"를 제외하고 다른 신청서에 등록되었는지 확인
            if (returningRepository.existsInOtherReturning(itmNo, rtrnMId, orgCd)) {
                throw new BusinessException("이미 다른 반납 신청에 포함된 물품입니다: " + itmNo);
            }

            ReturningDetail detail = ReturningDetail.create(
                    rtrnMId, itmNo, asset.getDeptCd(), orgCd
            );

            returningRepository.saveDetail(detail);
        }
    }

    /**
     * 반납 신청 삭제 (WAIT만 가능)
     */
    @Transactional
    public void deleteReturning(UUID rtrnMId, String orgCd) {
        ReturningMaster master = returningRepository.findMasterById(rtrnMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 반납 신청입니다."));

        master.validateOwnership(orgCd);
        master.validateModifiable();

        // 고아 레코드 방지를 위해 상세 먼저 삭제 후 마스터 삭제
        returningRepository.deleteAllDetailsByMasterId(rtrnMId);
        returningRepository.deleteMaster(rtrnMId);
    }

    /**
     * 승인 요청
     */
    @Transactional
    public void requestApproval(UUID rtrnMId, String orgCd) {
        ReturningMaster master = returningRepository.findMasterById(rtrnMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 반납 신청입니다."));

        master.validateOwnership(orgCd);
        master.requestApproval();

        returningRepository.saveMaster(master);
    }

    /**
     * 승인 요청 취소 → 소프트 삭제
     */
    @Transactional
    public void cancelRequest(UUID rtrnMId, String orgCd) {
        ReturningMaster master = returningRepository.findMasterById(rtrnMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 반납 신청입니다."));

        master.validateOwnership(orgCd);
        master.validateCancellable();

        returningRepository.deleteAllDetailsByMasterId(rtrnMId);
        returningRepository.deleteMaster(rtrnMId);
    }

    /**
     * TODO: 반납 승인 (ADMIN 권한)
     * - 승인 시 물품 상태를 RTN(반납)으로 변경
     * - 부서코드 NONE 값으로 변경
     * - 상태 이력 테이블에 기록
     */

    /**
     * TODO: 반납 반려 (ADMIN 권한)
     */
}