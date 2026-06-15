package cc.openxiot.product.db.product;

import cc.openxiot.product.db.product.basic.ProductBasicEntity;
import cc.openxiot.product.db.product.instance.ProductInstanceEntity;
import cn.geekcity.xiot.spec.by.Updater;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;

@MongoEntity(collection = "products")
@BsonDiscriminator
public class ProductEntity extends PanacheMongoEntity {

    public ProductBasicEntity basic;

    public List<ProductInstanceEntity> instances;

    public ProductEntity() {
    }

    public ProductInstanceEntity findInstance(int version) {
        if (instances == null) {
            return null;
        }

        for (ProductInstanceEntity instance : instances) {
            if (instance.version == version) {
                return instance;
            }
        }

        return null;
    }

    public void deleteInstance(int version) {
        ProductInstanceEntity found = findInstance(version);
        if (found == null) {
            throw new IllegalArgumentException("product instance not found");
        }

        if (found.lifecycle == Lifecycle.RELEASED) {
            throw new IllegalArgumentException("product instance is released");
        }

        if (found.lifecycle == Lifecycle.PREVIEW) {
            throw new IllegalArgumentException("product instance is preview");
        }

        instances.removeIf(entity -> entity.version == version);

        update();
    }

    public void updateInstanceLifecycle(int version, Lifecycle lifecycle, Updater updater) {
        ProductInstanceEntity found = findInstance(version);
        if (found == null) {
            throw new IllegalArgumentException("product instance not found");
        }

        found.lifecycle = lifecycle;
        found.updater = updater;

        update();
    }
}
