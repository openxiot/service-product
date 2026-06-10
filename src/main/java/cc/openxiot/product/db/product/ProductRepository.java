package cc.openxiot.product.db.product;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProductRepository implements PanacheMongoRepository<ProductEntity> {
    // 自带所有 CRUD 方法：findById、persist、delete、listAll...

    public List<ProductEntity> findByOrganization(String organizationId) {
        return list("organization = ?1", organizationId);
    }
}