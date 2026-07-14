package cc.openxiot.product.db.product.basic;

import static org.junit.jupiter.api.Assertions.*;

import cc.openxiot.common.person.Person;
import cc.openxiot.product.db.product.ProductEntity;
import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;
import cn.geekcity.xiot.spec.definition.urn.DeviceType;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;
import cn.geekcity.xiot.spec.name.LocalizedName;
import cn.geekcity.xiot.spec.product.basic.ProductBasic;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

class ProductBasicMapperTest {

    private static final String TEMPLATE_URN = "urn:homekit-spec:device:lightbulb:00000001:apple:lightbulb-v2:1";

    @Test
    void shouldMapProductToEntity() {
        Creator creator = new Creator().id("creator-1").name("Alice").timestamp(1000L);
        Updater updater = new Updater().id("updater-1").name("Bob").timestamp(2000L);

        ProductBasic product = new ProductBasic()
                .organization("org-1")
                .model("lightbulb-v2")
                .template(DeviceType.parse(TEMPLATE_URN))
                .icon("https://example.com/icon.png")
                .name(new LocalizedName(Map.of("en", "Light Bulb")))
                .alias(List.of(new LocalizedName(Map.of("en", "Lamp"))))
                .upgrade(List.of("v1"))
                .protocol("mqtt")
                .lifecycle(Lifecycle.DEVELOPMENT.toString())
                .creator(creator)
                .updater(updater);

        ProductEntity entity = ProductBasicMapper.toEntity(product);

        assertNotNull(entity);
        assertNotNull(entity.basic);
        assertEquals("org-1", entity.basic.organization);
        assertEquals("lightbulb-v2", entity.basic.model);
        assertEquals(TEMPLATE_URN, entity.basic.template);
        assertEquals("https://example.com/icon.png", entity.basic.icon);
        assertEquals(Map.of("en", "Light Bulb"), entity.basic.name);
        assertEquals(List.of(Map.of("en", "Lamp")), entity.basic.alias);
        assertEquals(List.of("v1"), entity.basic.upgrade);
        assertEquals("mqtt", entity.basic.protocol);
        assertEquals("development", entity.basic.lifecycle);
        assertNotNull(entity.basic.creator);
        assertEquals("creator-1", entity.basic.creator.id);
        assertNotNull(entity.basic.updater);
        assertEquals("updater-1", entity.basic.updater.id);
    }

    @Test
    void shouldMapEntityToProduct() {
        ProductEntity entity = new ProductEntity();
        entity.id = new ObjectId("507f1f77bcf86cd799439011");
        entity.basic = new ProductBasicEntity();
        entity.basic.organization = "org-1";
        entity.basic.model = "lightbulb-v2";
        entity.basic.template = TEMPLATE_URN;
        entity.basic.icon = "https://example.com/icon.png";
        entity.basic.name = Map.of("en", "Light Bulb");
        entity.basic.alias = List.of(Map.of("en", "Lamp"));
        entity.basic.upgrade = List.of("v1");
        entity.basic.protocol = "mqtt";
        entity.basic.lifecycle = "development";
        entity.basic.creator = new Person();
        entity.basic.creator.id = "creator-1";
        entity.basic.creator.name = "Alice";
        entity.basic.creator.timestamp = new Date(1000L);
        entity.basic.updater = new Person();
        entity.basic.updater.id = "updater-1";
        entity.basic.updater.name = "Bob";
        entity.basic.updater.timestamp = new Date(2000L);

        ProductBasic product = ProductBasicMapper.toProduct(entity);

        assertNotNull(product);
        assertEquals("507f1f77bcf86cd799439011", product.id());
        assertEquals("org-1", product.organization());
        assertEquals("lightbulb-v2", product.model());
        assertEquals(TEMPLATE_URN, product.template().toString());
        assertEquals("https://example.com/icon.png", product.icon());
        assertEquals(Map.of("en", "Light Bulb"), product.name().value());
        assertEquals(1, product.alias().size());
        assertEquals(Map.of("en", "Lamp"), product.alias().getFirst().value());
        assertEquals(List.of("v1"), product.upgrade());
        assertEquals("mqtt", product.protocol());
        assertEquals("development", product.lifecycle().toString());
        assertEquals("creator-1", product.creator().id());
        assertEquals("Alice", product.creator().name());
        assertEquals("updater-1", product.updater().id());
        assertEquals("Bob", product.updater().name());
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(ProductBasicMapper.toEntity(null));
        assertNull(ProductBasicMapper.toProduct(null));
    }

    @Test
    void shouldRoundTrip() {
        Creator creator = new Creator().id("c1").name("Charlie").timestamp(3000L);
        Updater updater = new Updater().id("u1").name("Diana").timestamp(4000L);

        ProductBasic original = new ProductBasic()
                .organization("org-2")
                .model("sensor-v1")
                .template(DeviceType.parse("urn:test-ns:device:sensor:00000001:org-2:sensor-v1:1"))
                .icon("icon.png")
                .name(new LocalizedName(Map.of("en", "Sensor")))
                .alias(List.of(new LocalizedName(Map.of("en", "Temp Sensor"))))
                .upgrade(List.of())
                .protocol("coap")
                .lifecycle(Lifecycle.DEVELOPMENT.toString())
                .creator(creator)
                .updater(updater);

        ProductEntity entity = ProductBasicMapper.toEntity(original);
        entity.id = new ObjectId("507f1f77bcf86cd799439022");

        ProductBasic result = ProductBasicMapper.toProduct(entity);

        assertEquals(original.organization(), result.organization());
        assertEquals(original.model(), result.model());
        assertEquals(original.template().toString(), result.template().toString());
        assertEquals(original.icon(), result.icon());
        assertEquals(original.name().value(), result.name().value());
        assertEquals(original.alias().size(), result.alias().size());
        assertEquals(original.upgrade(), result.upgrade());
        assertEquals(original.protocol(), result.protocol());
        assertEquals(original.lifecycle(), result.lifecycle());
        assertEquals(original.creator().id(), result.creator().id());
        assertEquals(original.updater().id(), result.updater().id());
    }
}
