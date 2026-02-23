package com.usto.api.ai.forecast.domain.service;

import com.usto.api.ai.chat.domain.model.ChatThread;
import com.usto.api.ai.forecast.presentation.dto.request.AiForecastRequest;
import com.usto.api.common.exception.BusinessException;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ForecastPolicy {
    public  void validateRequest(AiForecastRequest request, String orgCd) {

        if (request == null) {
            throw new BusinessException("요청 본문이 비어있습니다.");
        }

        if (!StringUtils.hasText(request.prompt())) {
            throw new BusinessException("prompt는 필수입니다.");
        }

        AiForecastRequest.Conditions c = request.conditions();
        if (c == null) {
            throw new BusinessException("conditions는 필수입니다.");
        }

        // year
        if (c.year() == null) {
            throw new BusinessException("conditions.year는 필수입니다.");
        }
        if (c.year() < 2000 || c.year() > 2100) {
            throw new BusinessException("conditions.year가 유효하지 않습니다. (2000~2100)");
        }

        // semester
        if (c.semester() == null) {
            throw new BusinessException("conditions.semester는 필수입니다.");
        }
        if (c.semester() != 1 && c.semester() != 2) {
            throw new BusinessException("conditions.semester는 1 또는 2만 허용됩니다.");
        }

        // campus/org
        if (!StringUtils.hasText(c.campus())) {
            throw new BusinessException("conditions.campus는 필수입니다.");
        }

        // 연결 성공 단계에서는 일단 문자열 통과시키되,
        // 원래 의도(코드 기반)라면 아래처럼 orgCd와의 불일치만 막아도 안정성이 올라감
        if (StringUtils.hasText(orgCd) && StringUtils.hasText(c.campus())) {
            // NOTE: 현재 campus 필드는 "ERICA" 같은 값이 들어오고, orgCd는 "0000000" 같은 코드일 수 있음.
            // 그래서 지금은 강제 비교하지 않고, 둘 다 코드로 통일되면 아래 비교를 활성화하세요.
            //
            // if (!orgCd.equals(c.campus())) {
            //     throw new BusinessException("요청 campus(org) 값이 로그인 조직코드와 일치하지 않습니다.");
            // }
        }

        // department/dept
        if (!StringUtils.hasText(c.department())) {
            throw new BusinessException("conditions.department는 필수입니다.");
        }

        // category (optional)
        if (c.category() != null && c.category().length() > 200) {
            throw new BusinessException("conditions.category가 너무 깁니다.");
        }

        // risk_level (enum)
        if (c.risk_level() == null) {
            throw new BusinessException("conditions.risk_level은 필수입니다.");
        }
    }

    public void validateOrganization(String campus, String orgCd) {
        if(!campus.equals(orgCd)){
            throw new BusinessException("요청 campus(org) 값이 로그인 조직코드와 일치하지 않습니다.");
        }
    }
}
