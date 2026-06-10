package cc.openxiot.product.api.basic;

import cc.openxiot.product.db.product.ProductMapper;
import cc.openxiot.product.db.product.ProductRepository;
import cn.geekcity.xiot.spec.product.basic.ProductBasic;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductBasicService {

    @Inject
    ProductRepository repository;

    public void add(ProductBasic product) {
        var entity = ProductMapper.toEntity(product);
        repository.persist(entity);
    }

    public void remove(String id) {
        repository.deleteById(new ObjectId(id));
    }

    public void update(ProductBasic product) {
        var entity = repository.findById(new ObjectId(product.id()));

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
        return ProductMapper.toProduct(entity);
    }

    public List<ProductBasic> findByOrganization(String organizationId) {
        return repository.findByOrganization(organizationId).stream()
                .map(ProductMapper::toProduct)
                .toList();
    }


    public List<ProductBasic> findAll() {
        return repository.listAll().stream()
                .map(ProductMapper::toProduct)
                .collect(Collectors.toList());
    }
}