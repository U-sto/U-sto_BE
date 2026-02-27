package com.usto.api.common.code.application;

import com.usto.api.common.code.domain.CodeGroup;
import com.usto.api.common.code.presentation.dto.CodeGroupResponse;
import com.usto.api.common.code.presentation.dto.CodeResponse;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CodeApplication {

    /**
     * 특정 코드 그룹 조회
     */
    public CodeGroupResponse getCodeGroup(String groupName) {
        return switch (groupName.toUpperCase()) {
            case "APPR_STATUS" -> createCodeGroup("승인상태", ApprStatus.values());
            case "OPER_STATUS" -> createCodeGroup("운용상태", OperStatus.values());
            case "ITEM_STATUS" -> createCodeGroup("물품상태", ItemStatus.values());
            case "ACQ_ARRANGEMENT_TYPE" -> createCodeGroup("취득정리구분", AcqArrangementType.values());
            case "RETURNING_REASON" -> createCodeGroup("반납사유", ReturningReason.values());
            case "DISUSE_REASON" -> createCodeGroup("불용사유", DisuseReason.values());
            case "DISPOSAL_TYPE" -> createCodeGroup("처분정리구분", DisposalArrangementType.values());
            default -> throw new IllegalArgumentException("존재하지 않는 코드 그룹입니다: " + groupName);
        };
    }

    /**
     * 모든 코드 그룹 조회
     */
    public List<CodeGroupResponse> getAllCodeGroups() {
        return List.of(
                createCodeGroup("승인상태", ApprStatus.values()),
                createCodeGroup("운용상태", OperStatus.values()),
                createCodeGroup("물품상태", ItemStatus.values()),
                createCodeGroup("취득정리구분", AcqArrangementType.values()),
                createCodeGroup("반납사유", ReturningReason.values()),
                createCodeGroup("불용사유", DisuseReason.values()),
                createCodeGroup("처분정리구분", DisposalArrangementType.values())
        );
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