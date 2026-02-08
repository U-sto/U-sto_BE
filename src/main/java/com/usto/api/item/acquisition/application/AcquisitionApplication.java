package com.usto.api.item.acquisition.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.g2b.infrastructure.entity.G2bItemJpaEntity;
import com.usto.api.g2b.infrastructure.repository.G2bItemJpaRepository;
import com.usto.api.item.acquisition.domain.model.Acquisition;
import com.usto.api.item.acquisition.domain.service.AcquisitionPolicy;
import com.usto.api.item.acquisition.infrastructure.mapper.AcquisitionMapper;
import com.usto.api.item.asset.application.AssetApplication;
import com.usto.api.item.returning.domain.model.ReturningMaster;
import com.usto.api.organization.infrastructure.repository.DepartmentJpaRepository;
import com.usto.api.item.acquisition.domain.repository.AcquisitionRepository;
import com.usto.api.item.acquisition.presentation.dto.request.AcqRegisterRequest;
import com.usto.api.item.acquisition.presentation.dto.request.AcqSearchRequest;
import com.usto.api.item.acquisition.presentation.dto.response.AcqListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class AcquisitionApplication {

    private final AcquisitionRepository acquisitionRepository;
    private final G2bItemJpaRepository g2bItemJpaRepository;
    private final DepartmentJpaRepository departmentJpaRepository;
    private final AssetApplication assetApplication;
    private final AcquisitionPolicy acquisitionPolicy;


    /**
     * 1. 취득 등록
     */
    @Transactional
    public UUID registerAcquisition(AcqRegisterRequest request, String userId, String orgCd) {
        // 검증
        G2bItemJpaEntity g2bItem = validateRequest(request, orgCd);

        // Domain Model 생성
        Acquisition acquisition = AcquisitionMapper.toDomain(
                request.getG2bDCd(),
                request.getAcqAt(),
                g2bItem.getG2bUpr(),
                request.getDeptCd(),
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
     * 2. 취득 수정
     */
    @Transactional
    public void updateAcquisition(UUID acqId, AcqRegisterRequest request, String orgCd) {
        // Domain 조회
        Acquisition acquisition = acquisitionRepository.findById(acqId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 취득 정보입니다."));

        // 정책 검증
        acquisitionPolicy.validateOwnership(acquisition, orgCd);
        acquisitionPolicy.validateModifiable(acquisition);

        // 검증
        G2bItemJpaEntity g2bItem = validateRequest(request, orgCd);

        // Domain 메서드 호출 (비즈니스 로직)
        acquisition.updateInfo(
                request.getG2bDCd(),
                request.getAcqAt(),
                g2bItem.getG2bUpr(),
                request.getDeptCd(),
                "5",
                request.getAcqQty(),
                request.getArrgTy(),
                request.getRmk()
        );

        // 저장
        acquisitionRepository.save(acquisition);
    }

    /**
     * 3. 취득 삭제
     */
    @Transactional
    public void deleteAcquisition(UUID acqId, String orgCd) {
        Acquisition acquisition = acquisitionRepository.findById(acqId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 취득 정보입니다."));

        // 정책 검증
        acquisitionPolicy.validateOwnership(acquisition, orgCd);
        acquisitionPolicy.validateModifiable(acquisition);

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

        // 정책 검증
        acquisitionPolicy.validateOwnership(acquisition, orgCd);
        acquisitionPolicy.validateRequestable(acquisition);

        // 도메인 로직 실행
        acquisition.requestApproval();

        // 저장
        acquisitionRepository.save(acquisition);
    }

    /**
     * 5. 승인 요청 취소 (소프트 삭제)
     */
    @Transactional
    public void cancelRequest(UUID acqId, String orgCd) {
        Acquisition acquisition = acquisitionRepository.findById(acqId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 취득 건입니다."));

        // 정책 검증
        acquisitionPolicy.validateOwnership(acquisition, orgCd);
        acquisitionPolicy.validateCancellable(acquisition);

        acquisitionRepository.delete(acqId);
    }


    /**
     * 승인 확정
     */
    @Transactional
    public void approvalAcquisition(List<UUID> acqIds, String userId, String orgCd) {

        List<Acquisition> acquisitions = acqIds.stream()
                .map(id -> acquisitionRepository.findMasterById(id, orgCd)
                        .orElseThrow(() -> new BusinessException("존재하지 않는 취득 신청입니다.")))
                .toList();

        for (Acquisition acquisition : acquisitions) {
            // 정책 검증
            acquisitionPolicy.validateOwnership(acquisition, orgCd);
            acquisitionPolicy.validateApprovable(acquisition);

            // 도메인 로직 실행
            acquisition.confirmApproval(userId);

            // 자산 등록
            assetApplication.registerAssetsFromAcquisition(acquisition);
        }
        acquisitionRepository.saveAll(acquisitions);
    }

    //취득 반려
    @Transactional
    public void rejectAcquisition(List<UUID> acqIds, String userId, String orgCd) {

        List<Acquisition> acquisitions = acqIds.stream()
                .map(id -> acquisitionRepository.findMasterById(id, orgCd)
                        .orElseThrow(() -> new BusinessException("존재하지 않는 취득 신청입니다.")))
                .toList();


        //상태 변경
        for (Acquisition acquisition : acquisitions) {
            // 도메인 로직 실행(반납 신청 반려처리 -> 저장)
            acquisition.rejectApproval(userId);
            acquisitionRepository.saveMaster(acquisition);
        }
    }

    /**
     * 목록 조회
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

        acquisitionPolicy.validateAcquisitionDate(request.getAcqAt());

        return g2bItem;
    }

}