package cc.openxiot.product.db.specification;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SpecificationRepository implements PanacheMongoRepository<SpecificationEntity> {
    // 自带所有 CRUD 方法：findById、persist、delete、listAll...

    public Optional<SpecificationEntity> findOptional(String organization, String namespace) {
        return find("organization = ?1 and namespace.code = ?2", organization, namespace).firstResultOptional();
    }

    public Optional<SpecificationEntity> findOptional(String namespace) {
        return find("namespace.code = ?2", namespace).firstResultOptional();
    }

    public SpecificationEntity findByNamespace(String namespace) {
        return findOptional(namespace).orElse(null);
    }

    public List<SpecificationEntity> findByOrganization(String organization) {
        return find("organization = ?1", organization).stream().toList();
    }
}