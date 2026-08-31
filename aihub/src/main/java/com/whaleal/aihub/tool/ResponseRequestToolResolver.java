package com.whaleal.aihub.tool;

import com.whaleal.aihub.platform.openai.response.entity.ResponseRequest;
import com.whaleal.aihub.platform.openai.tool.Tool;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 将 Chat Completions 的 Function 声明投影为 Responses API 的 tools 形态。
 *
 * @author 恒哥
 */
public final class ResponseRequestToolResolver {

    private ResponseRequestToolResolver() {
    }

    public static ResponseRequest resolve(ResponseRequest request) {
        if (request == null) {
            return null;
        }
        if (request.getFunctions() == null || request.getFunctions().isEmpty()) {
            return request;
        }

        List<Object> mergedTools = new ArrayList<Object>();
        if (request.getTools() != null && !request.getTools().isEmpty()) {
            mergedTools.addAll(request.getTools());
        }

        List<Tool> resolvedTools = ToolUtil.getAllFunctionTools(request.getFunctions());
        if (resolvedTools != null && !resolvedTools.isEmpty()) {
            for (Tool tool : resolvedTools) {
                mergedTools.add(toResponsesTool(tool));
            }
        }

        return request.toBuilder()
                .tools(mergedTools)
                .build();
    }

    private static Object toResponsesTool(Tool tool) {
        if (tool == null || tool.getFunction() == null) {
            return tool;
        }
        Tool.Function function = tool.getFunction();

        Map<String, Object> flat = new LinkedHashMap<String, Object>();
        flat.put("type", tool.getType() == null ? "function" : tool.getType());
        flat.put("name", function.getName());
        if (function.getDescription() != null) {
            flat.put("description", function.getDescription());
        }
        if (function.getParameters() != null) {
            flat.put("parameters", function.getParameters());
        }
        if (function.getStrict() != null) {
            flat.put("strict", function.getStrict());
        }
        return flat;
    }
}
