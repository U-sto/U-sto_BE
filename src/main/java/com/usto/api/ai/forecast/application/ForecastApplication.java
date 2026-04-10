package com.usto.api.ai.forecast.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usto.api.ai.common.AiForecastAdapter;
import com.usto.api.ai.forecast.domain.model.Forecast;
import com.usto.api.ai.forecast.domain.model.RiskLevel;
import com.usto.api.ai.forecast.domain.repository.ForecastRepository;
import com.usto.api.ai.forecast.domain.service.ForecastPolicy;
import com.usto.api.ai.forecast.infrastructure.mapper.ForecastMapper;
import com.usto.api.ai.forecast.presentation.dto.request.AiForecastRequest;
import com.usto.api.ai.forecast.presentation.dto.request.AiForecastRequestToAi;
import com.usto.api.ai.forecast.presentation.dto.response.AiForecastResponse;
import com.usto.api.common.exception.BusinessException;
import com.usto.api.organization.infrastructure.entity.DepartmentJpaEntity;
import com.usto.api.organization.infrastructure.repository.DepartmentJpaRepository;
import com.usto.api.organization.infrastructure.repository.OrganizationJpaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForecastApplication {

    private final AiForecastAdapter aiForecastAdapter;
    private final ObjectMapper objectMapper;
    private final ForecastPolicy forecastPolicy;
    private final ForecastRepository forecastRepository;
    private final DepartmentJpaRepository departmentJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;

    @Transactional
    public AiForecastResponse analyze(String usrId, String orgCd, AiForecastRequest request) {

        //정책 검사
        forecastPolicy.validateRequest(request,orgCd);
        forecastPolicy.validateOrganization(request.conditions().campus(),orgCd);

        // AI에게 보낼 Resquest
        AiForecastRequestToAi requestToAi = toAiPayload(request);

        // AI 호출
        AiForecastResponse responseFromAi =
                aiForecastAdapter.fetchForecastResponse(requestToAi);

        //도메인 객체에 내용 담기
        Forecast forecast = ForecastMapper.toDomain(
                usrId,
                request.conditions().year().shortValue(),
                request.conditions().semester().byteValue(),
                request.conditions().risk_level(),
                request.prompt(),
                orgCd,
                toJsonNullable(responseFromAi.section4AlgorithmGuide()),//AI분석알고리즘가이드(원래 상단요약이였는데 바뀜 - 어쩔 수 없음)
                toJsonNullable(responseFromAi.section1TimeSeries()),
                toJsonNullable(responseFromAi.section2StrategicGuide()), //매트릭스=AI전략적 조달 가이드
                toJsonNullable(responseFromAi.section3Recommendations()),
                request.conditions().department(),
                request.conditions().category()
        );

        forecastRepository.save(forecast);

        return responseFromAi;
    }

    @Transactional
    public AiForecastResponse check(String username, String orgCd, @Valid UUID forecastId) {

        Forecast forecast = forecastRepository.findById(forecastId);
        if(forecast == null){
            throw new BusinessException("존재하지 않는 예측입니다.");
        }

        forecastPolicy.validateOrganization(forecast.getOrgCode(),orgCd);
        forecastPolicy.valdateOwnership(forecast.getUserId(),username);

        JsonNode tsNode = readTreeOrNull(forecast.getTsJson());
        JsonNode matrixNode = readTreeOrNull(forecast.getMatrixJson());
        JsonNode recoNode = readTreeOrNull(forecast.getRecoJson());
        JsonNode algoNode = readTreeOrNull(forecast.getSummaryJson());  //이렇게 안 하면 다 바꿔야함

        // 저장을 섹션별로 했을 때, 다시 하나의 객체로 합쳐 반환
        AiForecastResponse ai = new AiForecastResponse(
                tsNode == null ? null : objectMapper.convertValue(
                        tsNode, new TypeReference<List<AiForecastResponse.TimeSeriesPointRaw>>() {}
                ),
                matrixNode == null ? null : objectMapper.convertValue(
                        matrixNode, AiForecastResponse.StrategicGuidePointRaw.class
                ),
                recoNode == null ? null : objectMapper.convertValue(
                        recoNode, new TypeReference<List<AiForecastResponse.RecommendationItemRaw>>() {}
                ),
                algoNode == null ? null : objectMapper.convertValue(
                        algoNode, AiForecastResponse.AlgorithmGuideRaw.class
                )
        );

        return ai;
    }

    private String toJsonNullable(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Forecast 응답 JSON 직렬화에 실패했습니다.");
        }
    }

    private JsonNode readTreeOrNull(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Transactional
    public List<UUID> findAll(String username, String orgCd) {

        List<UUID> ids = forecastRepository.findByUsrId(username);

        return ids;
    }

    @Transactional
    public void delete(String username, String orgCd, @Valid UUID forecastId) {
        Forecast forecast = forecastRepository.findById(forecastId);
        if(forecast == null){
            throw new BusinessException("존재하지 않는 예측입니다.");
        }

        forecastPolicy.validateOrganization(forecast.getOrgCode(),orgCd);
        forecastPolicy.valdateOwnership(forecast.getUserId(),username);

        forecastRepository.delete(forecastId);

    }

    private AiForecastRequestToAi toAiPayload(AiForecastRequest request) {
        AiForecastRequest.Conditions c = request.conditions();

        String aiSemester = toAiSemester(c.semester());
        String deptName = resolveDeptName(c.campus(),c.department());
        String riskLevel = toAiRiskLevel(c.risk_level());
        String orgName = resolveOrgName(c.campus());
        log.info("year={}, semester={}, orgName={}, dept_name={}, category() ={} , risk_level ={}"
                ,c.year(), aiSemester, orgName, deptName,c.category(),riskLevel);

        return new AiForecastRequestToAi(
                request.prompt(),
                new AiForecastRequestToAi.Conditions(
                        c.year(), //2026
                        aiSemester, //1, 여름, 2, 겨울
                        orgName,          //한양대학교 ERICA캠퍼스
                        deptName,             //소프트웨어융합대학행정실
                        c.category(), //전부
                        riskLevel      //Low, Medium, High
                )
        );
    }

    private String toAiRiskLevel(RiskLevel riskLevel) {
        // AI 팀 스펙 확정에 맞춰 문자열 enum으로 변환
        return switch (riskLevel) {
            case LOW -> RiskLevel.LOW.getDisplayName();
            case MEDIUM -> RiskLevel.MEDIUM.getDisplayName();
            case HIGH -> RiskLevel.HIGH.getDisplayName();
             default -> throw new IllegalArgumentException("risk_level은 LOW, MEDIUM, HIGH값이여야합니다.: " + riskLevel);
        };
    }

    private String toAiSemester(Integer sem) {
        // AI 팀 스펙 확정에 맞춰 문자열 enum으로 변환
        return switch (sem) {
            case 1 -> "1";
            case 2 -> "여름";
            case 3 -> "2";
            case 4 -> "겨울";
            default -> throw new IllegalArgumentException("semester은 1-4값이여야합니다.: " + sem);
        };
    }

    private String resolveDeptName(String campus,String department) {

        return departmentJpaRepository.findById_OrgCdAndId_DeptCd(campus, department)
                .map(DepartmentJpaEntity::getDeptNm)
                .orElseGet(() -> {
                    log.warn("학과명 조회 실패: campus={}, dept_cd={}", campus, department);
                    return department;
                });
    }

    private String resolveOrgName(String campus) {
        return organizationJpaRepository.findById(campus)
                .map(entity -> entity.getOrgNm())
                .orElseGet(() -> {
                    log.warn("조직명 조회 실패: campus={}", campus);
                    return campus;
                });
    }


}
