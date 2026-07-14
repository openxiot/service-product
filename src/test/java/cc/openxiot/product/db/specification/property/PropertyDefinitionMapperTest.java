package cc.openxiot.product.db.specification.property;

import static org.junit.jupiter.api.Assertions.*;

import cn.geekcity.xiot.spec.codec.vertx.definition.ValueLengthCodec;
import cn.geekcity.xiot.spec.codec.vertx.definition.ValueListCodec;
import cn.geekcity.xiot.spec.codec.vertx.definition.ValueRangeCodec;
import cn.geekcity.xiot.spec.definition.PropertyDefinition;
import cn.geekcity.xiot.spec.definition.property.Access;
import cn.geekcity.xiot.spec.definition.property.ValueLength;
import cn.geekcity.xiot.spec.definition.property.ValueList;
import cn.geekcity.xiot.spec.definition.property.ValueRange;
import cn.geekcity.xiot.spec.definition.property.data.DataFormat;
import cn.geekcity.xiot.spec.definition.urn.PropertyType;
import io.vertx.core.json.JsonArray;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

@SuppressWarnings("rawtypes")
class PropertyDefinitionMapperTest {

    @Test
    void shouldMapDefinitionToEntityWithoutConstraint() {
        PropertyType type = new PropertyType("test-ns", "brightness", 1);
        PropertyDefinition<?> def = new PropertyDefinition<>(
                type, Map.of("en", "Brightness"),
                Access.valueOf(List.of("read", "write")),
                DataFormat.from("uint8"), null, "percent"
        );
        def.lifecycle("development");

        PropertyDefinitionEntity entity = PropertyDefinitionMapper.toEntity(def);

        assertNotNull(entity);
        assertEquals("brightness", entity.code);
        assertEquals(1, entity.value);
        assertEquals(Map.of("en", "Brightness"), entity.description);
        assertEquals("uint8", entity.format);
        assertEquals(List.of("read", "write"), entity.access);
        assertEquals("percent", entity.unit);
        assertEquals("development", entity.lifecycle);
        assertNotNull(entity.constraintValue);
        assertEquals("none", entity.constraintValue.type);
    }

    @Test
    void shouldMapEntityToDefinitionWithoutConstraint() {
        PropertyDefinitionEntity entity = new PropertyDefinitionEntity();
        entity.code = "temperature";
        entity.value = 2;
        entity.description = Map.of("en", "Temperature");
        entity.format = "float";
        entity.access = List.of("read");
        entity.unit = "celsius";
        entity.lifecycle = "released";
        entity.constraintValue = new PropertyDefinitionEntity.ConstraintValueEntity();
        entity.constraintValue.type = "none";

        PropertyDefinition<?> def = PropertyDefinitionMapper.toDefinition("test-ns", entity);

        assertNotNull(def);
        assertEquals("test-ns", def.type().ns());
        assertEquals("temperature", def.type().name());
        assertEquals(2, def.type().value());
        assertEquals(Map.of("en", "Temperature"), def.description());
        assertEquals("float", def.format().toString());
        assertEquals("celsius", def.unit());
        assertEquals("released", def.lifecycle().toString());
    }

    @Test
    void shouldMapWithValueListConstraint() {
        PropertyType type = new PropertyType("test-ns", "mode", 3);
        JsonArray listValues = new JsonArray("[1, 2, 3]");
        ValueList<?> valueList = ValueListCodec.decode(DataFormat.from("uint8"), listValues);
        PropertyDefinition<?> def = new PropertyDefinition<>(
                type, Map.of("en", "Mode"),
                Access.valueOf(List.of("read", "write")),
                DataFormat.from("uint8"), valueList, ""
        );
        def.lifecycle("development");

        PropertyDefinitionEntity entity = PropertyDefinitionMapper.toEntity(def);

        assertNotNull(entity);
        assertEquals("list", entity.constraintValue.type);
        assertNotNull(entity.constraintValue.list);

        PropertyDefinition<?> result = PropertyDefinitionMapper.toDefinition("test-ns", entity);
        assertNotNull(result);
        assertInstanceOf(ValueList.class, result.constraintValue());
    }

    @Test
    void shouldMapWithValueRangeConstraint() {
        PropertyType type = new PropertyType("test-ns", "volume", 4);
        JsonArray rangeValues = new JsonArray("[0, 100]");
        ValueRange<?> valueRange = ValueRangeCodec.decode(DataFormat.from("uint8"), rangeValues);
        PropertyDefinition<?> def = new PropertyDefinition<>(
                type, Map.of("en", "Volume"),
                Access.valueOf(List.of("read", "write")),
                DataFormat.from("uint8"), valueRange, ""
        );
        def.lifecycle("development");

        PropertyDefinitionEntity entity = PropertyDefinitionMapper.toEntity(def);

        assertNotNull(entity);
        assertEquals("range", entity.constraintValue.type);
        assertNotNull(entity.constraintValue.range);

        PropertyDefinition<?> result = PropertyDefinitionMapper.toDefinition("test-ns", entity);
        assertNotNull(result);
        assertInstanceOf(ValueRange.class, result.constraintValue());
    }

    @Test
    void shouldMapWithValueLengthConstraint() {
        PropertyType type = new PropertyType("test-ns", "name", 5);
        JsonArray lengthValues = new JsonArray("[1, 64]");
        ValueLength<?> valueLength = ValueLengthCodec.decode(lengthValues);
        PropertyDefinition<?> def = new PropertyDefinition<>(
                type, Map.of("en", "Name"),
                Access.valueOf(List.of("read", "write")),
                DataFormat.from("string"), valueLength, ""
        );
        def.lifecycle("development");

        PropertyDefinitionEntity entity = PropertyDefinitionMapper.toEntity(def);

        assertNotNull(entity);
        assertEquals("length", entity.constraintValue.type);
        assertNotNull(entity.constraintValue.length);

        PropertyDefinition<?> result = PropertyDefinitionMapper.toDefinition("test-ns", entity);
        assertNotNull(result);
        assertInstanceOf(ValueLength.class, result.constraintValue());
    }

    @Test
    void shouldMapWithMembers() {
        PropertyType type = new PropertyType("test-ns", "color", 6);
        PropertyDefinition<?> def = new PropertyDefinition<>(
                type, Map.of("en", "Color"),
                Access.valueOf(List.of("read")),
                DataFormat.from("uint8"), null, ""
        );
        def.members(List.of(
                new PropertyType("test-ns", "red", 0),
                new PropertyType("test-ns", "green", 0),
                new PropertyType("test-ns", "blue", 0)
        ));
        def.lifecycle("development");

        PropertyDefinitionEntity entity = PropertyDefinitionMapper.toEntity(def);

        assertNotNull(entity);
        assertEquals(3, entity.members.size());

        PropertyDefinition<?> result = PropertyDefinitionMapper.toDefinition("test-ns", entity);
        assertNotNull(result);
        assertFalse(result.members().isEmpty());
        assertEquals(3, result.members().size());
    }

    @Test
    void shouldHandleNullConstraintValue() {
        PropertyDefinitionEntity entity = new PropertyDefinitionEntity();
        entity.code = "simple";
        entity.value = 0;
        entity.description = Map.of("en", "Simple");
        entity.format = "string";
        entity.access = List.of("read");
        entity.unit = "";
        entity.lifecycle = "development";

        PropertyDefinition<?> def = PropertyDefinitionMapper.toDefinition("test-ns", entity);

        assertNotNull(def);
        assertNull(def.constraintValue());
    }

    @Test
    void shouldRoundTripWithoutConstraint() {
        PropertyType type = new PropertyType("test-ns", "humidity", 7);
        PropertyDefinition<?> original = new PropertyDefinition<>(
                type, Map.of("en", "Humidity"),
                Access.valueOf(List.of("read")),
                DataFormat.from("uint8"), null, "percent"
        );
        original.lifecycle("preview");

        PropertyDefinitionEntity entity = PropertyDefinitionMapper.toEntity(original);
        PropertyDefinition<?> result = PropertyDefinitionMapper.toDefinition("test-ns", entity);

        assertEquals(original.type().ns(), result.type().ns());
        assertEquals(original.type().name(), result.type().name());
        assertEquals(original.type().value(), result.type().value());
        assertEquals(original.description(), result.description());
        assertEquals(original.format().toString(), result.format().toString());
        assertEquals(original.unit(), result.unit());
        assertEquals(original.lifecycle().toString(), result.lifecycle().toString());
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(PropertyDefinitionMapper.toEntity(null));
        assertNull(PropertyDefinitionMapper.toDefinition("ns", null));
    }
}
