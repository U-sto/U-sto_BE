package com.usto.api.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UsefulLifeItemsDeserializer extends JsonDeserializer<List<PrdctUsefulLifeEnvelope.Item>> {

    @Override
    public List<PrdctUsefulLifeEnvelope.Item> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);

        // 케이스1) items: { item: {...} } or items: { item: [{...},{...}] }
        if (node != null && node.has("item")) {
            node = node.get("item");
        }

        List<PrdctUsefulLifeEnvelope.Item> result = new ArrayList<>();
        ObjectMapper om = (ObjectMapper) codec;

        if (node == null || node.isNull()) return result;

        if (node.isArray()) {
            for (JsonNode it : node) {
                result.add(om.treeToValue(it, PrdctUsefulLifeEnvelope.Item.class));
            }
            return result;
        }

        if (node.isObject()) {
            result.add(om.treeToValue(node, PrdctUsefulLifeEnvelope.Item.class));
            return result;
        }

        return result;
    }
}
