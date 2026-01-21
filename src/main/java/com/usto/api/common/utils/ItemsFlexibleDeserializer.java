package com.usto.api.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.usto.api.g2b.presentation.dto.response.ShoppingMallEnvelope;

import java.io.IOException;
import java.util.List;

public class ItemsFlexibleDeserializer extends JsonDeserializer<List<ShoppingMallEnvelope.Item>> {

    private static final TypeReference<List<ShoppingMallEnvelope.Item>> LIST_TYPE = new TypeReference<>() {};

    @Override
    public List<ShoppingMallEnvelope.Item> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        if (node.isArray()) {
            return mapper.convertValue(node, LIST_TYPE);
        }

        if (node.isObject()) {
            JsonNode itemNode = ((ObjectNode) node).get("item");
            if (itemNode == null || itemNode.isNull()) return List.of();
            if (itemNode.isArray()) return mapper.convertValue(itemNode, LIST_TYPE);
        }

        return List.of();
    }
}
