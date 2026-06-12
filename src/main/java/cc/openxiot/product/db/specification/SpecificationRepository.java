package cc.openxiot.product.db.specification;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SpecificationRepository implements PanacheMongoRepository<SpecificationEntity> {
    // 自带所有 CRUD 方法：findById、persist、delete、listAll...

    public Optional<SpecificationEntity> find(String organization, String namespace) {
        return find("namespace.organization = ?1 and namespace.code = ?2", organization, namespace).firstResultOptional();
    }

    public List<SpecificationEntity> findByOrganization(String organization) {
        return find("namespace.organization = ?1", organization).stream().toList();
    }

//    public Optional<NamespaceEntity> findOptionalByOrgAndModel(String organizationId, String model) {
//        return find("basic.organization = ?1 and basic.model = ?2", organizationId, model).firstResultOptional();
//    }
}