package com.whaleal.aihub.tool;

import com.whaleal.aihub.annotation.FunctionCall;
import com.whaleal.aihub.annotation.FunctionParameter;
import com.whaleal.aihub.annotation.FunctionRequest;
import com.whaleal.aihub.convert.Jsons;
import com.whaleal.aihub.platform.openai.tool.Tool;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 将 {@link FunctionCall} 注解转换为 Chat Completions 的 tools 字段，并执行本地 Function。
 *
 * @Author cly
 * @author 恒哥
 */
public class ToolUtil {

    private static final Logger log = LoggerFactory.getLogger(ToolUtil.class);

    private static volatile Reflections reflections;
    private static volatile boolean initialized = false;

    public static final Map<String, Tool> toolEntityMap = new ConcurrentHashMap<String, Tool>();
    public static final Map<String, Class<?>> toolClassMap = new ConcurrentHashMap<String, Class<?>>();
    public static final Map<String, Class<?>> toolRequestMap = new ConcurrentHashMap<String, Class<?>>();

    private static Reflections getReflections() {
        if (reflections == null) {
            synchronized (ToolUtil.class) {
                if (reflections == null) {
                    reflections = new Reflections(new ConfigurationBuilder()
                            .setUrls(ClasspathHelper.forPackage("com.whaleal.aihub"))
                            .setScanners(Scanners.TypesAnnotated, Scanners.MethodsAnnotated));
                }
            }
        }
        return reflections;
    }

    public static String invoke(String functionName, String argument) {
        ensureInitialized();
        if (toolClassMap.containsKey(functionName) && toolRequestMap.containsKey(functionName)) {
            return invokeFunctionTool(functionName, argument);
        }
        throw new RuntimeException("工具未找到: " + functionName);
    }

    public static List<Tool> getAllFunctionTools(List<String> functionList) {
        ensureInitialized();
        List<Tool> tools = new ArrayList<Tool>();
        if (functionList == null || functionList.isEmpty()) {
            return tools;
        }
        for (String functionName : functionList) {
            if (functionName == null || functionName.trim().isEmpty()) {
                continue;
            }
            try {
                Tool tool = toolEntityMap.get(functionName);
                if (tool == null) {
                    tool = getToolEntity(functionName);
                    if (tool != null) {
                        toolEntityMap.put(functionName, tool);
                    }
                }
                if (tool != null) {
                    tools.add(tool);
                }
            } catch (Exception e) {
                log.error("获取Function工具失败: {}", functionName, e);
            }
        }
        return tools;
    }

    public static Tool getToolEntity(String functionName) {
        if (functionName == null || functionName.trim().isEmpty()) {
            return null;
        }
        try {
            Tool.Function functionEntity = getFunctionEntity(functionName);
            if (functionEntity != null) {
                Tool tool = new Tool();
                tool.setType("function");
                tool.setFunction(functionEntity);
                return tool;
            }
        } catch (Exception e) {
            log.error("创建工具实体失败: {}", functionName, e);
        }
        return null;
    }

    public static Tool.Function getFunctionEntity(String functionName) {
        if (functionName == null || functionName.trim().isEmpty()) {
            return null;
        }
        try {
            Set<Class<?>> functionSet = getReflections().getTypesAnnotatedWith(FunctionCall.class);
            for (Class<?> functionClass : functionSet) {
                FunctionCall functionCall = functionClass.getAnnotation(FunctionCall.class);
                if (functionCall != null && functionCall.name().equals(functionName)) {
                    Tool.Function function = new Tool.Function();
                    function.setName(functionCall.name());
                    function.setDescription(functionCall.description());
                    setFunctionParameters(function, functionClass);
                    if (functionCall.strict()) {
                        applyStrictMode(function);
                    }
                    toolClassMap.put(functionName, functionClass);
                    return function;
                }
            }
        } catch (Exception e) {
            log.error("获取Function实体失败: {}", functionName, e);
        }
        return null;
    }

    private static void ensureInitialized() {
        if (!initialized) {
            synchronized (ToolUtil.class) {
                if (!initialized) {
                    scanFunctionTools();
                    initialized = true;
                }
            }
        }
    }

    private static void scanFunctionTools() {
        try {
            Set<Class<?>> functionSet = getReflections().getTypesAnnotatedWith(FunctionCall.class);
            for (Class<?> functionClass : functionSet) {
                FunctionCall functionCall = functionClass.getAnnotation(FunctionCall.class);
                if (functionCall == null) {
                    continue;
                }
                String functionName = functionCall.name();
                toolClassMap.put(functionName, functionClass);
                Class<?>[] innerClasses = functionClass.getDeclaredClasses();
                for (Class<?> innerClass : innerClasses) {
                    if (innerClass.getAnnotation(FunctionRequest.class) != null) {
                        toolRequestMap.put(functionName, innerClass);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("扫描Function工具失败", e);
        }
    }

    private static String invokeFunctionTool(String functionName, String argument) {
        Class<?> functionClass = toolClassMap.get(functionName);
        Class<?> functionRequestClass = toolRequestMap.get(functionName);
        try {
            Method apply = functionClass.getMethod("apply", functionRequestClass);
            Object arg = Jsons.fromJson(argument, functionRequestClass);
            Object functionInstance = functionClass.getDeclaredConstructor().newInstance();
            Object result = apply.invoke(functionInstance, arg);
            return Jsons.toJson(result);
        } catch (Exception e) {
            log.error("Call Function Name: {} failed", functionName, e);
            throw new RuntimeException("Call Function Error: " + functionName, e);
        }
    }

    private static void setFunctionParameters(Tool.Function function, Class<?> functionClass) {
        Map<String, Tool.Function.Property> parameters = new HashMap<String, Tool.Function.Property>();
        List<String> requiredParameters = new ArrayList<String>();
        for (Class<?> clazz : functionClass.getDeclaredClasses()) {
            if (clazz.getAnnotation(FunctionRequest.class) == null) {
                continue;
            }
            toolRequestMap.put(function.getName(), clazz);
            for (Field field : clazz.getDeclaredFields()) {
                FunctionParameter parameter = field.getAnnotation(FunctionParameter.class);
                if (parameter == null) {
                    continue;
                }
                parameters.put(field.getName(), createPropertyFromType(field.getType(), parameter.description()));
                if (parameter.required()) {
                    requiredParameters.add(field.getName());
                }
            }
        }
        function.setParameters(new Tool.Function.Parameter("object", parameters, requiredParameters));
    }

    private static void applyStrictMode(Tool.Function function) {
        Tool.Function.Parameter params = function.getParameters();
        if (params != null) {
            params.enforceStrictSchema();
        }
        function.setStrict(Boolean.TRUE);
    }

    private static Tool.Function.Property createPropertyFromType(Class<?> fieldType, String description) {
        Tool.Function.Property property = new Tool.Function.Property();
        if (fieldType.isEnum()) {
            property.setType("string");
            property.setEnumValues(getEnumValues(fieldType));
        } else if (fieldType.equals(String.class)) {
            property.setType("string");
        } else if (fieldType.equals(int.class) || fieldType.equals(Integer.class)
                || fieldType.equals(long.class) || fieldType.equals(Long.class)
                || fieldType.equals(short.class) || fieldType.equals(Short.class)) {
            property.setType("integer");
        } else if (fieldType.equals(float.class) || fieldType.equals(Float.class)
                || fieldType.equals(double.class) || fieldType.equals(Double.class)) {
            property.setType("number");
        } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
            property.setType("boolean");
        } else if (fieldType.isArray() || Collection.class.isAssignableFrom(fieldType)) {
            property.setType("array");
            Tool.Function.Property items = new Tool.Function.Property();
            Class<?> elementType = fieldType.isArray() ? fieldType.getComponentType() : null;
            if (elementType == String.class) {
                items.setType("string");
            } else if (elementType == Integer.class || elementType == int.class
                    || elementType == Long.class || elementType == long.class) {
                items.setType("integer");
            } else if (elementType == Double.class || elementType == double.class
                    || elementType == Float.class || elementType == float.class) {
                items.setType("number");
            } else if (elementType == Boolean.class || elementType == boolean.class) {
                items.setType("boolean");
            } else {
                items.setType("object");
            }
            property.setItems(items);
        } else {
            property.setType("object");
        }
        property.setDescription(description);
        return property;
    }

    private static List<String> getEnumValues(Class<?> enumType) {
        List<String> enumValues = new ArrayList<String>();
        for (Object enumConstant : enumType.getEnumConstants()) {
            enumValues.add(enumConstant.toString());
        }
        return enumValues;
    }
}
