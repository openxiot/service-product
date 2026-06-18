package cc.openxiot.product.prepared;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cc.openxiot.product.db.specification.action.ActionDefinitionMapper;
import cc.openxiot.product.db.specification.device.DeviceDefinitionMapper;
import cc.openxiot.product.db.specification.event.EventDefinitionMapper;
import cc.openxiot.product.db.specification.namespace.NamespaceDefinitionMapper;
import cc.openxiot.product.db.specification.property.PropertyDefinitionMapper;
import cc.openxiot.product.db.specification.service.ServiceDefinitionMapper;
import cn.geekcity.xiot.spec.definition.*;
import cn.geekcity.xiot.spec.definition.urn.ActionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class SpecificationPrepared {

    public static String HOMEKIT_SPEC = "homekit-spec";

    @Inject
    ObjectMapper objectMapper;

    private final Map<String, SpecificationEntity> specifications = new HashMap<>();

    public boolean contains(String namespace) {
        return HOMEKIT_SPEC.equals(namespace);
    }

    public NamespaceDefinition getNamespaceDefinition(String namespace) throws IOException {
        SpecificationEntity entity = getSpecification(namespace);
        if (entity == null) {
            throw new IOException("Specification not found: " + namespace);
        }

        return NamespaceDefinitionMapper.toDefinition(entity.namespace);
    }

    public List<DeviceDefinition> getDevices(String namespace) throws IOException {
        SpecificationEntity entity = getSpecification(namespace);
        if (entity == null) {
            throw new IOException("Specification not found: " + namespace);
        }

        return entity.devices.values().stream()
                .map(x -> DeviceDefinitionMapper.toDefinition(entity.namespace.code, x))
                .toList();
    }

    public List<ServiceDefinition> getServices(String namespace) throws IOException {
        SpecificationEntity entity = getSpecification(namespace);
        if (entity == null) {
            throw new IOException("Specification not found: " + namespace);
        }

        return entity.services.values().stream()
                .map(x -> ServiceDefinitionMapper.toDefinition(entity.namespace.code, x))
                .toList();
    }

    public List<PropertyDefinition<?>> getProperties(String namespace) throws IOException {
        SpecificationEntity spec = getSpecification(namespace);
        if (spec == null) {
            throw new IOException("Specification not found: " + namespace);
        }

        return spec.properties.values().stream()
                .map(x -> PropertyDefinitionMapper.toDefinition(spec.namespace.code, x))
                .collect(Collectors.toList());
    }

    public ActionDefinition getAction(ActionType type) throws IOException {
        SpecificationEntity spec = getSpecification(type.ns());
        if (spec == null) {
            throw new IOException("Specification not found: " + type.ns());
        }

        var entity = spec.actions.get(type.name());
        if (entity == null) {
            throw new IOException("Action not found: " + type.name());
        }

        return ActionDefinitionMapper.toDefinition(spec.namespace.code, entity);
    }

    public List<ActionDefinition> getActions(String namespace) throws IOException {
        SpecificationEntity entity = getSpecification(namespace);
        if (entity == null) {
            throw new IOException("Specification not found: " + namespace);
        }

        return entity.actions.values().stream()
                .map(x -> ActionDefinitionMapper.toDefinition(entity.namespace.code, x))
                .toList();
    }

    public List<EventDefinition> getEvents(String namespace) throws IOException {
        SpecificationEntity entity = getSpecification(namespace);
        if (entity == null) {
            throw new IOException("Specification not found: " + namespace);
        }

        return entity.events.values().stream()
                .map(x -> EventDefinitionMapper.toDefinition(entity.namespace.code, x))
                .toList();
    }

    private SpecificationEntity getSpecification(String namespace) throws IOException {
        SpecificationEntity entity = specifications.get(namespace);
        if (entity == null) {
            String path = "/specifications/" + namespace + "/" + namespace + ".json";
            try (InputStream is = getClass().getResourceAsStream(path)) {
                entity = objectMapper.readValue(is, SpecificationEntity.class);
                specifications.put(entity.namespace.code, entity);
            }
        }

        return entity;
    }
}
