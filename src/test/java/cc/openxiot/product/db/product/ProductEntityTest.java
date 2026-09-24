package cc.openxiot.product.db.product;

import static org.junit.jupiter.api.Assertions.*;

import cc.openxiot.product.db.product.controller.ProductControllerEntity;
import cc.openxiot.product.db.product.controller.ProductControllerVersion;
import cc.openxiot.product.db.product.instance.ProductInstanceEntity;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProductEntityTest {

    private static final String CONTROLLER_INSTANCE = "urn:xiot-spec:device:lightbulb:00000002:geekcity:lightbulb-v2:2";

    private static ProductControllerEntity controller(String category, int versionCode) {
        ProductControllerEntity controller = new ProductControllerEntity();
        controller.instance = CONTROLLER_INSTANCE;
        controller.category = category;
        controller.version = new ProductControllerVersion("v" + versionCode, versionCode);
        controller.lifecycle = "development";
        return controller;
    }

    @Test
    void findInstanceShouldReturnMatchingVersion() {
        ProductEntity product = new ProductEntity();
        product.instances = new ArrayList<>();
        ProductInstanceEntity v1 = new ProductInstanceEntity();
        v1.version = 1;
        ProductInstanceEntity v2 = new ProductInstanceEntity();
        v2.version = 2;
        product.instances.add(v1);
        product.instances.add(v2);

        ProductInstanceEntity found = product.findInstance(2);
        assertNotNull(found);
        assertEquals(2, found.version);
    }

    @Test
    void findInstanceShouldReturnNullForNonMatchingVersion() {
        ProductEntity product = new ProductEntity();
        product.instances = new ArrayList<>();
        ProductInstanceEntity v1 = new ProductInstanceEntity();
        v1.version = 1;
        product.instances.add(v1);

        assertNull(product.findInstance(99));
    }

    @Test
    void findInstanceShouldReturnNullWhenInstancesIsNull() {
        ProductEntity product = new ProductEntity();
        product.instances = null;

        assertNull(product.findInstance(1));
    }

    @Test
    void deleteInstanceShouldThrowWhenInstanceNotFound() {
        ProductEntity product = new ProductEntity();
        product.instances = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> product.deleteInstance(1));
    }

    @Test
    void deleteInstanceShouldThrowWhenReleased() {
        ProductEntity product = new ProductEntity();
        product.instances = new ArrayList<>();
        ProductInstanceEntity instance = new ProductInstanceEntity();
        instance.version = 1;
        instance.lifecycle = "released";
        product.instances.add(instance);

        assertThrows(IllegalArgumentException.class, () -> product.deleteInstance(1));
    }

    @Test
    void deleteInstanceShouldThrowWhenPreview() {
        ProductEntity product = new ProductEntity();
        product.instances = new ArrayList<>();
        ProductInstanceEntity instance = new ProductInstanceEntity();
        instance.version = 1;
        instance.lifecycle = "preview";
        product.instances.add(instance);

        assertThrows(IllegalArgumentException.class, () -> product.deleteInstance(1));
    }

    @Test
    void updateInstanceLifecycleShouldThrowWhenInstanceNotFound() {
        ProductEntity product = new ProductEntity();
        product.instances = new ArrayList<>();

        assertThrows(IllegalArgumentException.class,
                () -> product.updateInstanceLifecycle(1, cn.geekcity.xiot.spec.lifecycle.Lifecycle.RELEASED, null));
    }

    @Test
    void findControllerShouldMatchInstanceCategoryAndVersionCode() {
        ProductEntity product = new ProductEntity();
        product.controllers = new ArrayList<>();
        product.controllers.add(controller("mobile", 1));
        product.controllers.add(controller("mobile", 2));
        product.controllers.add(controller("desktop", 1));

        assertNotNull(product.findController(CONTROLLER_INSTANCE, "mobile", 2));
        assertEquals(2, product.findController(CONTROLLER_INSTANCE, "mobile", 2).version.code);
        assertNull(product.findController(CONTROLLER_INSTANCE, "mobile", 3));
        assertNull(product.findController(CONTROLLER_INSTANCE, "tablet", 1));
    }

    @Test
    void findControllerShouldNotMatchOtherInstance() {
        ProductEntity product = new ProductEntity();
        product.controllers = new ArrayList<>();
        product.controllers.add(controller("mobile", 1));

        assertNull(product.findController(
                "urn:xiot-spec:device:lightbulb:00000002:geekcity:lightbulb-v2:3", "mobile", 1));
    }

    @Test
    void findControllerShouldReturnNullWhenControllersIsNull() {
        ProductEntity product = new ProductEntity();
        product.controllers = null;

        assertNull(product.findController(CONTROLLER_INSTANCE, "mobile", 1));
    }

    @Test
    void deleteControllerShouldThrowWhenControllerNotFound() {
        ProductEntity product = new ProductEntity();
        product.controllers = new ArrayList<>();

        assertThrows(IllegalArgumentException.class,
                () -> product.deleteController(CONTROLLER_INSTANCE, "mobile", 1));
    }

    @Test
    void deleteControllerShouldThrowWhenReleased() {
        ProductEntity product = new ProductEntity();
        product.controllers = new ArrayList<>();
        ProductControllerEntity released = controller("mobile", 1);
        released.lifecycle = "released";
        product.controllers.add(released);

        assertThrows(IllegalArgumentException.class,
                () -> product.deleteController(CONTROLLER_INSTANCE, "mobile", 1));
    }

    @Test
    void deleteControllerShouldThrowWhenPreview() {
        ProductEntity product = new ProductEntity();
        product.controllers = new ArrayList<>();
        ProductControllerEntity preview = controller("mobile", 1);
        preview.lifecycle = "preview";
        product.controllers.add(preview);

        assertThrows(IllegalArgumentException.class,
                () -> product.deleteController(CONTROLLER_INSTANCE, "mobile", 1));
    }

    @Test
    void updateControllerLifecycleShouldThrowWhenControllerNotFound() {
        ProductEntity product = new ProductEntity();
        product.controllers = new ArrayList<>();

        assertThrows(IllegalArgumentException.class,
                () -> product.updateControllerLifecycle(CONTROLLER_INSTANCE, "mobile", 1,
                        cn.geekcity.xiot.spec.lifecycle.Lifecycle.RELEASED, null));
    }
}
