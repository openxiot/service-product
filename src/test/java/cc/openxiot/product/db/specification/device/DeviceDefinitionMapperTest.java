package cc.openxiot.product.db.specification.device;

import static org.junit.jupiter.api.Assertions.*;

import cn.geekcity.xiot.spec.definition.DeviceDefinition;
import cn.geekcity.xiot.spec.definition.urn.DeviceType;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DeviceDefinitionMapperTest {

    @Test
    void shouldMapDefinitionToEntity() {
        DeviceType type = new DeviceType("test-ns", "lightbulb", 1);
        DeviceDefinition def = new DeviceDefinition(type, Map.of("en", "Light Bulb"));
        def.lifecycle("development");

        DeviceDefinitionEntity entity = DeviceDefinitionMapper.toEntity(def);

        assertNotNull(entity);
        assertEquals("lightbulb", entity.code);
        assertEquals(1, entity.value);
        assertEquals(Map.of("en", "Light Bulb"), entity.description);
        assertEquals("development", entity.lifecycle);
    }

    @Test
    void shouldMapEntityToDefinition() {
        DeviceDefinitionEntity entity = new DeviceDefinitionEntity();
        entity.code = "fan";
        entity.value = 2;
        entity.description = Map.of("en", "Fan");
        entity.lifecycle = "released";

        DeviceDefinition def = DeviceDefinitionMapper.toDefinition("test-ns", entity);

        assertNotNull(def);
        assertEquals("test-ns", def.type().ns());
        assertEquals("fan", def.type().name());
        assertEquals(2, def.type().value());
        assertEquals(Map.of("en", "Fan"), def.description());
        assertEquals("released", def.lifecycle().toString());
    }

    @Test
    void shouldRoundTrip() {
        DeviceType type = new DeviceType("test-ns", "sensor", 3);
        DeviceDefinition original = new DeviceDefinition(type, Map.of("en", "Sensor", "zh", "传感器"));
        original.lifecycle("preview");

        DeviceDefinitionEntity entity = DeviceDefinitionMapper.toEntity(original);
        DeviceDefinition result = DeviceDefinitionMapper.toDefinition("test-ns", entity);

        assertEquals(original.type().ns(), result.type().ns());
        assertEquals(original.type().name(), result.type().name());
        assertEquals(original.type().value(), result.type().value());
        assertEquals(original.description(), result.description());
        assertEquals(original.lifecycle().toString(), result.lifecycle().toString());
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(DeviceDefinitionMapper.toEntity(null));
        assertNull(DeviceDefinitionMapper.toDefinition("ns", null));
    }
}
