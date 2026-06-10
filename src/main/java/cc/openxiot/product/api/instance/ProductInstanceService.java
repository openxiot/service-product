package cc.openxiot.product.api.instance;

import cc.openxiot.product.db.product.ProductMapper;
import cc.openxiot.product.db.product.ProductRepository;
import cn.geekcity.xiot.spec.product.basic.ProductBasic;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductInstanceService {

    @Inject
    ProductRepository repository;

    // 保存
    public void save(ProductBasic product) {
        var entity = ProductMapper.toEntity(product);
        repository.persist(entity);
    }

    // 根据ID查询
    public ProductBasic findById(String id) {
        var entity = repository.findById(new ObjectId(id));
        return ProductMapper.toProduct(entity);
    }

    // 查询所有
    public List<ProductBasic> findAll() {
        return repository.listAll().stream()
                .map(ProductMapper::toProduct)
                .collect(Collectors.toList());
    }

    public List<ProductBasic> findByOrganization(String organizationId) {
        return repository.findByOrganization(organizationId).stream()
                .map(ProductMapper::toProduct)
                .toList();
    }
}