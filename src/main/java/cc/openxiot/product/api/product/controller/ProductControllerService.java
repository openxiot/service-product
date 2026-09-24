package cc.openxiot.product.api.product.controller;

import cc.openxiot.common.person.PersonConvertor;
import cc.openxiot.product.db.product.ProductEntity;
import cc.openxiot.product.db.product.ProductRepository;
import cc.openxiot.product.db.product.controller.ProductControllerEntity;
import cc.openxiot.product.db.product.controller.ProductControllerMapper;
import cc.openxiot.product.db.product.controller.ProductControllerVersion;
import cn.geekcity.xiot.spec.by.Updater;
import cn.geekcity.xiot.spec.definition.urn.Urn;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;
import cn.geekcity.xiot.spec.product.controller.ProductController;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProductControllerService {

    @Inject
    ProductRepository repository;

    public void add(ProductController controller) {
        ProductEntity product = findProduct(controller.instance());

        if (product.controllers == null) {
            product.controllers = new ArrayList<>();
        }

        String instance = controller.instance().toString();
        if (product.findController(instance, controller.category(), controller.version().code()) != null) {
            throw new IllegalArgumentException("product controller already exist");
        }

        product.controllers.add(ProductControllerMapper.toEntity(controller));

        repository.update(product);
    }

    public void delete(Urn instance, String category, int versionCode) {
        ProductEntity product = findProduct(instance);

        product.deleteController(instance.toString(), category, versionCode);
    }

    public void update(ProductController controller) {
        ProductEntity product = findProduct(controller.instance());

        ProductControllerEntity entity = findController(product, controller.instance(), controller.category(), controller.version().code());

        product.checkControllerEditable(entity);

        entity.type = controller.type();
        if (entity.version == null) {
            entity.version = new ProductControllerVersion();
        }
        entity.version.name = controller.version().name();
        entity.format = null;
        entity.url = null;

        ProductController.Web web = controller.web();
        if (web != null) {
            entity.format = web.format();
            entity.url = web.url();
        }

        entity.updater = PersonConvertor.of(controller.updater());

        repository.update(product);
    }

    public void updateLifecycle(Urn instance, String category, int versionCode, Lifecycle lifecycle, Updater updater) {
        ProductEntity product = findProduct(instance);

        product.updateControllerLifecycle(instance.toString(), category, versionCode, lifecycle, updater);
    }

    public ProductController findOne(Urn instance, String category, int versionCode) {
        ProductEntity product = findProduct(instance);

        ProductControllerEntity entity = product.findController(instance.toString(), category, versionCode);

        return ProductControllerMapper.toController(entity);
    }

    public List<ProductController> findByInstance(Urn instance) {
        return toControllers(findProduct(instance).controllers);
    }

    public List<ProductController> findByProduct(String productId) {
        ProductEntity product = repository.findById(new ObjectId(productId));
        if (product == null) {
            throw new IllegalArgumentException("product not found");
        }

        return toControllers(product.controllers);
    }

    public List<ProductController> findAll() {
        List<ProductController> all = new ArrayList<>();

        for (ProductEntity product : repository.listAll()) {
            all.addAll(toControllers(product.controllers));
        }

        return all;
    }

    /**
     * 按设备类型（实例 urn，即 Device.type）查该产品下的控制页列表。
     * 设备类型经 product 定位到产品（org + model），返回其全部控制页；category 为空时不筛选。
     */
    public List<ProductController> findByDeviceType(String deviceType, String category) {
        Urn device = ProductControllerMapper.of(deviceType);

        ProductEntity product = findProduct(device);

        List<ProductControllerEntity> entities = product.controllers;
        if (entities == null) {
            return new ArrayList<>();
        }

        if (category != null && !category.isBlank()) {
            entities = entities.stream()
                    .filter(e -> category.equals(e.category))
                    .toList();
        }

        return toControllers(entities);
    }

    private List<ProductController> toControllers(List<ProductControllerEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(ProductControllerMapper::toController)
                .toList();
    }

    private ProductControllerEntity findController(ProductEntity product, Urn instance, String category, int versionCode) {
        ProductControllerEntity entity = product.findController(instance.toString(), category, versionCode);
        if (entity == null) {
            throw new IllegalArgumentException("product controller not found");
        }

        return entity;
    }

    private ProductEntity findProduct(Urn instance) {
        if (instance == null || instance.invalid() || instance.organization() == null || instance.model() == null) {
            throw new IllegalArgumentException("invalid product instance urn");
        }

        ProductEntity product = repository.findByOrgAndModel(instance.organization(), instance.model());
        if (product == null) {
            throw new IllegalArgumentException("product not found");
        }

        return product;
    }
}
