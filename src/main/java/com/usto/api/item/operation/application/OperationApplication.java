package com.usto.api.item.operation.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.repository.AssetRepository;
import com.usto.api.item.asset.domain.service.AssetPolicy;
import com.usto.api.item.operation.domain.model.OperationDetail;
import com.usto.api.item.operation.domain.model.OperationMaster;
import com.usto.api.item.operation.domain.repository.OperationRepository;
import com.usto.api.item.operation.domain.service.OperationPolicy;
import com.usto.api.item.operation.infrastructure.mapper.OperationMapper;
import com.usto.api.item.operation.presentation.dto.request.OperationRegisterRequest;
import com.usto.api.item.operation.presentation.dto.request.OperationSearchRequest;
import com.usto.api.item.operation.presentation.dto.response.OperationItemListResponse;
import com.usto.api.item.operation.presentation.dto.response.OperationListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OperationApplication {

    private final OperationRepository operationRepository;
    private final OperationPolicy operationPolicy;
    private final AssetRepository assetRepository;
    private final AssetPolicy assetPolicy;

    /**
     * 운용 신청 등록
     * - 취득(ACQ) 또는 반납(RTN) 상태의 물품만 운용 신청 가능
     */
    @Transactional
    public UUID registerOperation(OperationRegisterRequest request, String userId, String orgCd) {
        // 운용일자 검증
        operationPolicy.validateAplyAt(request.getAplyAt());

        // 1. 운용 신청서(Master) 생성 (Mapper 사용)
        OperationMaster master = OperationMapper.toMasterDomain(
                userId,
                request.getAplyAt(),
                request.getDeptCd(),
                request.getItemSts(),
                orgCd
        );
        OperationMaster savedMaster = operationRepository.saveMaster(master);

        // 공통 로직 호출
        processOperationItems(request.getItmNos(), savedMaster.getOperMId(), null, request.getDeptCd(), orgCd);

        return savedMaster.getOperMId();
    }

    /**
     * 운용 목록 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<OperationListResponse> getOperationList(
            OperationSearchRequest searchRequest, String orgCd, Pageable pageable) {
        return operationRepository.findAllByFilter(searchRequest, orgCd, pageable);
    }

    /**
     * 운용물품목록 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<OperationItemListResponse> getOperationItems(UUID operMId, String orgCd, Pageable pageable) {
        operationRepository.findMasterById(operMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 운용 신청입니다."));

        return operationRepository.findItemsByMasterId(operMId, orgCd, pageable);
    }

    /**
     * 운용 신청 수정
     */
    @Transactional
    public void updateOperation(UUID operMId, OperationRegisterRequest request, String orgCd) {
        // 1. 마스터 조회
        OperationMaster master = operationRepository.findMasterById(operMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 운용 신청입니다."));

        // 2. 정책 검증
        operationPolicy.validateOwnership(master, orgCd);
        operationPolicy.validateModifiable(master);

        // 3. 마스터 정보 업데이트
        master.updateInfo(request.getDeptCd(), request.getItemSts());
        operationRepository.saveMaster(master);

        // 4. 기존 상세 삭제
        operationRepository.deleteAllDetailsByMasterId(operMId);

        // 5. 새로운 Detail 생성
        // 공통 로직 호출
        processOperationItems(request.getItmNos(), operMId, operMId, request.getDeptCd(), orgCd);
    }

    /**
     * 운용 신청 삭제
     */
    @Transactional
    public void deleteOperation(UUID operMId, String orgCd) {
        OperationMaster master = operationRepository.findMasterById(operMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 운용 신청입니다."));

        // 정책 검증
        operationPolicy.validateOwnership(master, orgCd);
        operationPolicy.validateModifiable(master);

        operationRepository.deleteAllDetailsByMasterId(operMId);
        operationRepository.deleteMaster(operMId);
    }

    /**
     * 승인 요청
     */
    @Transactional
    public void requestApproval(UUID operMId, String orgCd) {
        OperationMaster master = operationRepository.findMasterById(operMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 운용 신청입니다."));

        // 정책 검증
        operationPolicy.validateOwnership(master, orgCd);
        operationPolicy.validateRequestable(master);

        // 도메인 로직 실행
        master.requestApproval();

        operationRepository.saveMaster(master);
    }

    /**
     * 승인 요청 취소
     */
    @Transactional
    public void cancelRequest(UUID operMId, String orgCd) {
        OperationMaster master = operationRepository.findMasterById(operMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 운용 신청입니다."));

        // 정책 검증
        operationPolicy.validateOwnership(master, orgCd);
        operationPolicy.validateCancellable(master);

        operationRepository.deleteAllDetailsByMasterId(operMId);
        operationRepository.deleteMaster(operMId);
    }

    /**
     * 물품 검증 및 상세 생성 공통 로직
     *
     * @param itmNos 물품 번호 목록
     * @param operMId 운용 Master ID
     * @param excludeOperMId 중복 체크 시 제외할 Master ID (수정 시)
     * @param orgCd 조직코드
     */
    private void processOperationItems(
            List<String> itmNos, UUID operMId, UUID excludeOperMId, String deptCd, String orgCd
    ) {
        // Batch 조회 - 모든 물품 한 번에 조회 (1번의 IN 쿼리)
        List<Asset> assets = assetRepository.findAllById(itmNos, orgCd);

        // 조회된 물품 개수 검증
        if (assets.size() != itmNos.size()) {
            throw new BusinessException("일부 물품을 찾을 수 없습니다.");
        }

        // Batch 중복 체크 (1번의 IN 쿼리)
        List<String> duplicated = operationRepository.findDuplicatedItems(
                itmNos, excludeOperMId, orgCd
        );
        if (!duplicated.isEmpty()) {
            throw new BusinessException(
                    "이미 다른 운용 신청에 포함된 물품입니다: " + String.join(", ", duplicated)
            );
        }

        // 검증 및 Detail 생성 (메모리 연산)
        List<OperationDetail> details = new ArrayList<>();
        for (Asset asset : assets) {
            assetPolicy.validateUpdate(asset, orgCd);
            assetPolicy.validateOperation(asset, deptCd);

            details.add(OperationMapper.toDetailDomain(operMId, asset.getItmNo(), orgCd));
        }

        // 한 번에 저장 (1번의 Batch INSERT)
        operationRepository.saveAllDetails(details);
    }

    /**
     * TODO: 운용 승인 및 반려 (ADMIN 권한)
     * - 승인 시 물품 상태를 OPER로 변경
     * - 각 대장 상세의 운용부서코드 변경
     * - 상태 이력 테이블에 기록
     */
}