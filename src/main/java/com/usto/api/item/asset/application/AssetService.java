package com.usto.api.item.asset.application;

import com.usto.api.item.acquisition.domain.model.Acquisition;
import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.model.AssetMaster;
import com.usto.api.item.asset.domain.repository.AssetRepository;
import com.usto.api.item.asset.presentation.dto.request.AssetSearchRequest;
import com.usto.api.item.asset.presentation.dto.request.AssetUpdateRequest;
import com.usto.api.item.asset.presentation.dto.response.AssetDetailResponse;
import com.usto.api.item.asset.presentation.dto.response.AssetListResponse;
import com.usto.api.item.common.utils.ItemNumberGenerator;
import com.usto.api.common.exception.BusinessException;
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
    private final ItemNumberGenerator itemNumberGenerator;

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
     * 개별 물품 상세 조회 (상태 이력 포함)
     */
    @Transactional(readOnly = true)
    public AssetDetailResponse getAssetDetail(String itmNo, String orgCd) {
        // 1. 기본 정보 조회
        AssetDetailResponse detail = assetRepository.findDetailById(itmNo, orgCd)
                .orElseThrow(() -> new BusinessException("해당 물품을 찾을 수 없습니다."));

        // 2. 상태 이력 조회 및 추가
        List<AssetDetailResponse.StatusHistoryDto> histories =
                assetRepository.findStatusHistoriesByItmNo(itmNo, orgCd);

        return AssetDetailResponse.builder()
                .itmNo(detail.getItmNo())
                .g2bDNm(detail.getG2bDNm())
                .g2bItemNo(detail.getG2bItemNo())
                .acqAt(detail.getAcqAt())
                .arrgAt(detail.getArrgAt())
                .operSts(detail.getOperSts())
                .drbYr(detail.getDrbYr())
                .acqUpr(detail.getAcqUpr())
                .qty(detail.getQty())
                .acqArrgTy(detail.getAcqArrgTy())
                .deptNm(detail.getDeptNm())
                .deptCd(detail.getDeptCd())
                .rmk(detail.getRmk())
                .statusHistories(histories)
                .build();
    }

    /**
     * 개별 물품 정보 수정 (취득단가, 내용연수, 비고만 수정 가능)
     */
    @Transactional
    public void updateAssetInfo(String itmNo, AssetUpdateRequest request, String orgCd) {
        // 1. 물품 존재 여부 및 소속 조직 검증
        Asset asset = assetRepository.findById(itmNo, orgCd)
                .orElseThrow(() -> new BusinessException("해당 물품을 찾을 수 없습니다."));

        asset.validateOwnership(orgCd);

        // 2. 도메인 로직 실행 (내부에서 상태 및 값 검증 수행)
        asset.updateAssetInfo(request.getAcqUpr(), request.getDrbYr(), request.getRmk());

        // 3. 저장
        assetRepository.save(asset);
    }

    /**
     * 취득 승인 건을 운용대장(002M/D)으로 정식 등록
     * - 하나의 취득 ID에 대해 1개의 마스터와 N개의 상세 데이터를 생성함
     * @param acq 승인 완료된 취득 도메인 모델
     */
    // TODO: 해당 메서드 사용하여 acquistion 패키지에서 취득승인 로직 구현
    @Transactional
    public void registerAssetsFromAcquisition(Acquisition acq) {

        // 중복 체크: 이미 자산 등록된 취득 건인지 확인
        if (assetRepository.existsMasterByAcqId(acq.getAcqId())) {
            throw new BusinessException("이미 자산으로 등록된 취득 건(ID: " + acq.getAcqId() + ")입니다.");
        }

        // 1. 대장 기본(Master) 생성: 1건
        AssetMaster master = AssetMaster.create(
                acq.getAcqId(), acq.getG2bDCd(), acq.getAcqQty(),
                acq.getAcqAt(), acq.getApprAt(), acq.getArrgTy(), acq.getOrgCd()
        );
        assetRepository.saveMaster(master);

        // 2. 현재 연도 + 다음 순번 조회
        int currentYear = LocalDate.now().getYear();
        int nextSeq = assetRepository.getNextSequenceForYear(currentYear, acq.getOrgCd());

        // 3. [핵심] 수량만큼 반복문 수행
        for (int i = 0; i < acq.getAcqQty(); i++) {
            // 고유번호 생성 (예: M202600001, M202600002...)
            String itmNo = itemNumberGenerator.generate(currentYear, nextSeq + i);

            // 개별물품 생성
            Asset asset = Asset.create(
                    itmNo,
                    acq.getAcqId(),
                    acq.getG2bDCd(),
                    acq.getDeptCd(),
                    acq.getAcqUpr(),
                    acq.getDrbYr(),
                    acq.getOrgCd()
            );

            assetRepository.save(asset); // 002D에 저장
        }
    }
}