package cc.openxiot.product.prepared;

import cc.openxiot.product.db.specification.action.ActionDefinitionEntity;
import cc.openxiot.product.db.specification.namespace.NamespaceDefinitionEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.InputStream;

@ApplicationScoped
public class SpecificationPrepared {

    public static String HOMEKIT_SPEC = "homekit-spec";

    @Inject
    ObjectMapper objectMapper;

    public NamespaceDefinitionEntity getNamespaceDefinition(String namespace) throws IOException {
        String path = "/specifications/" + namespace + "/" + namespace + ".json";
        try (InputStream is = getClass().getResourceAsStream(path)) {
            return objectMapper.readValue(is, NamespaceDefinitionEntity.class);
        }
    }

    public ActionDefinitionEntity getAction(String namespace) throws IOException {
        String path = "/specifications/" + namespace + "/" + namespace + ".json";
        try (InputStream is = getClass().getResourceAsStream(path)) {
            return objectMapper.readValue(is, NamespaceDefinitionEntity.class);
        }
    }
}
