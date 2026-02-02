package com.usto.api.item.disuse.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.repository.AssetRepository;
import com.usto.api.item.asset.domain.service.AssetPolicy;
import com.usto.api.item.common.model.OperStatus;
import com.usto.api.item.disuse.domain.model.DisuseDetail;
import com.usto.api.item.disuse.domain.model.DisuseMaster;
import com.usto.api.item.disuse.domain.repository.DisuseRepository;
import com.usto.api.item.disuse.domain.service.DisusePolicy;
import com.usto.api.item.disuse.infrastructure.mapper.DisuseMapper;
import com.usto.api.item.disuse.presentation.dto.request.DisuseRegisterRequest;
import com.usto.api.item.disuse.presentation.dto.request.DisuseSearchRequest;
import com.usto.api.item.disuse.presentation.dto.response.DisuseItemListResponse;
import com.usto.api.item.disuse.presentation.dto.response.DisuseListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DisuseApplication {

    private final DisuseRepository disuseRepository;
    private final DisusePolicy disusePolicy;
    private final AssetRepository assetRepository;
    private final AssetPolicy assetPolicy;

    /**
     * 불용 신청 등록
     * - 반납(RTN) 상태의 물품만 불용 신청 가능
     */
    @Transactional
    public UUID registerDisuse(DisuseRegisterRequest request, String userId, String orgCd) {
        // 불용일자 검증
        disusePolicy.validateAplyAt(request.getAplyAt());

        // 1. 불용 신청서(Master) 생성 (Mapper 사용)
        DisuseMaster master = DisuseMapper.toMasterDomain(
                userId,
                request.getAplyAt(),
                request.getItemSts(),
                request.getDsuRsn(),
                orgCd
        );

        DisuseMaster savedMaster = disuseRepository.saveMaster(master);

        // 2. 각 물품 검증 및 불용 상세(Detail) 생성
        for (String itmNo : request.getItmNos()) {
            // 물품 조회 및 검증
            Asset asset = assetRepository.findById(itmNo, orgCd)
                    .orElseThrow(() -> new BusinessException("물품을 찾을 수 없습니다: " + itmNo));

            assetPolicy.validateUpdate(asset, orgCd);

            // 중복 체크
            if (disuseRepository.existsInOtherDisuse(itmNo, null, orgCd)) {
                throw new BusinessException("이미 다른 불용 신청에 포함된 물품입니다: " + itmNo);
            }

            // 반납 상태 확인 (RTN만 불용 가능)
            if (asset.getOperSts() != OperStatus.RTN) {
                throw new BusinessException("반납(RTN) 상태의 물품만 불용 신청할 수 있습니다: " + itmNo);
            }

            // 불용 상세 생성 (Mapper 사용)
            DisuseDetail detail = DisuseMapper.toDetailDomain(
                    savedMaster.getDsuMId(),
                    itmNo,
                    asset.getDeptCd(),
                    orgCd
            );

            disuseRepository.saveDetail(detail);
        }

        return savedMaster.getDsuMId();
    }

    /**
     * 불용 목록 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<DisuseListResponse> getDisuseList(
            DisuseSearchRequest searchRequest, String orgCd, Pageable pageable) {
        return disuseRepository.findAllByFilter(searchRequest, orgCd, pageable);
    }

    /**
     * 불용물품목록 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<DisuseItemListResponse> getDisuseItems(UUID dsuMId, String orgCd, Pageable pageable) {
        disuseRepository.findMasterById(dsuMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 불용 신청입니다."));

        return disuseRepository.findItemsByMasterId(dsuMId, orgCd, pageable);
    }

    /**
     * 불용 신청 수정
     */
    @Transactional
    public void updateDisuse(UUID dsuMId, DisuseRegisterRequest request, String orgCd) {
        // 1. 마스터 조회
        DisuseMaster master = disuseRepository.findMasterById(dsuMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 불용 신청입니다."));

        // 2. 정책 검증
        disusePolicy.validateOwnership(master, orgCd);
        disusePolicy.validateModifiable(master);

        // 3. 마스터 정보 업데이트
        master.updateInfo(request.getItemSts(), request.getDsuRsn());
        disuseRepository.saveMaster(master);

        // 4. 기존 상세 삭제
        disuseRepository.deleteAllDetailsByMasterId(dsuMId);

        // 5. 새로운 Detail 생성
        for (String itmNo : request.getItmNos()) {
            Asset asset = assetRepository.findById(itmNo, orgCd)
                    .orElseThrow(() -> new BusinessException("물품을 찾을 수 없습니다: " + itmNo));

            assetPolicy.validateUpdate(asset, orgCd);

            if (asset.getOperSts() != OperStatus.RTN) {
                throw new BusinessException("반납(RTN) 상태의 물품만 불용 신청할 수 있습니다: " + itmNo);
            }

            if (disuseRepository.existsInOtherDisuse(itmNo, dsuMId, orgCd)) {
                throw new BusinessException("이미 다른 불용 신청에 포함된 물품입니다: " + itmNo);
            }

            DisuseDetail detail = DisuseMapper.toDetailDomain(
                    dsuMId, itmNo, asset.getDeptCd(), orgCd
            );

            disuseRepository.saveDetail(detail);
        }
    }

    /**
     * 불용 신청 삭제
     */
    @Transactional
    public void deleteDisuse(UUID dsuMId, String orgCd) {
        DisuseMaster master = disuseRepository.findMasterById(dsuMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 불용 신청입니다."));

        // 정책 검증
        disusePolicy.validateOwnership(master, orgCd);
        disusePolicy.validateModifiable(master);

        disuseRepository.deleteAllDetailsByMasterId(dsuMId);
        disuseRepository.deleteMaster(dsuMId);
    }

    /**
     * 승인 요청
     */
    @Transactional
    public void requestApproval(UUID dsuMId, String orgCd) {
        DisuseMaster master = disuseRepository.findMasterById(dsuMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 불용 신청입니다."));

        // 정책 검증
        disusePolicy.validateOwnership(master, orgCd);
        disusePolicy.validateRequestable(master);

        // 도메인 로직 실행
        master.requestApproval();

        disuseRepository.saveMaster(master);
    }

    /**
     * 승인 요청 취소
     */
    @Transactional
    public void cancelRequest(UUID dsuMId, String orgCd) {
        DisuseMaster master = disuseRepository.findMasterById(dsuMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 불용 신청입니다."));

        // 정책 검증
        disusePolicy.validateOwnership(master, orgCd);
        disusePolicy.validateCancellable(master);

        disuseRepository.deleteAllDetailsByMasterId(dsuMId);
        disuseRepository.deleteMaster(dsuMId);
    }

    /**
     * TODO: 불용 승인 (ADMIN 권한)
     * - 승인 시 물품 상태를 RTN → DSU로 변경
     * - 이후 해당 자산은 수정 불가하도록 잠금
     * - 상태 이력 테이블에 기록
     */

    /**
     * TODO: 불용 반려 (ADMIN 권한)
     */
}