package cc.openxiot.product.api.instance;

import cc.openxiot.product.db.product.ProductEntity;
import cc.openxiot.product.db.product.ProductRepository;
import cc.openxiot.product.db.product.instance.ProductInstanceEntity;
import cc.openxiot.product.db.product.instance.ProductInstanceMapper;
import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;
import cn.geekcity.xiot.spec.codec.vertx.instance.DeviceInstanceCodec;
import cn.geekcity.xiot.spec.definition.urn.Urn;
import cn.geekcity.xiot.spec.instance.DeviceInstance;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;
import cn.geekcity.xiot.spec.product.instance.ProductInstance;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductInstanceService {

    @Inject
    ProductRepository repository;

    public void add(DeviceInstance instance, Creator creator) {
        var product = repository.findByOrgAndModel(instance.type().organization(), instance.type().model());
        if (product == null) {
            throw new IllegalArgumentException("product not found");
        }

        if (product.instances == null) {
            product.instances = new ArrayList<>();

            if (instance.type().version() != 1) {
                throw new IllegalArgumentException("product first instance version must be 1");
            }
        } else {
            int currentVersion = 0;
            for (ProductInstanceEntity entity : product.instances) {
                if (entity.version > currentVersion) {
                    currentVersion = entity.version;
                }
            }

            if (instance.type().version() > currentVersion) {
                throw new IllegalArgumentException("product instance version invalid: " + instance.type().version());
            }
        }

        ProductInstanceEntity entity = new ProductInstanceEntity();
        entity.version = instance.type().version();
        entity.lifecycle = Lifecycle.DEVELOPMENT;
        entity.type = instance.type().toString();
        entity.content = DeviceInstanceCodec.encode(instance).toString();
        entity.creator = creator;

        product.instances.add(entity);

        repository.persist(product);
    }

    public void delete(String productId, int version) {
        var product = repository.findById(new ObjectId(productId));
        if (product == null) {
            throw new IllegalArgumentException("product not found");
        }

        product.deleteInstance(version);
    }

    public void deleteByType(Urn urn) {
        var product = repository.findByOrgAndModel(urn.organization(), urn.model());
        if (product == null) {
            throw new IllegalArgumentException("product not found");
        }

        product.deleteInstance(urn.version());
    }

    public void update(String productId, int version, Lifecycle lifecycle, Updater updater) throws IllegalArgumentException {
        var product = repository.findById(new ObjectId(productId));
        if (product == null) {
            throw new IllegalArgumentException("product not found");
        }

        product.updateInstanceLifecycle(version, lifecycle, updater);
    }

    public void update(Urn type, Lifecycle lifecycle, Updater updater) throws IllegalArgumentException {
        var product = repository.findByOrgAndModel(type.organization(), type.model());
        if (product == null) {
            throw new IllegalArgumentException("product not found");
        }

        product.updateInstanceLifecycle(type.version(), lifecycle, updater);
    }

    public void update(DeviceInstance instance, Updater updater) throws IllegalArgumentException {
        var product = repository.findByOrgAndModel(instance.type().organization(), instance.type().model());
        if (product == null) {
            throw new IllegalArgumentException("product not found");
        }

        var found = product.findInstance(instance.type().version());
        if (found == null) {
            throw new IllegalArgumentException("product instance not found");
        }

        if (found.lifecycle == Lifecycle.RELEASED) {
            throw new IllegalArgumentException("product instance is released");
        }

        if (found.lifecycle == Lifecycle.PREVIEW) {
            throw new IllegalArgumentException("product instance is preview");
        }

        found.content = DeviceInstanceCodec.encode(instance).toString();
        found.updater = updater;

        repository.persist(product);
    }

    public DeviceInstance findInstance(String productId, int version) {
        var product = repository.findById(new ObjectId(productId));
        if (product.instances == null) {
            product.instances = new ArrayList<>();
        }

        return getInstance(product, version);
    }

    public DeviceInstance findInstanceByType(Urn urn) {
        ProductEntity product = repository.findByOrgAndModel(urn.organization(), urn.model());
        if (product == null) {
            throw new IllegalArgumentException("product not found");
        }

        return getInstance(product, urn.version());
    }

    private DeviceInstance getInstance(ProductEntity product, int version) {
        ProductInstanceEntity found = product.findInstance(version);
        if (found == null) {
            throw new IllegalArgumentException("product instance not found");
        }

        JsonObject o = new JsonObject(found.content);
        return DeviceInstanceCodec.decode(o);
    }

    public List<ProductInstance> findInstancesByModel(String organization, String model) {
        ProductEntity product = repository.findByOrgAndModel(organization, model);
        if (product == null) {
            throw new IllegalArgumentException("product not found");
        }

        return getInstances(product);
    }

    public List<ProductInstance> findInstances(String productId) {
        var product = repository.findById(new ObjectId(productId));
        if (product == null) {
            throw new IllegalArgumentException("product not found");
        }

        return getInstances(product);
    }

    public List<ProductInstance> findAll() {
        List<ProductInstance> all = new ArrayList<>();

        var products = repository.findAll().stream().toList();
        for (ProductEntity product : products) {
            all.addAll(getInstances(product));
        }

        return all;
    }

    private List<ProductInstance> getInstances(ProductEntity product) {
        if (product.instances == null) {
            product.instances = new ArrayList<>();
        }

        return product.instances.stream()
                .map(ProductInstanceMapper::toInstance)
                .collect(Collectors.toList());
    }
}