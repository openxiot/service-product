package cc.openxiot.product.db.specification.namespace;

import static org.junit.jupiter.api.Assertions.*;

import cn.geekcity.xiot.spec.definition.NamespaceDefinition;
import cn.geekcity.xiot.spec.visibility.Visibility;
import java.util.Map;
import org.junit.jupiter.api.Test;

class NamespaceDefinitionMapperTest {

    @Test
    void shouldMapEntityToDefinition() {
        NamespaceDefinitionEntity entity = new NamespaceDefinitionEntity();
        entity.code = "test-ns";
        entity.description = Map.of("en", "Test Namespace");
        entity.visibility = "public";
        entity.organization = "org-1";

        NamespaceDefinition def = NamespaceDefinitionMapper.toDefinition(entity);

        assertNotNull(def);
        assertEquals("test-ns", def.namespace());
        assertEquals(Map.of("en", "Test Namespace"), def.description());
        assertEquals(Visibility.PUBLIC, def.visibility());
        assertEquals("org-1", def.organization());
    }

    @Test
    void shouldMapDefinitionToEntity() {
        NamespaceDefinition def = new NamespaceDefinition("test-ns", Map.of("en", "Test Namespace"));
        def.visibility(Visibility.PUBLIC);
        def.organization("org-1");

        NamespaceDefinitionEntity entity = NamespaceDefinitionMapper.toEntity(def);

        assertNotNull(entity);
        assertEquals("test-ns", entity.code);
        assertEquals(Map.of("en", "Test Namespace"), entity.description);
        assertEquals("public", entity.visibility);
        assertEquals("org-1", entity.organization);
    }

    @Test
    void shouldRoundTrip() {
        NamespaceDefinition original = new NamespaceDefinition("roundtrip-ns", Map.of("en", "Round Trip"));
        original.visibility(Visibility.PRIVATE);
        original.organization("org-2");

        NamespaceDefinitionEntity entity = NamespaceDefinitionMapper.toEntity(original);
        NamespaceDefinition result = NamespaceDefinitionMapper.toDefinition(entity);

        assertEquals(original.namespace(), result.namespace());
        assertEquals(original.description(), result.description());
        assertEquals(original.visibility(), result.visibility());
        assertEquals(original.organization(), result.organization());
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(NamespaceDefinitionMapper.toEntity(null));
        assertNull(NamespaceDefinitionMapper.toDefinition((NamespaceDefinitionEntity) null));
    }
}
