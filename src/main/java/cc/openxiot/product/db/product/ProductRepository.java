package cc.openxiot.product.db.product;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductRepository implements PanacheMongoRepository<ProductEntity> {
    // 自带所有 CRUD 方法：findById、persist、delete、listAll...

    public List<ProductEntity> findByOrg(String organizationId) {
        return list("organization = ?1", organizationId);
    }

    public ProductEntity findByOrgAndModel(String organizationId, String model) {
        return find("basic.organization = ?1 and basic.model = ?2", organizationId, model).firstResult();
    }

    public Optional<ProductEntity> findOptionalByOrgAndModel(String organizationId, String model) {
        return find("basic.organization = ?1 and basic.model = ?2", organizationId, model).firstResultOptional();
    }
}