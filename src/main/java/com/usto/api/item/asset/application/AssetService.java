package com.usto.api.item.asset.application;

import com.usto.api.item.acquisition.domain.model.Acquisition;
import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.model.AssetMaster;
import com.usto.api.item.asset.domain.repository.AssetRepository;
import com.usto.api.item.asset.presentation.dto.request.AssetSearchRequest;
import com.usto.api.item.asset.presentation.dto.response.AssetListResponse;
import com.usto.api.item.common.utils.ItemNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 자산(Asset) 관리 서비스
 * - 운용대장 조회, 개별물품 정보 수정 및 취득 확정 시 대장 자동 생성 담당
 */
@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final ItemNumberGenerator ItemNumberGenerator;

    /**
     * 운용대장 목록 조회
     * - 처분되지 않은(논리삭제 안 된) 물품만 조회
     * - 필터: G2B코드, 취득일자, 정리일자, 부서, 운용상태, 물품번호
     */
    @Transactional(readOnly = true)
    public List<AssetListResponse> getAssetList(AssetSearchRequest searchRequest, String orgCd) {
        return assetRepository.findAllByFilter(searchRequest, orgCd);
    }

    /**
     * 취득 승인 건을 운용대장(002M/D)으로 정식 등록
     * - 하나의 취득 ID에 대해 1개의 마스터와 N개의 상세 데이터를 생성함
     * @param acq 승인 완료된 취득 도메인 모델
     */
    // TODO: 해당 메서드 사용하여 acquistion 패키지에서 취득승인 로직 구현
    @Transactional
    public void registerAssetsFromAcquisition(Acquisition acq) {
        // 1. 대장 기본(Master) 생성: 1건
        AssetMaster master = AssetMaster.create(
                acq.getAcqId(), acq.getG2bDCd(), acq.getAcqQty(),
                acq.getAcqAt(), acq.getApprAt(), acq.getOrgCd()
        );
        assetRepository.saveMaster(master);

        // 2. 현재 연도 순번 가져오기
        int currentYear = LocalDate.now().getYear();
        int lastSeq = assetRepository.getNextSequenceForYear(currentYear, acq.getOrgCd());

        // 3. [핵심] 수량만큼 반복문 수행
        for (int i = 0; i < acq.getAcqQty(); i++) {
            // 고유번호 생성 (예: M202600001, M202600002...)
            String itmNo = ItemNumberGenerator.generate(currentYear, lastSeq + i);

            // 개별물품 생성
            Asset asset = Asset.createFromAcquisition(
                    itmNo,
                    acq.getAcqId(),
                    acq.getG2bDCd(),
                    acq.getDeptCd(),
                    acq.getOperSts(),
                    acq.getAcqUpr(),
                    acq.getDrbYr(),
                    acq.getOrgCd()
            );

            assetRepository.save(asset); // 002D에 저장
        }
    }
}