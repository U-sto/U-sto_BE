package com.usto.api.item.disuse.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.model.AssetStatusHistory;
import com.usto.api.item.asset.domain.repository.AssetRepository;
import com.usto.api.item.asset.domain.repository.AssetStatusHistoryRepository;
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
import com.usto.api.item.returning.domain.model.ReturningDetail;
import com.usto.api.item.returning.domain.model.ReturningMaster;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DisuseApplication {

    private final DisuseRepository disuseRepository;
    private final DisusePolicy disusePolicy;
    private final AssetRepository assetRepository;
    private final AssetPolicy assetPolicy;
    private final AssetStatusHistoryRepository historyRepository;

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

        // 공통 로직 호출
        processDisuseItems(request.getItmNos(), savedMaster.getDsuMId(), null, orgCd);

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
        // 공통 로직 호출
        processDisuseItems(request.getItmNos(), dsuMId, dsuMId, orgCd);
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
     * 물품 검증 및 상세 생성 공통 로직
     *
     * @param itmNos 물품 번호 목록
     * @param dsuMId 불용 Master ID
     * @param excludeDsuMId 중복 체크 시 제외할 Master ID (수정 시)
     * @param orgCd 조직코드
     */
    private void processDisuseItems(
            List<String> itmNos, UUID dsuMId, UUID excludeDsuMId, String orgCd
    ) {
        // Batch 조회 - 모든 물품 한 번에 조회 (1번의 IN 쿼리)
        List<Asset> assets = assetRepository.findAllById(itmNos, orgCd);

        // 조회된 물품 개수 검증
        if (assets.size() != itmNos.size()) {
            throw new BusinessException("일부 물품을 찾을 수 없습니다.");
        }

        // Batch 중복 체크 (1번의 IN 쿼리)
        List<String> duplicated = disuseRepository.findDuplicatedItems(
                itmNos, excludeDsuMId, orgCd
        );
        if (!duplicated.isEmpty()) {
            throw new BusinessException(
                    "이미 다른 불용 신청에 포함된 물품입니다: " + String.join(", ", duplicated)
            );
        }

        // 검증 및 Detail 생성 (메모리 연산)
        List<DisuseDetail> details = new ArrayList<>();
        for (Asset asset : assets) {
            assetPolicy.validateUpdate(asset, orgCd);
            assetPolicy.validateDisuse(asset);

            details.add(DisuseMapper.toDetailDomain(
                    dsuMId, asset.getItmNo(), asset.getDeptCd(), orgCd
            ));
        }

        // 한 번에 저장 (1번의 Batch INSERT)
        disuseRepository.saveAllDetails(details);
    }


    /**
     * TODO: 불용 승인 (ADMIN 권한)
     * - 승인 시 물품 상태를 RTN → DSU로 변경
     * - 이후 해당 자산은 수정 불가하도록 잠금
     * - 상태 이력 테이블에 기록
     */
    @Transactional
    public void approvalDisuse(UUID dsuMId, String userId, String orgCd) {

        DisuseMaster master = disuseRepository.findMasterById(dsuMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 불용 신청입니다."));

        disusePolicy.validateOwnership(master,orgCd);
        disusePolicy.validateConfirm(master);

        List<DisuseDetail> details = disuseRepository.findDetailsByMasterId(dsuMId, orgCd);
        if (details.isEmpty()) {
            throw new BusinessException("반납 상세 정보가 없습니다.");
        }

        List<Asset> assetsToUpdate = new ArrayList<>(details.size());
        List<AssetStatusHistory> histories = new ArrayList<>(details.size());

        for (DisuseDetail detail : details) {
            String itemNo = detail.getItmNo();
            Asset asset = assetRepository.findAssetById(itemNo, orgCd);
            if (asset == null) {
                throw new BusinessException("해당 물품 정보를 대장에서 찾을 수 없습니다: " + itemNo);
            }

            OperStatus prevStatus = asset.getOperSts();
            asset.disuseAsset();
            assetsToUpdate.add(asset);
                //물품 히스토리 저장 로직 실행
            histories.add(
                    AssetStatusHistory.builder()
                    .itemHisId(UUID.randomUUID())
                    .itmNo(itemNo)
                    .prevSts(prevStatus) //이전 상태
                    .newSts(asset.getOperSts()) //현재 상태 = 반납
                    .chgRsn("불용 신청 승인") //별도로 enum 관리를 하고싶다면 변동 가능성 있음.
                    .reqUsrId(master.getAplyUsrId())
                    .reqAt(master.getAplyAt())
                    .apprUsrId(userId)
                    .apprAt(LocalDate.now(ZoneId.of("Asia/Seoul")))
                    .orgCd(orgCd)
                    .delAt(asset.getDelAt())
                    .delYn(asset.getDelYn())
                    .build());
        }
        assetRepository.saveAll(assetsToUpdate);
        historyRepository.saveAll(histories);
        master.confirmApproval(userId);
        // 마스터 저장
        disuseRepository.saveMaster(master);
    }


    /**
     * TODO: 불용 반려 (ADMIN 권한)
     * 다른 신청들과 비슷하게 소프트 삭제 진행
     */
    @Transactional
    public void rejectDisuse(UUID dsuMId, String userId, String orgCd) {

        DisuseMaster master = disuseRepository.findMasterById(dsuMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 불용 신청입니다."));
        //정책 확인
        disusePolicy.validateConfirm(master);

        //소프트 삭제 전 상태 변경(반납 신청 반려처리 -> 저장)
        master.rejectApproval(userId);
        disuseRepository.saveMaster(master);
    }
}