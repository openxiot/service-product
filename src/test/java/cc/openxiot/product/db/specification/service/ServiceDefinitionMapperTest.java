package cc.openxiot.product.db.specification.service;

import static org.junit.jupiter.api.Assertions.*;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cc.openxiot.product.db.specification.action.ActionDefinitionEntity;
import cc.openxiot.product.db.specification.event.EventDefinitionEntity;
import cc.openxiot.product.db.specification.namespace.NamespaceDefinitionEntity;
import cc.openxiot.product.db.specification.property.PropertyDefinitionEntity;
import cn.geekcity.xiot.spec.definition.ServiceDefinition;
import cn.geekcity.xiot.spec.definition.urn.PropertyType;
import cn.geekcity.xiot.spec.definition.urn.ServiceType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ServiceDefinitionMapperTest {

    private static SpecificationEntity createSpec() {
        SpecificationEntity spec = new SpecificationEntity();
        spec.namespace = new NamespaceDefinitionEntity();
        spec.namespace.code = "test-ns";

        spec.properties = new HashMap<>();
        PropertyDefinitionEntity prop1 = new PropertyDefinitionEntity();
        prop1.code = "brightness";
        prop1.value = 0;
        spec.properties.put("brightness", prop1);
        PropertyDefinitionEntity prop2 = new PropertyDefinitionEntity();
        prop2.code = "color";
        prop2.value = 0;
        spec.properties.put("color", prop2);

        spec.actions = new HashMap<>();
        ActionDefinitionEntity action = new ActionDefinitionEntity();
        action.code = "toggle";
        action.value = 0;
        spec.actions.put("toggle", action);

        spec.events = new HashMap<>();
        EventDefinitionEntity event = new EventDefinitionEntity();
        event.code = "status-change";
        event.value = 0;
        spec.events.put("status-change", event);

        return spec;
    }

    @Test
    void shouldMapDefinitionToEntity() {
        ServiceType type = new ServiceType("test-ns", "light-control", 1);
        ServiceDefinition def = new ServiceDefinition(
                type, Map.of("en", "Light Control"),
                List.of(), List.of(), List.of(), List.of(), List.of(), List.of()
        );
        def.lifecycle("development");

        ServiceDefinitionEntity entity = ServiceDefinitionMapper.toEntity(def);

        assertNotNull(entity);
        assertEquals("light-control", entity.code);
        assertEquals(1, entity.value);
        assertEquals(Map.of("en", "Light Control"), entity.description);
        assertEquals("development", entity.lifecycle);
    }

    @Test
    void shouldMapEntityToDefinition() {
        SpecificationEntity spec = createSpec();
        ServiceDefinitionEntity entity = new ServiceDefinitionEntity();
        entity.code = "fan-control";
        entity.value = 2;
        entity.description = Map.of("en", "Fan Control");
        entity.optionalProperties = List.of("brightness");
        entity.requiredProperties = List.of("color");
        entity.optionalActions = List.of("toggle");
        entity.optionalEvents = List.of("status-change");
        entity.lifecycle = "released";

        ServiceDefinition def = ServiceDefinitionMapper.toDefinition(spec, entity);

        assertNotNull(def);
        assertEquals("test-ns", def.type().ns());
        assertEquals("fan-control", def.type().name());
        assertEquals(2, def.type().value());
        assertEquals(Map.of("en", "Fan Control"), def.description());
        assertFalse(def.optionalProperties().isEmpty());
        assertFalse(def.requiredProperties().isEmpty());
        assertEquals("released", def.lifecycle().toString());
    }

    @Test
    void shouldHandleNullLists() {
        SpecificationEntity spec = createSpec();
        ServiceDefinitionEntity entity = new ServiceDefinitionEntity();
        entity.code = "basic";
        entity.value = 0;
        entity.description = Map.of("en", "Basic");
        entity.lifecycle = "development";

        ServiceDefinition def = ServiceDefinitionMapper.toDefinition(spec, entity);

        assertNotNull(def);
        assertTrue(def.optionalProperties().isEmpty());
        assertTrue(def.requiredProperties().isEmpty());
        assertTrue(def.optionalActions().isEmpty());
        assertTrue(def.requiredActions().isEmpty());
        assertTrue(def.optionalEvents().isEmpty());
        assertTrue(def.requiredEvents().isEmpty());
    }

    @Test
    void shouldRoundTrip() {
        ServiceType type = new ServiceType("test-ns", "full-service", 3);
        ServiceDefinition original = new ServiceDefinition(
                type, Map.of("en", "Full Service"),
                List.of(), List.of(), List.of(), List.of(), List.of(), List.of()
        );
        original.lifecycle("preview");

        ServiceDefinitionEntity entity = ServiceDefinitionMapper.toEntity(original);
        SpecificationEntity spec = createSpec();
        ServiceDefinition result = ServiceDefinitionMapper.toDefinition(spec, entity);

        assertEquals(original.type().ns(), result.type().ns());
        assertEquals(original.type().name(), result.type().name());
        assertEquals(original.type().value(), result.type().value());
        assertEquals(original.description(), result.description());
        assertEquals(original.lifecycle().toString(), result.lifecycle().toString());
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(ServiceDefinitionMapper.toEntity(null));
        assertNull(ServiceDefinitionMapper.toDefinition(new SpecificationEntity(), null));
    }
}
