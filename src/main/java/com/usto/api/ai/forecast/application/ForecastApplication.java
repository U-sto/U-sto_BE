package com.usto.api.ai.forecast.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usto.api.ai.common.AiForecastAdapter;
import com.usto.api.ai.forecast.domain.model.Forecast;
import com.usto.api.ai.forecast.domain.repository.ForecastRepository;
import com.usto.api.ai.forecast.domain.service.ForecastPolicy;
import com.usto.api.ai.forecast.infrastructure.mapper.ForecastMapper;
import com.usto.api.ai.forecast.presentation.dto.request.AiForecastRequest;
import com.usto.api.ai.forecast.presentation.dto.request.AiForecastRequestToAi;
import com.usto.api.ai.forecast.presentation.dto.response.AiForecastResponse;
import com.usto.api.common.exception.BusinessException;
import com.usto.api.organization.infrastructure.entity.DepartmentJpaEntity;
import com.usto.api.organization.infrastructure.repository.DepartmentJpaRepository;
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

    @Transactional
    public AiForecastResponse analyze(String usrId, String orgCd, AiForecastRequest request) {

        //정책 검사
        forecastPolicy.validateRequest(request,orgCd);
        forecastPolicy.validateOrganization(request.conditions().campus(),orgCd);

        AiForecastRequestToAi requestToAi = toAiPayload(request);

        // AI 호출
        AiForecastResponse aiResponse = aiForecastAdapter.fetchForecastResponse(requestToAi);

        log.info("AI Response: {}", aiResponse);
        log.info("summary : {}",aiResponse.summary());

        //도메인 객체에 내용 담기
        Forecast forecast = ForecastMapper.toDomain(
                usrId,
                request.conditions().year().shortValue(),
                request.conditions().semester().byteValue(),
                request.conditions().risk_level(),
                request.prompt(),
                orgCd,
                toJsonNullable(aiResponse.summary()),
                toJsonNullable(aiResponse.chartForecast()),
                toJsonNullable(aiResponse.chartPortfolio()),
                toJsonNullable(aiResponse.recommendations()),
                request.conditions().department()
        );

        forecastRepository.save(forecast);

        return aiResponse;
    }

    @Transactional
    public AiForecastResponse check(String username, String orgCd, @Valid UUID forecastId) {

        Forecast forecast = forecastRepository.findById(forecastId);
        if(forecast == null){
            throw new BusinessException("존재하지 않는 예측입니다.");
        }

        forecastPolicy.validateOrganization(forecast.getOrgCode(),orgCd);
        forecastPolicy.valdateOwnership(forecast.getUserId(),username);

        JsonNode summaryNode = readTreeOrNull(forecast.getSummaryJson());
        JsonNode tsNode = readTreeOrNull(forecast.getTsJson());
        JsonNode matrixNode = readTreeOrNull(forecast.getMatrixJson());
        JsonNode recoNode = readTreeOrNull(forecast.getRecoJson());

        return AiForecastResponse
                .builder()
                .summary(objectMapper.convertValue(summaryNode, AiForecastResponse.Summary.class))
                .chartForecast(objectMapper.convertValue(tsNode, new TypeReference<>() {}))
                .chartPortfolio(objectMapper.convertValue(matrixNode, new TypeReference<>() {}))
                .recommendations(objectMapper.convertValue(recoNode, new TypeReference<>() {}))
                .build();
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

        return new AiForecastRequestToAi(
                request.prompt(),
                new AiForecastRequestToAi.Conditions(
                        c.year(),
                        // a) ToAi.semester 타입이 Integer인 현재 파일 기준
                        //aiSemester,
                        c.semester(),
                        c.campus(),          // org_cd
                        c.department(),      // dept_cd
                        c.category(),
                        c.risk_level(),      // enum은 문자열로 직렬화되어 전송됨
                        deptName             // dept_name
                )
        );
    }

    private String toAiSemester(Integer sem) {
        // AI 팀 스펙 확정에 맞춰 문자열 enum으로 변환
        return switch (sem) {
            case 1 -> "SPRING";
            case 2 -> "SUMMER";
            case 3 -> "FALL";
            case 4 -> "WINTER";
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
}
