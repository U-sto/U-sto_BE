package com.usto.api.common.code.application;

import com.usto.api.common.code.domain.CodeGroup;
import com.usto.api.common.code.presentation.dto.CodeGroupResponse;
import com.usto.api.common.code.presentation.dto.CodeResponse;
import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.acquisition.domain.model.AcqArrangementType;
import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.common.model.ItemStatus;
import com.usto.api.item.common.model.OperStatus;
import com.usto.api.item.disposal.domain.model.DisposalArrangementType;
import com.usto.api.item.disuse.domain.model.DisuseReason;
import com.usto.api.item.returning.domain.model.ReturningReason;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CodeApplication {

    private record CodeGroupInfo(String description, CodeGroup[] values) {}

    private static final Map<String, CodeGroupInfo> CODE_GROUPS = new LinkedHashMap<>() {{
        put("APPR_STATUS",               new CodeGroupInfo("승인상태",    ApprStatus.values()));
        put("OPER_STATUS",               new CodeGroupInfo("운용상태",    OperStatus.values()));
        put("ITEM_STATUS",               new CodeGroupInfo("물품상태",    ItemStatus.values()));
        put("ACQ_ARRANGEMENT_TYPE",      new CodeGroupInfo("취득정리구분", AcqArrangementType.values()));
        put("RETURNING_REASON",          new CodeGroupInfo("반납사유",    ReturningReason.values()));
        put("DISUSE_REASON",             new CodeGroupInfo("불용사유",    DisuseReason.values()));
        put("DISPOSAL_ARRANGEMENT_TYPE", new CodeGroupInfo("처분정리구분", DisposalArrangementType.values()));
    }};

    /**
     * 특정 코드 그룹 조회
     */
    public CodeGroupResponse getCodeGroup(String groupName) {
        CodeGroupInfo info = CODE_GROUPS.get(groupName.toUpperCase());
        if (info == null) {
            throw new BusinessException("존재하지 않는 코드 그룹입니다: " + groupName);
        }
        return createCodeGroup(info.description(), info.values());
    }

    /**
     * 모든 코드 그룹 조회
     */
    public List<CodeGroupResponse> getAllCodeGroups() {
        return CODE_GROUPS.values().stream()
                .map(info -> createCodeGroup(info.description(), info.values()))
                .collect(Collectors.toList());
    }

    /**
     * CodeGroup 인터페이스를 구현한 Enum → DTO 변환
     */
    private CodeGroupResponse createCodeGroup(String groupName, CodeGroup[] values) {
        List<CodeResponse> codes = Arrays.stream(values)
                .map(codeGroup -> new CodeResponse(
                        codeGroup.getCode(),
                        codeGroup.getDescription()
                ))
                .collect(Collectors.toList());
        return new CodeGroupResponse(groupName, codes);
    }
}