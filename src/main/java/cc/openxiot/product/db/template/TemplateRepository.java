package cc.openxiot.product.db.template;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TemplateRepository implements PanacheMongoRepository<TemplateEntity> {
    // 自带所有 CRUD 方法：findById、persist、delete、listAll...

    public List<TemplateEntity> findByOrganization(String organization) {
        return find("organization = ?1", organization).stream().toList();
    }

    public List<TemplateEntity> findByNamespace(String namespace) {
        return find("namespace = ?1", namespace).stream().toList();
    }

    public TemplateEntity findBy(String namespace, String organization, String model, int version) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder query = new StringBuilder();

        if (namespace != null) {
            query.append("namespace = :namespace");
            params.put("namespace", namespace);
        }

        if (organization != null) {
            if (!query.isEmpty()) {
                query.append(" and ");
            }
            query.append("organization = :organization");
            params.put("organization", organization);
        }

        if (model != null) {
            if (!query.isEmpty()) {
                query.append(" and ");
            }
            query.append("model = :model");
            params.put("model", model);
        }

        // version 是基本类型 int，不可能为 null，直接加入条件
        if (!query.isEmpty()) {
            query.append(" and ");
        }

        query.append("version = :version");
        params.put("version", version);

        PanacheQuery<TemplateEntity> panacheQuery = find(query.toString(), params);
        return panacheQuery.firstResult();
    }
}