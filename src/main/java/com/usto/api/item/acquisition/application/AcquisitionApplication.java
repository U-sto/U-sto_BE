package com.usto.api.item.acquisition.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.g2b.infrastructure.entity.G2bItemJpaEntity;
import com.usto.api.g2b.infrastructure.repository.G2bItemJpaRepository;
import com.usto.api.item.acquisition.domain.model.Acquisition;
import com.usto.api.item.asset.application.AssetService;
import com.usto.api.item.asset.domain.repository.AssetRepository;
import com.usto.api.item.common.model.OperStatus;
import com.usto.api.organization.infrastructure.repository.DepartmentJpaRepository;
import com.usto.api.item.acquisition.domain.repository.AcquisitionRepository;
import com.usto.api.item.acquisition.presentation.dto.request.AcqRegisterRequest;
import com.usto.api.item.acquisition.presentation.dto.request.AcqSearchRequest;
import com.usto.api.item.acquisition.presentation.dto.response.AcqListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class AcquisitionApplication {

    private final AcquisitionRepository acquisitionRepository;
    private final G2bItemJpaRepository g2bItemJpaRepository;
    private final DepartmentJpaRepository departmentJpaRepository;

    private final AssetRepository assetRepository;
    private final AssetService assetService;

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    /**
     * 1. 등록 (Domain Model 사용)
     */
    @Transactional
    public UUID registerAcquisition(AcqRegisterRequest request, String userId, String orgCd) {
        // 검증
        G2bItemJpaEntity g2bItem = validateRequest(request, orgCd);

        // Domain Model 생성 (UUID 자동 생성)
        Acquisition acquisition = Acquisition.create(
                request.getG2bDCd(),
                request.getAcqAt(),
                g2bItem.getG2bUpr(),
                request.getDeptCd(),
                OperStatus.ACQ,
                "5",  // TODO: 내용연수 로직
                request.getAcqQty(),
                request.getArrgTy(),
                request.getRmk(),
                userId,
                orgCd
        );

        // 저장
        Acquisition saved = acquisitionRepository.save(acquisition);
        return saved.getAcqId();
    }

    /**
     * 2. 수정
     */
    @Transactional
    public void updateAcquisition(UUID acqId, AcqRegisterRequest request, String orgCd) {
        // Domain 조회
        Acquisition acquisition = acquisitionRepository.findById(acqId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 취득 정보입니다."));

        acquisition.validateOwnership(orgCd);

        // 검증
        G2bItemJpaEntity g2bItem = validateRequest(request, orgCd);

        // Domain 메서드 호출 (비즈니스 로직은 Domain에서)
        acquisition.updateInfo(
                request.getG2bDCd(),
                request.getAcqAt(),
                g2bItem.getG2bUpr(),
                request.getDeptCd(),
                acquisition.getOperSts(),
                "5",
                request.getAcqQty(),
                request.getArrgTy(),
                request.getRmk()
        );

        // 저장
        acquisitionRepository.save(acquisition);
    }

    /**
     * 3. 삭제
     */
    @Transactional
    public void deleteAcquisition(UUID acqId, String orgCd) {
        Acquisition acquisition = acquisitionRepository.findById(acqId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 취득 정보입니다."));

        acquisition.validateOwnership(orgCd);

        // 수정 가능 여부 체크 (Domain 메서드 사용)
        acquisition.validateModifiable();

        // 소프트 삭제
        acquisitionRepository.delete(acqId);
    }

    /**
     * 4. 승인 요청
     */
    @Transactional
    public void requestApproval(UUID acqId, String orgCd) {
        Acquisition acquisition = acquisitionRepository.findById(acqId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 취득 건입니다."));

        acquisition.validateOwnership(orgCd);

        // Domain 메서드 호출 (비즈니스 로직)
        acquisition.requestApproval();

        // 저장
        acquisitionRepository.save(acquisition);
    }

    /**
     * 5. 승인 요청 취소 → 소프트 삭제
     */
    @Transactional
    public void cancelRequest(UUID acqId, String orgCd) {
        Acquisition acquisition = acquisitionRepository.findById(acqId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 취득 건입니다."));

        acquisition.validateOwnership(orgCd);

        // 취소 가능 여부 체크 (Domain 메서드)
        acquisition.validateCancellable();

        // 상태 변경이 아닌 소프트 삭제
        acquisitionRepository.delete(acqId);
    }

    /**
     * 6. 목록 조회 (변경 없음)
     */
    @Transactional(readOnly = true)
    public List<AcqListResponse> getAcquisitionList(AcqSearchRequest searchRequest, String orgCd) {
        return acquisitionRepository.findAllByFilter(searchRequest, orgCd);
    }

    /**
     * 공통 검증 로직
     */
    private G2bItemJpaEntity validateRequest(AcqRegisterRequest request, String orgCd) {
        G2bItemJpaEntity g2bItem = g2bItemJpaRepository.findById(request.getG2bDCd())
                .orElseThrow(() -> new BusinessException("존재하지 않는 G2B 식별코드입니다."));

        if (!departmentJpaRepository.existsById_OrgCdAndId_DeptCd(orgCd, request.getDeptCd())) {
            throw new BusinessException("해당 조직에 존재하지 않는 부서코드입니다.");
        }

        if (request.getAcqAt().isAfter(LocalDate.now(KOREA_ZONE))) {
            throw new BusinessException("취득일자는 현재 날짜 이후일 수 없습니다.");
        }

        return g2bItem;
    }

    @Transactional
    public void ApprovalAcquisition(List<UUID> acqIds, String userId, String orgCd) {

        List<Acquisition> acquisitions = acquisitionRepository.findAllById(acqIds);

        //갯수 안 맞다? 문제 있는거
        if (acquisitions.size() != acqIds.size()) {
            throw new BusinessException("요청한 항목 중 존재하지 않는 취득 정보가 포함되어 있습니다.");
        }

        for (Acquisition acquisition : acquisitions) {
            acquisition.validateOwnership(orgCd);
            acquisition.confirmApproval(userId);
            assetService.registerAssetsFromAcquisition(acquisition);
        }
        acquisitionRepository.saveAll(acquisitions);
    }
}