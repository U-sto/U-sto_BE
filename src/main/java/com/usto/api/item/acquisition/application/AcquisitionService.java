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

    @Transactional
    public Long registerAcquisition(AcqRegisterRequest request, String userId, String orgCd) {
        // 1. G2B 식별코드 존재 여부 체크
        G2bItemJpaEntity g2bItem = g2bItemJpaRepository.findById(request.getG2bDCd())
                .orElseThrow(() -> new BusinessException("존재하지 않는 G2B 식별코드입니다."));

        // 2. 운용부서 존재 여부 체크 (조직코드와 부서코드 쌍으로 확인)
        if (!departmentJpaRepository.existsById_OrgCdAndId_DeptCd(orgCd, request.getDeptCd())) {
            throw new BusinessException("해당 조직에 존재하지 않는 부서코드입니다.");
        }

        // 3. 취득일자 검증 (미래 날짜 불가)
        if (request.getAcqAt().isAfter(LocalDate.now())) {
            throw new BusinessException("취득일자는 현재 날짜 이후일 수 없습니다.");
        }

        // 4. 취득 등록 시 운용상태 제한 (ACQ 또는 OPER만 가능)
        if (request.getOperSts() != OperStatus.ACQ && request.getOperSts() != OperStatus.OPER) {
            throw new BusinessException("취득 등록 시 운용상태는 '취득' 또는 '운용'만 가능합니다.");
        }

        ItemAcquisitionEntity entity = ItemAcquisitionEntity.builder()
                .g2bDCd(request.getG2bDCd())   // 물품식별코드
                .acqAt(request.getAcqAt())     // 취득일자
                .acqUpr(g2bItem.getG2bUpr())   // G2B에서 가져온 단가 (Read-Only)
                // .drbYr(g2bItem.getDrbYr())  // G2B에서 가져온 내용연수 (Read-Only)
                .drbYr("5")  // TODO: '자산분류별 표준 내용연수' 찾으면 수정 필요
                .deptCd(request.getDeptCd())   // 운용부서코드
                .operSts(request.getOperSts()) // 운용상태
                .acqQty(request.getAcqQty())   // 취득수량
                .arrgTy(request.getArrgTy())   // 정리구분
                .apprSts(ApprStatus.WAIT)      // 승인상태(기본: 대기)
                .rmk(request.getRmk())         // 비고
                .aplyUsrId(userId)             // 등록자ID
                .orgCd(orgCd)                  // 조직코드
                .delYn("N")
                .build();

        return acquisitionRepository.save(entity).getAcqId();
    }

    @Transactional(readOnly = true)
    public List<AcqListResponse> getAcquisitionList(AcqSearchRequest searchRequest, String orgCd) {
        return acquisitionRepository.findAllByFilter(searchRequest, orgCd);
    }
}