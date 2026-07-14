package cc.openxiot.product.db.template;

import static org.junit.jupiter.api.Assertions.*;

import cn.geekcity.xiot.spec.codec.vertx.template.DeviceTemplateCodec;
import cn.geekcity.xiot.spec.template.DeviceTemplate;
import cn.geekcity.xiot.spec.template.TemplateSummary;
import io.vertx.core.json.JsonObject;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TemplateMapperTest {

    private static final String TEST_URN = "urn:test-ns:device:test-device:00000001:org-1:model-x:1";

    @Test
    void shouldMapDeviceTemplateToEntity() {
        JsonObject json = new JsonObject()
                .put("type", TEST_URN)
                .put("description", new JsonObject().put("en", "Device A"));
        DeviceTemplate template = DeviceTemplateCodec.decode(json);

        TemplateEntity entity = TemplateMapper.toEntity(template);

        assertNotNull(entity);
        assertEquals(TEST_URN, entity.type);
        assertEquals("test-ns", entity.ns);
        assertEquals("org-1", entity.organization);
        assertEquals("test-device", entity.name);
        assertEquals("model-x", entity.model);
        assertEquals(1, entity.version);
        assertEquals("development", entity.lifecycle);
        assertEquals(Map.of("en", "Device A"), entity.description);
        assertNotNull(entity.content);
    }

    @Test
    void shouldMapEntityToDeviceTemplate() {
        TemplateEntity entity = new TemplateEntity();
        entity.type = TEST_URN;
        entity.ns = "test-ns";
        entity.organization = "org-1";
        entity.name = "test-device";
        entity.model = "model-x";
        entity.version = 2;
        entity.lifecycle = "released";
        entity.description = Map.of("en", "Device B");

        JsonObject content = new JsonObject()
                .put("type", TEST_URN)
                .put("description", new JsonObject().put("en", "Device B"));
        entity.content = content.encode();

        DeviceTemplate template = TemplateMapper.toDefinition(entity);

        assertNotNull(template);
        assertEquals("test-ns", template.type().ns());
        assertEquals("org-1", template.type().organization());
        assertEquals("model-x", template.type().model());
        assertEquals(1, template.type().version());
        assertEquals(Map.of("en", "Device B"), template.description());
    }

    @Test
    void shouldMapSummaryEntityToSummary() {
        TemplateSummaryEntity summaryEntity = new TemplateSummaryEntity();
        summaryEntity.type = "urn:test-ns:device:test-device:00000003:org-1:model-x:3";
        summaryEntity.ns = "test-ns";
        summaryEntity.name = "test-device";
        summaryEntity.organization = "org-1";
        summaryEntity.model = "model-x";
        summaryEntity.version = 3;
        summaryEntity.lifecycle = "development";
        summaryEntity.description = Map.of("en", "Device C");

        TemplateSummary summary = TemplateMapper.toSummary(summaryEntity);

        assertNotNull(summary);
        assertEquals("test-ns", summary.type().ns());
        assertEquals("org-1", summary.type().organization());
        assertEquals("model-x", summary.type().model());
        assertEquals(3, summary.type().version());
        assertEquals("development", summary.lifecycle().toString());
        assertEquals(Map.of("en", "Device C"), summary.description());
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(TemplateMapper.toEntity(null));
        assertNull(TemplateMapper.toDefinition(null));
        assertNull(TemplateMapper.toSummary(null));
    }

    @Test
    void shouldRoundTrip() {
        JsonObject json = new JsonObject()
                .put("type", TEST_URN)
                .put("description", new JsonObject().put("en", "Device X").put("zh", "设备X"));
        DeviceTemplate original = DeviceTemplateCodec.decode(json);

        TemplateEntity entity = TemplateMapper.toEntity(original);
        DeviceTemplate result = TemplateMapper.toDefinition(entity);

        assertEquals(original.type().ns(), result.type().ns());
        assertEquals(original.type().organization(), result.type().organization());
        assertEquals(original.type().model(), result.type().model());
        assertEquals(original.type().version(), result.type().version());
        assertEquals(original.description(), result.description());
    }
}
