package cc.openxiot.product.db.specification.event;

import static org.junit.jupiter.api.Assertions.*;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cc.openxiot.product.db.specification.namespace.NamespaceDefinitionEntity;
import cc.openxiot.product.db.specification.property.PropertyDefinitionEntity;
import cn.geekcity.xiot.spec.definition.EventDefinition;
import cn.geekcity.xiot.spec.definition.urn.EventType;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EventDefinitionMapperTest {

    private static SpecificationEntity createSpec() {
        SpecificationEntity spec = new SpecificationEntity();
        spec.namespace = new NamespaceDefinitionEntity();
        spec.namespace.code = "test-ns";
        spec.properties = new HashMap<>();
        PropertyDefinitionEntity prop = new PropertyDefinitionEntity();
        prop.code = "temperature";
        prop.value = 0;
        spec.properties.put("temperature", prop);
        return spec;
    }

    @Test
    void shouldMapDefinitionToEntity() {
        EventType type = new EventType("test-ns", "overheat", 1);
        EventDefinition def = new EventDefinition(type, Map.of("en", "Overheat"), java.util.List.of());
        def.lifecycle("development");

        EventDefinitionEntity entity = EventDefinitionMapper.toEntity(def);

        assertNotNull(entity);
        assertEquals("overheat", entity.code);
        assertEquals(1, entity.value);
        assertEquals(Map.of("en", "Overheat"), entity.description);
        assertEquals("development", entity.lifecycle);
    }

    @Test
    void shouldMapEntityToDefinition() {
        SpecificationEntity spec = createSpec();
        EventDefinitionEntity entity = new EventDefinitionEntity();
        entity.code = "alarm";
        entity.value = 2;
        entity.description = Map.of("en", "Alarm");
        entity.arguments = java.util.List.of("temperature");
        entity.lifecycle = "released";

        EventDefinition def = EventDefinitionMapper.toDefinition(spec, entity);

        assertNotNull(def);
        assertEquals("test-ns", def.type().ns());
        assertEquals("alarm", def.type().name());
        assertEquals(2, def.type().value());
        assertEquals(Map.of("en", "Alarm"), def.description());
        assertEquals(1, def.arguments().size());
        assertEquals("temperature", def.arguments().getFirst().type().name());
        assertEquals("released", def.lifecycle().toString());
    }

    @Test
    void shouldHandleNullArguments() {
        SpecificationEntity spec = createSpec();
        EventDefinitionEntity entity = new EventDefinitionEntity();
        entity.code = "simple-event";
        entity.value = 0;
        entity.description = Map.of("en", "Simple");
        entity.arguments = null;
        entity.lifecycle = "development";

        EventDefinition def = EventDefinitionMapper.toDefinition(spec, entity);

        assertNotNull(def);
        assertTrue(def.arguments().isEmpty());
    }

    @Test
    void shouldRoundTrip() {
        EventType type = new EventType("test-ns", "motion", 3);
        EventDefinition original = new EventDefinition(type, Map.of("en", "Motion Detected"), java.util.List.of());
        original.lifecycle("preview");

        EventDefinitionEntity entity = EventDefinitionMapper.toEntity(original);
        SpecificationEntity spec = createSpec();
        EventDefinition result = EventDefinitionMapper.toDefinition(spec, entity);

        assertEquals(original.type().ns(), result.type().ns());
        assertEquals(original.type().name(), result.type().name());
        assertEquals(original.type().value(), result.type().value());
        assertEquals(original.description(), result.description());
        assertEquals(original.lifecycle().toString(), result.lifecycle().toString());
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(EventDefinitionMapper.toEntity(null));
        assertNull(EventDefinitionMapper.toDefinition(new SpecificationEntity(), null));
    }
}
