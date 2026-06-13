package cc.openxiot.product.api.product.basic;

import cc.openxiot.product.db.product.basic.ProductBasicMapper;
import cc.openxiot.product.db.product.ProductRepository;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;
import cn.geekcity.xiot.spec.product.basic.ProductBasic;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductBasicService {

    @Inject
    ProductRepository repository;

    public void add(ProductBasic product) {
        if (repository.findOptionalByOrgAndModel(product.organization(), product.model()).isPresent()) {
            throw new IllegalArgumentException("product already exist");
        }

        var entity = ProductBasicMapper.toEntity(product);
        repository.persist(entity);
    }

    public void delete(String id) {
        var entity = repository.findById(new ObjectId(id));
        if (entity == null) {
            throw new IllegalArgumentException("product not found");
        }

        if (entity.basic.lifecycle == Lifecycle.RELEASED) {
            throw new IllegalArgumentException("product is released");
        }

        if (entity.basic.lifecycle == Lifecycle.PREVIEW) {
            throw new IllegalArgumentException("product is preview");
        }

        if (entity.instances == null) {
            entity.instances = new ArrayList<>();
        }

        if (!entity.instances.isEmpty()) {
            throw new IllegalArgumentException("product has instances");
        }

        repository.deleteById(new ObjectId(id));
    }

    public void update(ProductBasic product) {
        var entity = repository.findById(new ObjectId(product.id()));
        if (entity == null) {
            throw new IllegalArgumentException("product not found");
        }

        if (entity.basic.lifecycle == Lifecycle.RELEASED) {
            throw new IllegalArgumentException("product is released");
        }

        if (entity.basic.lifecycle == Lifecycle.PREVIEW) {
            throw new IllegalArgumentException("product is preview");
        }

        entity.basic.organization = product.organization();
        entity.basic.model = product.model();
        entity.basic.template = product.template().toString();
        entity.basic.icon = product.icon();
        entity.basic.name = product.name();
        entity.basic.upgrade = product.upgrade();
        entity.basic.protocol = product.protocol();
        entity.basic.lifecycle = product.lifecycle();
        entity.basic.naming = product.naming();
        entity.basic.creator = product.creator();
        entity.basic.updater = product.updater();

        repository.update(entity);
    }

    public ProductBasic findById(String id) {
        var entity = repository.findById(new ObjectId(id));
        return ProductBasicMapper.toProduct(entity);
    }

    public List<ProductBasic> findByOrganization(String organizationId) {
        return repository.findByOrg(organizationId).stream()
                .map(ProductBasicMapper::toProduct)
                .toList();
    }

    public List<ProductBasic> findAll() {
        return repository.listAll().stream()
                .map(ProductBasicMapper::toProduct)
                .collect(Collectors.toList());
    }
}