package cc.openxiot.product.db.specification.action;

import static org.junit.jupiter.api.Assertions.*;

import cn.geekcity.xiot.spec.definition.ActionDefinition;
import cn.geekcity.xiot.spec.definition.ArgumentDefinition;
import cn.geekcity.xiot.spec.definition.urn.ActionType;
import cn.geekcity.xiot.spec.definition.urn.PropertyType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ActionDefinitionMapperTest {

    @Test
    void shouldMapDefinitionToEntity() {
        ActionType type = new ActionType("test-ns", "toggle", 1);
        List<ArgumentDefinition> in = List.of(new ArgumentDefinition(new PropertyType("test-ns", "target", 0)));
        List<ArgumentDefinition> out = List.of(new ArgumentDefinition(new PropertyType("test-ns", "result", 0)));
        ActionDefinition def = new ActionDefinition(type, Map.of("en", "Toggle"), in, out);
        def.lifecycle("development");

        ActionDefinitionEntity entity = ActionDefinitionMapper.toEntity(def);

        assertNotNull(entity);
        assertEquals("toggle", entity.code);
        assertEquals(1, entity.value);
        assertEquals(Map.of("en", "Toggle"), entity.description);
        assertEquals(List.of("target"), entity.in);
        assertEquals(List.of("result"), entity.out);
        assertEquals("development", entity.lifecycle);
    }

    @Test
    void shouldMapEntityToDefinition() {
        ActionDefinitionEntity entity = new ActionDefinitionEntity();
        entity.code = "dim";
        entity.value = 2;
        entity.description = Map.of("en", "Dim");
        entity.in = List.of("brightness");
        entity.out = List.of("current-brightness");
        entity.lifecycle = "released";

        ActionDefinition def = ActionDefinitionMapper.toDefinition("test-ns", entity);

        assertNotNull(def);
        assertEquals("test-ns", def.type().ns());
        assertEquals("dim", def.type().name());
        assertEquals(2, def.type().value());
        assertEquals(Map.of("en", "Dim"), def.description());
        assertEquals(1, def.in().size());
        assertEquals("brightness", def.in().getFirst().type().name());
        assertEquals(1, def.out().size());
        assertEquals("current-brightness", def.out().getFirst().type().name());
        assertEquals("released", def.lifecycle().toString());
    }

    @Test
    void shouldHandleNullInOutLists() {
        ActionDefinitionEntity entity = new ActionDefinitionEntity();
        entity.code = "noop";
        entity.value = 0;
        entity.description = Map.of("en", "No-op");
        entity.in = null;
        entity.out = null;
        entity.lifecycle = "development";

        ActionDefinition def = ActionDefinitionMapper.toDefinition("test-ns", entity);

        assertNotNull(def);
        assertTrue(def.in().isEmpty());
        assertTrue(def.out().isEmpty());
    }

    @Test
    void shouldRoundTrip() {
        ActionType type = new ActionType("test-ns", "reset", 3);
        List<ArgumentDefinition> in = List.of(
                new ArgumentDefinition(new PropertyType("test-ns", "confirm", 0))
        );
        ActionDefinition original = new ActionDefinition(type, Map.of("en", "Reset"), in, List.of());
        original.lifecycle("preview");

        ActionDefinitionEntity entity = ActionDefinitionMapper.toEntity(original);
        ActionDefinition result = ActionDefinitionMapper.toDefinition("test-ns", entity);

        assertEquals(original.type().ns(), result.type().ns());
        assertEquals(original.type().name(), result.type().name());
        assertEquals(original.type().value(), result.type().value());
        assertEquals(original.description(), result.description());
        assertEquals(original.lifecycle().toString(), result.lifecycle().toString());
        assertEquals(original.in().size(), result.in().size());
        assertEquals("test-ns", result.in().getFirst().type().ns());
        assertTrue(result.out().isEmpty());
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(ActionDefinitionMapper.toEntity(null));
        assertNull(ActionDefinitionMapper.toDefinition("ns", null));
    }
}
