package cc.openxiot.product.db.specification;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SpecificationRepository implements PanacheMongoRepository<SpecificationEntity> {
    // 自带所有 CRUD 方法：findById、persist、delete、listAll...

    public Optional<SpecificationEntity> findOptionalByNamespace(String namespace) {
        return find("namespace.code = ?1", namespace).firstResultOptional();
    }

    public SpecificationEntity findByNamespace(String namespace) {
        SpecificationEntity entity = findOptionalByNamespace(namespace).orElse(null);
        if (entity != null) {
            if (entity.actions == null) {
                entity.actions = new HashMap<>();
            }

            if (entity.devices == null) {
                entity.devices = new HashMap<>();
            }

            if (entity.events == null) {
                entity.events = new HashMap<>();
            }

            if (entity.formats == null) {
                entity.formats = new HashMap<>();
            }

            if (entity.properties == null) {
                entity.properties = new HashMap<>();
            }

            if (entity.services == null) {
                entity.services = new HashMap<>();
            }

            if (entity.units == null) {
                entity.units = new HashMap<>();
            }
        }

        return entity;
    }

    public List<SpecificationEntity> findByOrganization(String organization) {
        return find("organization = ?1", organization).stream().toList();
    }
}