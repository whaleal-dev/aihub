package com.whaleal.aihub.platform.hunyuan.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.whaleal.aihub.convert.Jsons;

import java.util.Iterator;
import java.util.Map;

/**
 * 混元请求体字段在驼峰与下划线之间转换。
 *
 * @Author cly
 * @author 恒哥
 * @Description 用于JSON字符串驼峰转换的工具类
 * @Date 2024/8/30 23:12
 */
public final class HunyuanJsonUtil {

    public static String toCamelCaseWithUppercaseJson(String json) {
        return convertJsonString(json, true);
    }

    public static String toSnakeCaseJson(String json) {
        return convertJsonString(json, false);
    }

    /**
     * 混元要求 tools.function.parameters 为 JSON 字符串；hunyuan-vision 把数组 content 挪到 contents。
     */
    public static String rewriteRequestBody(String requestJson, String model) {
        JsonNode rootNode = Jsons.readTree(requestJson);
        if (!rootNode.isObject()) {
            return toCamelCaseWithUppercaseJson(requestJson);
        }
        ObjectNode root = (ObjectNode) rootNode;
        JsonNode toolsNode = root.get("tools");
        if (toolsNode != null && toolsNode.isArray()) {
            for (JsonNode toolNode : toolsNode) {
                if (!toolNode.isObject()) {
                    continue;
                }
                ObjectNode tool = (ObjectNode) toolNode;
                JsonNode functionNode = tool.get("function");
                if (functionNode == null || !functionNode.isObject()) {
                    continue;
                }
                ObjectNode function = (ObjectNode) functionNode;
                ObjectNode newFunction = Jsons.mapper().createObjectNode();
                if (function.has("name")) {
                    newFunction.put("name", function.path("name").asText(null));
                }
                if (function.has("description")) {
                    newFunction.put("description", function.path("description").asText(null));
                }
                JsonNode parameters = function.get("parameters");
                if (parameters != null && !parameters.isNull()) {
                    newFunction.put("parameters", parameters.toString());
                }
                tool.set("function", newFunction);
                tool.put("type", "function");
            }
        }
        if ("hunyuan-vision".equals(model)) {
            JsonNode messagesNode = root.get("messages");
            if (messagesNode != null && messagesNode.isArray()) {
                for (JsonNode messageNode : messagesNode) {
                    if (!messageNode.isObject()) {
                        continue;
                    }
                    ObjectNode message = (ObjectNode) messageNode;
                    JsonNode contentNode = message.get("content");
                    String content = contentNode == null || contentNode.isNull() ? null : contentNode.asText();
                    if (content != null && content.startsWith("[") && content.endsWith("]")) {
                        JsonNode parsed = Jsons.readTree(content);
                        if (parsed.isArray() && parsed.size() > 0) {
                            message.set("contents", parsed);
                            message.remove("content");
                        }
                    }
                }
            }
        }
        return toCamelCaseWithUppercaseJson(Jsons.toJson(root));
    }

    /**
     * 混元响应包在 Response 字段里；先转 snake_case 再取出。
     */
    public static String extractChatPayload(String responseJson) {
        JsonNode root = Jsons.readTree(toSnakeCaseJson(responseJson));
        JsonNode response = root.get("response");
        if (response == null || response.isNull() || response.isMissingNode()) {
            throw new IllegalStateException("Hunyuan response missing Response field");
        }
        return response.isValueNode() ? response.asText() : response.toString();
    }

    private static String convertJsonString(String json, boolean isCamelCase) {
        return Jsons.toJson(renameKeys(Jsons.readTree(json), isCamelCase));
    }

    private static JsonNode renameKeys(JsonNode node, boolean isCamelCase) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return node;
        }
        if (node.isObject()) {
            ObjectNode out = Jsons.mapper().createObjectNode();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String newKey = isCamelCase ? toCamelCaseWithUppercase(entry.getKey()) : toSnakeCase(entry.getKey());
                out.set(newKey, renameKeys(entry.getValue(), isCamelCase));
            }
            return out;
        }
        if (node.isArray()) {
            ArrayNode out = Jsons.mapper().createArrayNode();
            for (JsonNode element : node) {
                out.add(renameKeys(element, isCamelCase));
            }
            return out;
        }
        return node;
    }

    private static String toCamelCaseWithUppercase(String key) {
        String[] parts = key.split("_");
        StringBuilder camelCaseKey = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                camelCaseKey.append(part.substring(0, 1).toUpperCase())
                        .append(part.substring(1).toLowerCase());
            }
        }
        return camelCaseKey.toString();
    }

    private static String toSnakeCase(String key) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append("_");
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private HunyuanJsonUtil() {
    }
}
