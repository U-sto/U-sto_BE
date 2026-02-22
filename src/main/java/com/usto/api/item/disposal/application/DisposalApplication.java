package com.usto.api.item.disposal.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.model.AssetStatusHistory;
import com.usto.api.item.asset.domain.repository.AssetRepository;
import com.usto.api.item.asset.domain.repository.AssetStatusHistoryRepository;
import com.usto.api.item.common.model.OperStatus;
import com.usto.api.item.disposal.domain.model.DisposalDetail;
import com.usto.api.item.disposal.domain.model.DisposalMaster;
import com.usto.api.item.disposal.domain.repository.DisposalRepository;
import com.usto.api.item.disposal.domain.service.DisposalPolicy;
import com.usto.api.item.disposal.infrastructure.mapper.DisposalMapper;
import com.usto.api.item.disposal.presentation.dto.request.DisposalRegisterRequest;
import com.usto.api.item.disposal.presentation.dto.request.DisposalSearchRequest;
import com.usto.api.item.disposal.presentation.dto.response.DisposalItemListResponse;
import com.usto.api.item.disposal.presentation.dto.response.DisposalListResponse;
import com.usto.api.item.disuse.domain.model.DisuseMaster;
import com.usto.api.item.disuse.domain.repository.DisuseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DisposalApplication {

    private final DisposalRepository disposalRepository;
    private final DisposalPolicy disposalPolicy;
    private final AssetRepository assetRepository;
    private final DisuseRepository disuseRepository;  // 불용 정보 조회용
    private final AssetStatusHistoryRepository historyRepository;

    /**
     * 처분 신청 등록
     * - 불용(DSU) 상태의 물품만 처분 신청 가능
     * - 불용 테이블에서 물품상태와 사유를 가져옴
     */
    @Transactional
    public UUID registerDisposal(DisposalRegisterRequest request, String userId, String orgCd) {
        // 처분일자 검증
        disposalPolicy.validateAplyAt(request.getAplyAt());

        // 1. 처분 신청서(Master) 생성
        DisposalMaster master = DisposalMapper.toMasterDomain(
                userId,
                request.getDispType(),
                request.getAplyAt(),
                orgCd
        );
        DisposalMaster savedMaster = disposalRepository.saveMaster(master);

        // 공통 로직 호출
        processDisposalItems(request.getItmNos(), savedMaster.getDispMId(), null, orgCd);

        return savedMaster.getDispMId();
    }

    /**
     * 처분 목록 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<DisposalListResponse> getDisposalList(
            DisposalSearchRequest searchRequest, String orgCd, Pageable pageable) {
        return disposalRepository.findAllByFilter(searchRequest, orgCd, pageable);
    }

    /**
     * 처분물품목록 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<DisposalItemListResponse> getDisposalItems(UUID dispMId, String orgCd, Pageable pageable) {
        disposalRepository.findMasterById(dispMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 처분 신청입니다."));

        return disposalRepository.findItemsByMasterId(dispMId, orgCd, pageable);
    }

    /**
     * 처분 신청 수정
     */
    @Transactional
    public void updateDisposal(UUID dispMId, DisposalRegisterRequest request, String orgCd) {
        // 1. 마스터 조회
        DisposalMaster master = disposalRepository.findMasterById(dispMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 처분 신청입니다."));

        // 2. 정책 검증
        disposalPolicy.validateOwnership(master, orgCd);
        disposalPolicy.validateModifiable(master);

        // 3. 마스터 정보 업데이트
        master.updateInfo(request.getDispType(), request.getAplyAt());
        disposalRepository.saveMaster(master);

        // 4. 기존 상세 삭제
        disposalRepository.deleteAllDetailsByMasterId(dispMId);

        // 5. 새로운 Detail 생성
        processDisposalItems(request.getItmNos(), dispMId, dispMId, orgCd);
    }

    /**
     * 처분 신청 삭제
     */
    @Transactional
    public void deleteDisposal(UUID dispMId, String orgCd) {
        DisposalMaster master = disposalRepository.findMasterById(dispMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 처분 신청입니다."));

        // 정책 검증
        disposalPolicy.validateOwnership(master, orgCd);
        disposalPolicy.validateModifiable(master);

        disposalRepository.deleteAllDetailsByMasterId(dispMId);
        disposalRepository.deleteMaster(dispMId);
    }

    /**
     * 승인 요청
     */
    @Transactional
    public void requestApproval(UUID dispMId, String orgCd) {
        DisposalMaster master = disposalRepository.findMasterById(dispMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 처분 신청입니다."));

        // 정책 검증
        disposalPolicy.validateOwnership(master, orgCd);
        disposalPolicy.validateRequestable(master);

        // 도메인 로직 실행
        master.requestApproval();

        disposalRepository.saveMaster(master);
    }

    /**
     * 승인 요청 취소
     */
    @Transactional
    public void cancelRequest(UUID dispMId, String orgCd) {
        DisposalMaster master = disposalRepository.findMasterById(dispMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 처분 신청입니다."));

        // 정책 검증
        disposalPolicy.validateOwnership(master, orgCd);
        disposalPolicy.validateCancellable(master);

        disposalRepository.deleteAllDetailsByMasterId(dispMId);
        disposalRepository.deleteMaster(dispMId);
    }

    /**
     * 물품 검증 및 상세 생성 공통 로직
     * - 불용(DSU) 상태만 처분 가능
     * - 불용 테이블에서 물품상태와 사유를 가져옴
     *
     * @param itmNos 물품 번호 목록
     * @param dispMId 처분 Master ID
     * @param excludeDispMId 중복 체크 시 제외할 Master ID (수정 시)
     * @param orgCd 조직코드
     */
    private void processDisposalItems(
            List<String> itmNos, UUID dispMId, UUID excludeDispMId, String orgCd
    ) {
        // Batch 조회 - 모든 물품 한 번에 조회
        List<Asset> assets = assetRepository.findAllById(itmNos, orgCd);

        // 조회된 물품 개수 검증
        if (assets.size() != itmNos.size()) {
            throw new BusinessException("일부 물품을 찾을 수 없습니다.");
        }

        // Batch 중복 체크
        List<String> duplicated = disposalRepository.findDuplicatedItems(
                itmNos, excludeDispMId, orgCd
        );
        if (!duplicated.isEmpty()) {
            throw new BusinessException(
                    "이미 다른 처분 신청에 포함된 물품입니다: " + String.join(", ", duplicated)
            );
        }

        // 불용(DSU) 상태만 처분 가능
        for (Asset asset : assets) {
            if (asset.getOperSts() != OperStatus.DSU) {
                throw new BusinessException(
                        "불용(DSU) 상태의 물품만 처분 신청할 수 있습니다: " + asset.getItmNo()
                );
            }
        }

        // 불용 테이블에서 물품상태와 사유 조회 (Batch)
        List<String> assetItmNos = assets.stream()
                .map(Asset::getItmNo)
                .toList();

        // 리포지토리에서 승인된 불용 정보를 한 번에 가져옴 (N+1 방지)
        Map<String, DisuseMaster> disuseMap = disuseRepository.findApprovedMastersByItmNos(assetItmNos, orgCd);

        // 검증 및 Detail 생성
        List<DisposalDetail> details = new ArrayList<>();
        for (Asset asset : assets) {
            DisuseMaster disuseMaster = disuseMap.get(asset.getItmNo());
            if (disuseMaster == null) {
                throw new BusinessException(
                        "물품 " + asset.getItmNo() + "의 승인된 불용 정보를 찾을 수 없습니다. "
                );
            }

            // 불용 테이블의 물품상태와 사유를 가져와서 처분 상세 생성
            details.add(DisposalMapper.toDetailDomain(
                    dispMId,
                    asset.getItmNo(),
                    disuseMaster.getItemSts(),    // 불용 테이블의 물품상태
                    disuseMaster.getDsuRsn(),     // 불용 테이블의 사유
                    orgCd
            ));
        }

        // 한 번에 저장
        disposalRepository.saveAllDetails(details);
    }


    /**
     * 처분 승인 (ADMIN)
     * - 승인 시 물품 소프트삭제
     * - 상태 이력 테이블에 기록
     */
    public void approvalDisposal(UUID dispMId, String userId, String orgCd) {
        DisposalMaster master = disposalRepository.findMasterById(dispMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 불용 신청입니다."));

        disposalPolicy.validateOwnership(master,orgCd);
        disposalPolicy.validateConfirm(master);

        List<DisposalDetail> details = disposalRepository.findDetailsByMasterId(dispMId, orgCd);
        if (details.isEmpty()) {
            throw new BusinessException("처분 상세 정보가 없습니다.");
        }

        List<Asset> assets = new ArrayList<>(details.size());
        List<AssetStatusHistory> histories = new ArrayList<>(details.size());

        //히스토리 저장
        for (DisposalDetail detail : details) {
            String itemNo = detail.getItmNo();
            Asset asset = assetRepository.findAssetById(itemNo, orgCd);
            if (asset == null) {
                throw new BusinessException("해당 물품 정보를 대장에서 찾을 수 없습니다: " + itemNo);
            }
            assets.add(asset);
            OperStatus prevStatus = asset.getOperSts();
            histories.add(
                    AssetStatusHistory.builder()
                            .itemHisId(UUID.randomUUID())
                            .itmNo(itemNo)
                            .prevSts(prevStatus) //이전 상태
                            .newSts(asset.getOperSts()) //현재 상태 = 반납
                            .chgRsn("처분 승인") //별도로 enum 관리를 하고싶다면 변동 가능성 있음.
                            .reqUsrId(master.getAplyUsrId())
                            .reqAt(master.getAplyAt())
                            .apprUsrId(userId)
                            .apprAt(LocalDate.now(ZoneId.of("Asia/Seoul")))
                            .orgCd(orgCd)
                            .delAt(asset.getDelAt())
                            .delYn(asset.getDelYn())
                            .build());
        }

        //운용상태 -> 처분(이력 구분을 위해)
        assetRepository.bulkDisposal(assets,userId,orgCd);
        //바로 소프트 삭제
        assetRepository.bulkSoftDelete(assets,userId,orgCd);

        historyRepository.saveAll(histories);

        master.confirmApproval(userId);
        disposalRepository.saveMaster(master);
    }

    /**
     * 처분 반려 (ADMIN 권한)
     */
    public void rejectDisposal(UUID dispMId, String userId, String orgCd) {
        DisposalMaster master = disposalRepository.findMasterById(dispMId, orgCd)
                .orElseThrow(() -> new BusinessException("존재하지 않는 불용 신청입니다."));
        //정책 확인
        disposalPolicy.validateConfirm(master);
        disposalPolicy.validateOwnership(master, orgCd);

        //상태 변경(반납 신청 반려처리 -> 저장)
        master.rejectApproval(userId);
        disposalRepository.saveMaster(master);
    }
}