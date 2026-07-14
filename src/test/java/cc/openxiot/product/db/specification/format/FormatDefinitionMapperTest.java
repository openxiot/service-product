package cc.openxiot.product.db.specification.format;

import static org.junit.jupiter.api.Assertions.*;

import cn.geekcity.xiot.spec.definition.FormatDefinition;
import cn.geekcity.xiot.spec.definition.urn.FormatType;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FormatDefinitionMapperTest {

    @Test
    void shouldMapDefinitionToEntity() {
        FormatType type = new FormatType("test-ns", "json", 1);
        FormatDefinition def = new FormatDefinition(type, Map.of("en", "JSON Format"));
        def.lifecycle("development");

        FormatDefinitionEntity entity = FormatDefinitionMapper.toEntity(def);

        assertNotNull(entity);
        assertEquals("json", entity.code);
        assertEquals(1, entity.value);
        assertEquals(Map.of("en", "JSON Format"), entity.description);
        assertEquals("development", entity.lifecycle);
    }

    @Test
    void shouldMapEntityToDefinition() {
        FormatDefinitionEntity entity = new FormatDefinitionEntity();
        entity.code = "xml";
        entity.value = 2;
        entity.description = Map.of("en", "XML Format");
        entity.lifecycle = "released";

        FormatDefinition def = FormatDefinitionMapper.toDefinition("test-ns", entity);

        assertNotNull(def);
        assertEquals("test-ns", def.type().ns());
        assertEquals("xml", def.type().name());
        assertEquals(2, def.type().value());
        assertEquals(Map.of("en", "XML Format"), def.description());
        assertEquals("released", def.lifecycle().toString());
    }

    @Test
    void shouldRoundTrip() {
        FormatType type = new FormatType("test-ns", "binary", 3);
        FormatDefinition original = new FormatDefinition(type, Map.of("en", "Binary Format"));
        original.lifecycle("preview");

        FormatDefinitionEntity entity = FormatDefinitionMapper.toEntity(original);
        FormatDefinition result = FormatDefinitionMapper.toDefinition("test-ns", entity);

        assertEquals(original.type().ns(), result.type().ns());
        assertEquals(original.type().name(), result.type().name());
        assertEquals(original.type().value(), result.type().value());
        assertEquals(original.description(), result.description());
        assertEquals(original.lifecycle().toString(), result.lifecycle().toString());
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(FormatDefinitionMapper.toEntity(null));
        assertNull(FormatDefinitionMapper.toDefinition("ns", null));
    }
}
