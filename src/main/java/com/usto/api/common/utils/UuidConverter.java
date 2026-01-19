package com.usto.api.common.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.nio.ByteBuffer;
import java.util.UUID;

@Converter
public class UuidConverter implements AttributeConverter<String, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(String uuid) {
        if (uuid == null) {
            return null;
        }

        UUID uuidObj = UUID.fromString(uuid);
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuidObj.getMostSignificantBits());
        bb.putLong(uuidObj.getLeastSignificantBits());
        return bb.array();
    }

    @Override
    public String convertToEntityAttribute(byte[] bytes) {
        if (bytes == null || bytes.length != 16) {
            return null;
        }

        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long mostSigBits = bb.getLong();
        long leastSigBits = bb.getLong();
        UUID uuid = new UUID(mostSigBits, leastSigBits);
        return uuid.toString();
    }
}