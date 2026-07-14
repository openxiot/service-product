package cc.openxiot.product.db.specification.unit;

import static org.junit.jupiter.api.Assertions.*;

import cn.geekcity.xiot.spec.definition.UnitDefinition;
import cn.geekcity.xiot.spec.definition.urn.UnitType;
import java.util.Map;
import org.junit.jupiter.api.Test;

class UnitDefinitionMapperTest {

    @Test
    void shouldMapDefinitionToEntity() {
        UnitType type = new UnitType("test-ns", "celsius", 1);
        UnitDefinition def = new UnitDefinition(type, Map.of("en", "Celsius"));
        def.lifecycle("development");

        UnitDefinitionEntity entity = UnitDefinitionMapper.toEntity(def);

        assertNotNull(entity);
        assertEquals("celsius", entity.code);
        assertEquals(1, entity.value);
        assertEquals(Map.of("en", "Celsius"), entity.description);
        assertEquals("development", entity.lifecycle);
    }

    @Test
    void shouldMapEntityToDefinition() {
        UnitDefinitionEntity entity = new UnitDefinitionEntity();
        entity.code = "fahrenheit";
        entity.value = 2;
        entity.description = Map.of("en", "Fahrenheit");
        entity.lifecycle = "released";

        UnitDefinition def = UnitDefinitionMapper.toDefinition("test-ns", entity);

        assertNotNull(def);
        assertEquals("test-ns", def.type().ns());
        assertEquals("fahrenheit", def.type().name());
        assertEquals(2, def.type().value());
        assertEquals(Map.of("en", "Fahrenheit"), def.description());
        assertEquals("released", def.lifecycle().toString());
    }

    @Test
    void shouldRoundTrip() {
        UnitType type = new UnitType("test-ns", "percent", 3);
        UnitDefinition original = new UnitDefinition(type, Map.of("en", "Percent"));
        original.lifecycle("preview");

        UnitDefinitionEntity entity = UnitDefinitionMapper.toEntity(original);
        UnitDefinition result = UnitDefinitionMapper.toDefinition("test-ns", entity);

        assertEquals(original.type().ns(), result.type().ns());
        assertEquals(original.type().name(), result.type().name());
        assertEquals(original.type().value(), result.type().value());
        assertEquals(original.description(), result.description());
        assertEquals(original.lifecycle().toString(), result.lifecycle().toString());
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(UnitDefinitionMapper.toEntity(null));
        assertNull(UnitDefinitionMapper.toDefinition("ns", null));
    }
}
