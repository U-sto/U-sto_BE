package com.usto.api.common.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class YseNoConverter implements AttributeConverter<Boolean, String> {
    // Entity(boolean) -> DB(Char/String)
    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return (attribute != null && attribute) ? "Y" : "N";
    }

    // DB(Char/String) -> Entity(boolean)
    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        return "Y".equalsIgnoreCase(dbData);
    }
}
