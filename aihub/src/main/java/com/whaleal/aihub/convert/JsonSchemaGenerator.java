package com.whaleal.aihub.convert;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Generates a JSON Schema from a Java class using reflection — no extra JSON library.
 * <p>
 * Supports: String, Integer/int, Long/long, Double/double, Float/float, Boolean/boolean,
 * BigDecimal, BigInteger, enum (→ string + enum values), nested objects (recursive),
 * List&lt;T&gt;/Collection&lt;T&gt; (→ array + items), Map (→ object with string values),
 * arrays. Generates OpenAI Structured Outputs-compatible output (all fields required,
 * additionalProperties: false).
 *
 * @author 恒哥
 */
public final class JsonSchemaGenerator {

    private JsonSchemaGenerator() {
    }

    public static String generate(Class<?> clazz) {
        return Jsons.toJson(generateObject(clazz));
    }

    public static ObjectNode generateObject(Class<?> clazz) {
        return buildSchema(clazz);
    }

    public static ObjectNode responseFormat(Class<?> clazz, String schemaName) {
        ObjectNode jsonSchema = Jsons.mapper().createObjectNode();
        jsonSchema.put("name", schemaName == null ? clazz.getSimpleName().toLowerCase() : schemaName);
        jsonSchema.put("strict", true);
        jsonSchema.set("schema", buildSchema(clazz));

        ObjectNode result = Jsons.mapper().createObjectNode();
        result.put("type", "json_schema");
        result.set("json_schema", jsonSchema);
        return result;
    }

    private static ObjectNode buildSchema(Class<?> clazz) {
        if (clazz == String.class || clazz == CharSequence.class || clazz == Character.class
                || clazz == char.class) {
            return simpleSchema("string");
        }
        if (clazz == Integer.class || clazz == int.class || clazz == Long.class || clazz == long.class
                || clazz == Short.class || clazz == short.class || clazz == Byte.class || clazz == byte.class
                || clazz == java.math.BigInteger.class) {
            return simpleSchema("integer");
        }
        if (clazz == Double.class || clazz == double.class || clazz == Float.class || clazz == float.class
                || clazz == java.math.BigDecimal.class) {
            return simpleSchema("number");
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return simpleSchema("boolean");
        }
        if (clazz.isEnum()) {
            ObjectNode schema = simpleSchema("string");
            ArrayNode values = Jsons.mapper().createArrayNode();
            for (Object constant : clazz.getEnumConstants()) {
                values.add(((Enum<?>) constant).name());
            }
            schema.set("enum", values);
            return schema;
        }
        if (clazz == Object.class) {
            return simpleSchema("object");
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            ObjectNode schema = simpleSchema("array");
            schema.set("items", simpleSchema("string"));
            return schema;
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return simpleSchema("object");
        }
        return buildObjectSchema(clazz);
    }

    private static ObjectNode buildObjectSchema(Class<?> clazz) {
        ObjectNode schema = simpleSchema("object");
        ObjectNode properties = Jsons.mapper().createObjectNode();
        ArrayNode required = Jsons.mapper().createArrayNode();

        for (Field field : getAllFields(clazz)) {
            field.setAccessible(true);
            String name = getFieldName(field);
            properties.set(name, buildFieldSchema(field));
            required.add(name);
        }

        schema.set("properties", properties);
        schema.set("required", required);
        schema.put("additionalProperties", false);
        return schema;
    }

    private static ObjectNode buildFieldSchema(Field field) {
        Class<?> type = field.getType();
        Type genericType = field.getGenericType();

        if (type.isArray()) {
            ObjectNode schema = simpleSchema("array");
            schema.set("items", buildSchema(type.getComponentType()));
            return schema;
        }
        if (Collection.class.isAssignableFrom(type) || List.class.isAssignableFrom(type)) {
            return buildSchemaFromType(genericType);
        }
        if (Map.class.isAssignableFrom(type)) {
            return simpleSchema("object");
        }
        return buildSchema(type);
    }

    private static ObjectNode buildSchemaFromType(Type genericType) {
        if (genericType instanceof Class) {
            return buildSchema((Class<?>) genericType);
        }
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Class<?> rawType = (Class<?>) pt.getRawType();
            Type[] typeArgs = pt.getActualTypeArguments();
            if (Collection.class.isAssignableFrom(rawType) && typeArgs.length > 0) {
                ObjectNode schema = simpleSchema("array");
                schema.set("items", buildSchemaFromType(typeArgs[0]));
                return schema;
            }
            if (Map.class.isAssignableFrom(rawType)) {
                return simpleSchema("object");
            }
            return buildSchema(rawType);
        }
        return simpleSchema("string");
    }

    private static ObjectNode simpleSchema(String type) {
        ObjectNode schema = Jsons.mapper().createObjectNode();
        schema.put("type", type);
        return schema;
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field f : current.getDeclaredFields()) {
                if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())
                        && !java.lang.reflect.Modifier.isTransient(f.getModifiers())) {
                    fields.add(f);
                }
            }
            current = current.getSuperclass();
        }
        return fields;
    }

    private static String getFieldName(Field field) {
        JsonProperty jackson = field.getAnnotation(JsonProperty.class);
        if (jackson != null && jackson.value() != null && !jackson.value().isEmpty()) {
            return jackson.value();
        }
        return field.getName();
    }
}
