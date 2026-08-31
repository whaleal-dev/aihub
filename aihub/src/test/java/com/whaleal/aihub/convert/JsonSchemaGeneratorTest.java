package com.whaleal.aihub.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests {@link JsonSchemaGenerator} — Java class → JSON Schema generation via reflection.
 */
public class JsonSchemaGeneratorTest {

    public enum Sentiment { POSITIVE, NEGATIVE, NEUTRAL }

    public static class Address {
        private String city;
        private String zipCode;
    }

    public static class Person {
        private String name;
        private Integer age;
        private Boolean active;
        private Double score;
        private Sentiment sentiment;
        private Address address;
        private List<String> tags;
        private int primitiveCount;
    }

    @Test
    public void generatesBasicObjectSchema() {
        ObjectNode schema = JsonSchemaGenerator.generateObject(Address.class);

        assertEquals("object", schema.path("type").asText());
        assertEquals(false, schema.path("additionalProperties").asBoolean());

        JsonNode props = schema.get("properties");
        assertNotNull(props);
        assertNotNull(props.get("city"));
        assertNotNull(props.get("zipCode"));

        ArrayNode required = (ArrayNode) schema.get("required");
        assertTrue("city should be required", containsText(required, "city"));
        assertTrue("zipCode should be required", containsText(required, "zipCode"));
    }

    @Test
    public void mapsJavaTypesToJsonTypes() {
        ObjectNode schema = JsonSchemaGenerator.generateObject(Person.class);
        JsonNode props = schema.get("properties");

        assertEquals("string", props.path("name").path("type").asText());
        assertEquals("integer", props.path("age").path("type").asText());
        assertEquals("boolean", props.path("active").path("type").asText());
        assertEquals("number", props.path("score").path("type").asText());
        assertEquals("integer", props.path("primitiveCount").path("type").asText());
    }

    @Test
    public void enumGeneratesStringWithEnumValues() {
        ObjectNode schema = JsonSchemaGenerator.generateObject(Person.class);
        JsonNode sentiment = schema.path("properties").path("sentiment");

        assertEquals("string", sentiment.path("type").asText());
        JsonNode values = sentiment.get("enum");
        assertNotNull(values);
        assertEquals(3, values.size());
        assertTrue(containsText(values, "POSITIVE"));
        assertTrue(containsText(values, "NEGATIVE"));
        assertTrue(containsText(values, "NEUTRAL"));
    }

    @Test
    public void nestedObjectRecurses() {
        ObjectNode schema = JsonSchemaGenerator.generateObject(Person.class);
        JsonNode address = schema.path("properties").path("address");

        assertEquals("object", address.path("type").asText());
        assertNotNull(address.path("properties").get("city"));
        assertNotNull(address.path("properties").get("zipCode"));
        assertEquals(false, address.path("additionalProperties").asBoolean());
    }

    @Test
    public void listGeneratesArrayWithItems() {
        ObjectNode schema = JsonSchemaGenerator.generateObject(Person.class);
        JsonNode tags = schema.path("properties").path("tags");

        assertEquals("array", tags.path("type").asText());
        assertEquals("string", tags.path("items").path("type").asText());
    }

    @Test
    public void allFieldsRequired() {
        ObjectNode schema = JsonSchemaGenerator.generateObject(Person.class);
        JsonNode required = schema.get("required");

        assertEquals(8, required.size());
        for (String field : new String[]{"name", "age", "active", "score", "sentiment", "address", "tags", "primitiveCount"}) {
            assertTrue(field + " should be required", containsText(required, field));
        }
    }

    @Test
    public void responseFormatProducesCorrectStructure() {
        ObjectNode rf = JsonSchemaGenerator.responseFormat(Address.class, "address_schema");

        assertEquals("json_schema", rf.path("type").asText());

        JsonNode inner = rf.get("json_schema");
        assertEquals("address_schema", inner.path("name").asText());
        assertEquals(true, inner.path("strict").asBoolean());
        assertNotNull(inner.get("schema"));
        assertEquals("object", inner.path("schema").path("type").asText());
    }

    @Test
    public void generateReturnsString() {
        String json = JsonSchemaGenerator.generate(Address.class);
        JsonNode parsed = Jsons.readTree(json);

        assertEquals("object", parsed.path("type").asText());
        assertEquals(false, parsed.path("additionalProperties").asBoolean());
    }

    @Test
    public void listOListGeneratesArrayOfArrays() {
        ObjectNode schema = JsonSchemaGenerator.generateObject(ListOfLists.class);
        JsonNode matrix = schema.path("properties").path("matrix");

        assertEquals("array", matrix.path("type").asText());
        JsonNode innerItems = matrix.get("items");
        assertEquals("array", innerItems.path("type").asText());
        assertEquals("string", innerItems.path("items").path("type").asText());
    }

    public static class ListOfLists {
        private List<List<String>> matrix;
    }

    @Test
    public void listOfNestedObjects() {
        ObjectNode schema = JsonSchemaGenerator.generateObject(Team.class);
        JsonNode members = schema.path("properties").path("members");

        assertEquals("array", members.path("type").asText());
        JsonNode memberItem = members.get("items");
        assertEquals("object", memberItem.path("type").asText());
        assertNotNull(memberItem.path("properties").get("name"));
    }

    public static class Team {
        private String teamName;
        private List<Person> members;
    }

    @Test
    public void staticAndTransientFieldsExcluded() {
        ObjectNode schema = JsonSchemaGenerator.generateObject(WithStatic.class);
        JsonNode props = schema.get("properties");

        assertNotNull(props.get("normalField"));
        assertNull("static field should be excluded", props.get("staticField"));
        assertNull("transient field should be excluded", props.get("transientField"));
    }

    public static class WithStatic {
        private String normalField;
        private static String staticField = "ignore";
        private transient String transientField;
    }

    private static boolean containsText(JsonNode array, String value) {
        if (array == null || !array.isArray()) {
            return false;
        }
        for (JsonNode node : array) {
            if (value.equals(node.asText())) {
                return true;
            }
        }
        return false;
    }
}
