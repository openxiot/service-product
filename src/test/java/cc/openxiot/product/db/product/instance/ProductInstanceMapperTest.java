package cc.openxiot.product.db.product.instance;

import static org.junit.jupiter.api.Assertions.*;

import cc.openxiot.common.person.Person;
import cn.geekcity.xiot.spec.product.instance.ProductInstance;
import java.util.Date;
import org.junit.jupiter.api.Test;

class ProductInstanceMapperTest {

    @Test
    void shouldMapEntityToInstance() {
        ProductInstanceEntity entity = new ProductInstanceEntity();
        entity.version = 2;
        entity.type = "urn:homekit-spec:device:lightbulb:00000002:apple:lightbulb-v2:2";
        entity.content = "{}";
        entity.lifecycle = "released";
        entity.creator = new Person();
        entity.creator.id = "creator-1";
        entity.creator.name = "Alice";
        entity.creator.timestamp = new Date(1000L);
        entity.updater = new Person();
        entity.updater.id = "updater-1";
        entity.updater.name = "Bob";
        entity.updater.timestamp = new Date(2000L);

        ProductInstance instance = ProductInstanceMapper.toInstance(entity);

        assertNotNull(instance);
        assertEquals("homekit-spec", instance.type().ns());
        assertEquals("apple", instance.type().organization());
        assertEquals("lightbulb-v2", instance.type().model());
        assertEquals(2, instance.type().version());
        assertNotNull(instance.creator());
        assertEquals("creator-1", instance.creator().id());
        assertNotNull(instance.updater());
        assertEquals("updater-1", instance.updater().id());
        assertEquals("released", instance.lifecycle().toString());
    }

    @Test
    void shouldReturnNullForNullEntity() {
        assertNull(ProductInstanceMapper.toInstance(null));
    }

    @Test
    void shouldHandleNullCreatorAndUpdater() {
        ProductInstanceEntity entity = new ProductInstanceEntity();
        entity.version = 1;
        entity.type = "urn:test-ns:device:test-device:00000001:org:model:1";
        entity.content = "{}";
        entity.lifecycle = "development";
        entity.creator = new Person();
        entity.creator.id = "";
        entity.creator.name = "";
        entity.creator.timestamp = new Date(0);
        entity.updater = new Person();
        entity.updater.id = "";
        entity.updater.name = "";
        entity.updater.timestamp = new Date(0);

        ProductInstance instance = ProductInstanceMapper.toInstance(entity);

        assertNotNull(instance);
        assertEquals(1, instance.type().version());
        assertEquals("development", instance.lifecycle().toString());
    }
}
