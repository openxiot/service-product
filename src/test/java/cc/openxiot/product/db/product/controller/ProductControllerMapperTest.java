package cc.openxiot.product.db.product.controller;

import static org.junit.jupiter.api.Assertions.*;

import cc.openxiot.common.person.Person;
import cn.geekcity.xiot.spec.product.controller.ProductController;
import java.util.Date;
import org.junit.jupiter.api.Test;

class ProductControllerMapperTest {

    private static final String INSTANCE = "urn:xiot-spec:device:lightbulb:00000002:geekcity:lightbulb-v2:2";

    private static ProductControllerEntity entity() {
        ProductControllerEntity entity = new ProductControllerEntity();
        entity.instance = INSTANCE;
        entity.category = "mobile";
        entity.type = "web";
        entity.format = "zip";
        entity.url = "https://cdn.example.com/panel.zip";
        entity.versionName = "1.0.0";
        entity.versionCode = 3;
        entity.lifecycle = "development";
        entity.creator = new Person();
        entity.creator.id = "creator-1";
        entity.creator.name = "Alice";
        entity.creator.timestamp = new Date(1000L);
        entity.updater = new Person();
        entity.updater.id = "updater-1";
        entity.updater.name = "Bob";
        entity.updater.timestamp = new Date(2000L);
        return entity;
    }

    @Test
    void shouldMapEntityToController() {
        ProductController controller = ProductControllerMapper.toController(entity());

        assertNotNull(controller);
        assertEquals("mobile", controller.category());
        assertEquals("web", controller.type());
        assertEquals("1.0.0", controller.version().name());
        assertEquals(3, controller.version().code());
        assertEquals("development", controller.lifecycle().toString());
        assertEquals("zip", controller.web().format());
        assertEquals("https://cdn.example.com/panel.zip", controller.web().url());
        assertEquals("creator-1", controller.creator().id());
        assertEquals("updater-1", controller.updater().id());
    }

    // 产品就是靠 instance urn 反解 organization / model 定位的，这个往返必须成立
    @Test
    void shouldRoundTripInstanceUrn() {
        ProductController controller = ProductControllerMapper.toController(entity());

        assertEquals("geekcity", controller.instance().organization());
        assertEquals("lightbulb-v2", controller.instance().model());
        assertEquals(2, controller.instance().version());
        assertEquals(INSTANCE, controller.instance().toString());
    }

    @Test
    void shouldMapControllerToEntity() {
        ProductController controller = ProductControllerMapper.toController(entity());

        ProductControllerEntity mapped = ProductControllerMapper.toEntity(controller);

        assertNotNull(mapped);
        assertEquals(INSTANCE, mapped.instance);
        assertEquals("mobile", mapped.category);
        assertEquals("web", mapped.type);
        assertEquals("zip", mapped.format);
        assertEquals("https://cdn.example.com/panel.zip", mapped.url);
        assertEquals("1.0.0", mapped.versionName);
        assertEquals(3, mapped.versionCode);
        assertEquals("development", mapped.lifecycle);
        assertEquals("creator-1", mapped.creator.id);
    }

    @Test
    void shouldReturnNullForNullEntity() {
        assertNull(ProductControllerMapper.toController(null));
        assertNull(ProductControllerMapper.toEntity(null));
    }

    @Test
    void shouldOmitWebWhenFormatAndUrlAreNull() {
        ProductControllerEntity entity = entity();
        entity.format = null;
        entity.url = null;

        assertNull(ProductControllerMapper.toController(entity).web());
    }

    @Test
    void shouldTolerateMissingCreator() {
        ProductControllerEntity entity = entity();
        entity.creator = null;

        ProductController controller = ProductControllerMapper.toController(entity);

        assertNull(controller.creator());
        assertNotNull(controller.updater());
    }
}
