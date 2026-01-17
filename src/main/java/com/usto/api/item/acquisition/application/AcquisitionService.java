package com.usto.api.item.acquisition.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.g2b.infrastructure.entity.G2bItemJpaEntity;
import com.usto.api.g2b.infrastructure.repository.G2bItemJpaRepository;
import com.usto.api.item.common.model.OperStatus;
import com.usto.api.organization.infrastructure.repository.DepartmentJpaRepository;
import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.acquisition.domain.repository.AcquisitionRepository;
import com.usto.api.item.acquisition.infrastructure.entity.ItemAcquisitionEntity;
import com.usto.api.item.acquisition.presentation.dto.request.AcqRegisterRequest;
import com.usto.api.item.acquisition.presentation.dto.request.AcqSearchRequest;
import com.usto.api.item.acquisition.presentation.dto.response.AcqListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AcquisitionService {

    private final AcquisitionRepository acquisitionRepository;
    private final G2bItemJpaRepository g2bItemJpaRepository;
    private final DepartmentJpaRepository departmentJpaRepository;

    /**
     * 1. 등록 (기존 로직 유지 + 검증 분리)
     */
    @Transactional
    public Long registerAcquisition(AcqRegisterRequest request, String userId, String orgCd) {
        G2bItemJpaEntity g2bItem = validateRequest(request, orgCd);

        ItemAcquisitionEntity entity = ItemAcquisitionEntity.builder()
                .g2bDCd(request.getG2bDCd())
                .acqAt(request.getAcqAt())
                .acqUpr(g2bItem.getG2bUpr())
                .drbYr("5") // TODO: 내용연수 로직
                .deptCd(request.getDeptCd())
                .operSts(request.getOperSts())
                .acqQty(request.getAcqQty())
                .arrgTy(request.getArrgTy())
                .apprSts(ApprStatus.WAIT)
                .aplyUsrId(userId)
                .orgCd(orgCd)
                .delYn("N")
                .build();

        return acquisitionRepository.save(entity).getAcqId();
    }

    /**
     * 2. 수정 (추가)
     */
    @Transactional
    public void updateAcquisition(Long acqId, AcqRegisterRequest request, String orgCd) {
        ItemAcquisitionEntity entity = acquisitionRepository.findById(acqId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 취득 정보입니다."));

        // 상태 체크: 대기(WAIT) 또는 반려(REJECTED) 상태일 때만 수정 가능
        if (entity.getApprSts() == ApprStatus.REQUEST || entity.getApprSts() == ApprStatus.APPROVED) {
            throw new BusinessException("승인 요청 중이거나 확정된 데이터는 수정할 수 없습니다.");
        }

        G2bItemJpaEntity g2bItem = validateRequest(request, orgCd);
        entity.updateInfo(request, g2bItem.getG2bUpr());
    }

    /**
     * 3. 삭제 (추가)
     */
    @Transactional
    public void deleteAcquisition(Long acqId) {
        ItemAcquisitionEntity entity = acquisitionRepository.findById(acqId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 취득 정보입니다."));

        // 상태 체크: 수정과 동일
        if (entity.getApprSts() == ApprStatus.REQUEST || entity.getApprSts() == ApprStatus.APPROVED) {
            throw new BusinessException("승인 요청 중이거나 확정된 데이터는 삭제할 수 없습니다.");
        }

        acquisitionRepository.delete(entity); // Soft Delete
    }

    /**
     * 4. 승인 요청 / 취소 (기존 사용자님 코드 유지)
     */
    @Transactional
    public void requestApproval(Long acqId) {
        ItemAcquisitionEntity acq = acquisitionRepository.findById(acqId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 취득 건입니다."));
        if (acq.getApprSts() != ApprStatus.WAIT && acq.getApprSts() != ApprStatus.REJECTED) {
            throw new BusinessException("승인요청이 가능한 상태가 아닙니다.");
        }
        acq.changeStatus(ApprStatus.REQUEST);
    }

    @Transactional
    public void cancelRequest(Long acqId) {
        ItemAcquisitionEntity acq = acquisitionRepository.findById(acqId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 취득 건입니다."));
        if (acq.getApprSts() != ApprStatus.REQUEST) {
            throw new BusinessException("승인요청 중인 상태만 취소할 수 있습니다.");
        }
        acq.changeStatus(ApprStatus.WAIT);
    }

    @Transactional(readOnly = true)
    public List<AcqListResponse> getAcquisitionList(AcqSearchRequest searchRequest, String orgCd) {
        return acquisitionRepository.findAllByFilter(searchRequest, orgCd);
    }

    // 공통 검증 로직 (안정성 확보)
    private G2bItemJpaEntity validateRequest(AcqRegisterRequest request, String orgCd) {
        G2bItemJpaEntity g2bItem = g2bItemJpaRepository.findById(request.getG2bDCd())
                .orElseThrow(() -> new BusinessException("존재하지 않는 G2B 식별코드입니다."));

        if (!departmentJpaRepository.existsById_OrgCdAndId_DeptCd(orgCd, request.getDeptCd())) {
            throw new BusinessException("해당 조직에 존재하지 않는 부서코드입니다.");
        }

        if (request.getAcqAt().isAfter(LocalDate.now())) {
            throw new BusinessException("취득일자는 현재 날짜 이후일 수 없습니다.");
        }

        if (request.getOperSts() != OperStatus.ACQ && request.getOperSts() != OperStatus.OPER) {
            throw new BusinessException("취득 등록 시 운용상태는 '취득' 또는 '운용'만 가능합니다.");
        }
        return g2bItem;
    }
}