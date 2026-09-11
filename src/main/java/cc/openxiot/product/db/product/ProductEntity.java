package cc.openxiot.product.db.product;

import cc.openxiot.common.person.PersonConvertor;
import cc.openxiot.product.db.product.basic.ProductBasicEntity;
import cc.openxiot.product.db.product.controller.ProductControllerEntity;
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

@MongoEntity(database = "product", collection = "products")
@BsonDiscriminator
@RegisterForReflection
public class ProductEntity extends PanacheMongoEntity {

    public ProductBasicEntity basic;

    public List<ProductInstanceEntity> instances;

    public List<ProductControllerEntity> controllers;

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

    public ProductControllerEntity findController(String instance, String category, int versionCode) {
        if (controllers == null) {
            return null;
        }

        for (ProductControllerEntity controller : controllers) {
            if (controller.matches(instance, category, versionCode)) {
                return controller;
            }
        }

        return null;
    }

    public void deleteController(String instance, String category, int versionCode) {
        ProductControllerEntity found = findController(instance, category, versionCode);
        if (found == null) {
            throw new IllegalArgumentException("product controller not found");
        }

        checkControllerEditable(found);

        controllers.removeIf(entity -> entity.matches(instance, category, versionCode));

        update();
    }

    public void updateControllerLifecycle(String instance, String category, int versionCode, Lifecycle lifecycle, Updater updater) {
        ProductControllerEntity found = findController(instance, category, versionCode);
        if (found == null) {
            throw new IllegalArgumentException("product controller not found");
        }

        found.lifecycle = lifecycle.toString();
        found.updater = PersonConvertor.of(updater);

        update();
    }

    // 已上线/已提审的控制页不允许改动
    public void checkControllerEditable(ProductControllerEntity controller) {
        Lifecycle lifecycle = Lifecycle.of(controller.lifecycle);

        if (lifecycle == Lifecycle.RELEASED) {
            throw new IllegalArgumentException("product controller is released");
        }

        if (lifecycle == Lifecycle.PREVIEW) {
            throw new IllegalArgumentException("product controller is preview");
        }
    }
}
