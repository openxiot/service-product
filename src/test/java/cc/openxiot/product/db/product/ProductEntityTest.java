package cc.openxiot.product.db.product;

import static org.junit.jupiter.api.Assertions.*;

import cc.openxiot.product.db.product.instance.ProductInstanceEntity;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProductEntityTest {

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
}
