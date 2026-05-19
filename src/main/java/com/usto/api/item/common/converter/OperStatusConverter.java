package com.usto.api.item.common.converter;

import com.usto.api.item.common.model.OperStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter(autoApply = true)
public class OperStatusConverter implements AttributeConverter<OperStatus, String> {

    @Override
    public String convertToDatabaseColumn(OperStatus attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public OperStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            return OperStatus.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            return null;  // NONE, ACQ 등 운용상태 ENUM에 없는 알 수 없는 값 → null 처리
        }
    }
}