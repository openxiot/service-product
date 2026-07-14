package cc.openxiot.product.db.product;

import cc.openxiot.common.person.PersonConvertor;
import cc.openxiot.product.db.product.basic.ProductBasicEntity;
import cc.openxiot.product.db.product.instance.ProductInstanceEntity;
import cn.geekcity.xiot.spec.by.Updater;
import cn.geekcity.xiot.spec.codec.vertx.instance.DeviceInstanceCodec;
import cn.geekcity.xiot.spec.instance.DeviceInstance;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.JsonObject;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;

@MongoEntity(collection = "products")
@BsonDiscriminator
@RegisterForReflection
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

        Lifecycle lifecycle = Lifecycle.of(found.lifecycle);

        if (lifecycle == Lifecycle.RELEASED) {
            throw new IllegalArgumentException("product instance is released");
        }

        if (lifecycle == Lifecycle.PREVIEW) {
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

        found.lifecycle = lifecycle.toString();
        found.updater = PersonConvertor.of(updater);

        DeviceInstance instance = DeviceInstanceCodec.decode(new JsonObject(found.content));
        instance.lifecycle(lifecycle);

        found.content = DeviceInstanceCodec.encode(instance).toString();

        update();
    }
}
