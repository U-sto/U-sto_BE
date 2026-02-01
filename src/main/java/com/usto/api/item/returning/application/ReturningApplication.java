package com.usto.api.item.returning.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.repository.AssetRepository;
import com.usto.api.item.asset.domain.service.AssetPolicy;
import com.usto.api.item.common.model.OperStatus;
import com.usto.api.item.returning.domain.model.ReturningDetail;
import com.usto.api.item.returning.domain.model.ReturningMaster;
import com.usto.api.item.returning.domain.repository.ReturningRepository;
import com.usto.api.item.returning.domain.service.ReturningPolicy;
import com.usto.api.item.returning.infrastructure.mapper.ReturningMapper;
import com.usto.api.item.returning.presentation.dto.request.ReturningRegisterRequest;
import com.usto.api.item.returning.presentation.dto.request.ReturningSearchRequest;
import com.usto.api.item.returning.presentation.dto.response.ReturningItemListResponse;
import com.usto.api.item.returning.presentation.dto.response.ReturningListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReturningApplication {

    private final ReturningRepository returningRepository;
    private final ReturningPolicy returningPolicy;
    private final AssetRepository assetRepository;
    private final AssetPolicy assetPolicy;

    /**
     * 반납 신청 등록
     * - 여러 물품을 한 번에 반납 신청
     * - 운용(OPER) 상태의 물품만 반납 가능
     */
    @Transactional
    public UUID registerReturning(ReturningRegisterRequest request, String userId, String orgCd) {
        // 반납일자 검증
        returningPolicy.validateAplyAt(request.getAplyAt());

        // 1. 반납 신청서(Master) 생성 (Mapper 사용)
        ReturningMaster master = ReturningMapper.toMasterDomain(
                userId,
                request.getAplyAt(),
                request.getItemSts(),
                request.getRtrnRsn(),
                orgCd
        );

        ReturningMaster savedMaster = returningRepository.saveMaster(master);

        // 2. 각 물품 검증 및 반납 상세(Detail) 생성
        for (String itmNo : request.getItmNos()) {
            // 물품 조회 및 검증
            Asset asset = assetRepository.findById(itmNo, orgCd)
                    .orElseThrow(() -> new BusinessException("물품을 찾을 수 없습니다: " + itmNo));

            assetPolicy.validateUpdate(asset, orgCd);

            // 중복 체크
            if (returningRepository.existsInOtherReturning(itmNo, null, orgCd)) {
                throw new BusinessException("이미 다른 반납 신청에 포함된 물품입니다: " + itmNo);
            }

            // 운용 상태 확인
            if (asset.getOperSts() != OperStatus.OPER) {
                throw new BusinessException("운용 중인 물품만 반납할 수 있습니다: " + itmNo);
            }

            // 반납 상세 생성 (Mapper 사용)
            ReturningDetail detail = ReturningMapper.toDetailDomain(
                    savedMaster.getRtrnMId(),
                    itmNo,
                    asset.getDeptCd(),
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
     * 반납물품목록 조회
     */
    @Transactional(readOnly = true)
    public List<ReturningItemListResponse> getReturningItems(UUID rtrnMId, String orgCd) {
        returningRepository.findMasterById(rtrnMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 반납 신청입니다."));

        return returningRepository.findItemsByMasterId(rtrnMId, orgCd);
    }

    /**
     * 반납 신청 수정
     */
    @Transactional
    public void updateReturning(UUID rtrnMId, ReturningRegisterRequest request, String orgCd) {
        // 1. 마스터 조회
        ReturningMaster master = returningRepository.findMasterById(rtrnMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 반납 신청입니다."));

        // 2. 정책 검증
        returningPolicy.validateOwnership(master, orgCd);
        returningPolicy.validateModifiable(master);

        // 3. 마스터 정보 업데이트
        master.updateInfo(request.getItemSts(), request.getRtrnRsn());
        returningRepository.saveMaster(master);

        // 4. 기존 상세 삭제
        returningRepository.deleteAllDetailsByMasterId(rtrnMId);

        // 5. 새로운 Detail 생성
        for (String itmNo : request.getItmNos()) {
            Asset asset = assetRepository.findById(itmNo, orgCd)
                    .orElseThrow(() -> new BusinessException("물품을 찾을 수 없습니다: " + itmNo));

            assetPolicy.validateUpdate(asset, orgCd);

            if (asset.getOperSts() != OperStatus.OPER) {
                throw new BusinessException("운용 중인 물품만 반납할 수 있습니다: " + itmNo);
            }

            if (returningRepository.existsInOtherReturning(itmNo, rtrnMId, orgCd)) {
                throw new BusinessException("이미 다른 반납 신청에 포함된 물품입니다: " + itmNo);
            }

            ReturningDetail detail = ReturningMapper.toDetailDomain(
                    rtrnMId, itmNo, asset.getDeptCd(), orgCd
            );

            returningRepository.saveDetail(detail);
        }
    }

    /**
     * 반납 신청 삭제
     */
    @Transactional
    public void deleteReturning(UUID rtrnMId, String orgCd) {
        ReturningMaster master = returningRepository.findMasterById(rtrnMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 반납 신청입니다."));

        // 정책 검증
        returningPolicy.validateOwnership(master, orgCd);
        returningPolicy.validateModifiable(master);

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

        // 정책 검증
        returningPolicy.validateOwnership(master, orgCd);
        returningPolicy.validateRequestable(master);

        // 도메인 로직 실행
        master.requestApproval();

        returningRepository.saveMaster(master);
    }

    /**
     * 승인 요청 취소
     */
    @Transactional
    public void cancelRequest(UUID rtrnMId, String orgCd) {
        ReturningMaster master = returningRepository.findMasterById(rtrnMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 반납 신청입니다."));

        // 정책 검증
        returningPolicy.validateOwnership(master, orgCd);
        returningPolicy.validateCancellable(master);

        returningRepository.deleteAllDetailsByMasterId(rtrnMId);
        returningRepository.deleteMaster(rtrnMId);
    }

    /**
     * TODO: 반납 승인 (ADMIN 권한)
     * - 승인 시 물품 상태를 RTN(반납)으로 변경
     * - 부서코드 NONE 값으로 변경
     * - 상태 이력 테이블에 기록 생성
     */

    /**
     * TODO: 반납 반려 (ADMIN 권한)
     */
}