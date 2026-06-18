package cc.openxiot.product.prepared;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cc.openxiot.product.db.specification.action.ActionDefinitionMapper;
import cc.openxiot.product.db.specification.device.DeviceDefinitionMapper;
import cc.openxiot.product.db.specification.event.EventDefinitionMapper;
import cc.openxiot.product.db.specification.format.FormatDefinitionMapper;
import cc.openxiot.product.db.specification.namespace.NamespaceDefinitionMapper;
import cc.openxiot.product.db.specification.property.PropertyDefinitionMapper;
import cc.openxiot.product.db.specification.service.ServiceDefinitionMapper;
import cc.openxiot.product.db.specification.unit.UnitDefinitionMapper;
import cn.geekcity.xiot.spec.definition.*;
import cn.geekcity.xiot.spec.definition.urn.*;
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
    public static String BLUETOOTH_SPEC = "bluetooth-spec";

    @Inject
    ObjectMapper objectMapper;

    private final Map<String, SpecificationEntity> specifications = new HashMap<>();

    public boolean contains(String namespace) {
        return HOMEKIT_SPEC.equals(namespace) || BLUETOOTH_SPEC.equals(namespace);
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

    public DeviceDefinition getDevice(DeviceType type) throws IOException {
        SpecificationEntity spec = getSpecification(type.ns());
        if (spec == null) {
            throw new IOException("Specification not found: " + type.ns());
        }

        var entity = spec.devices.get(type.name());
        if (entity == null) {
            throw new IOException("Device not found: " + type.name());
        }

        return DeviceDefinitionMapper.toDefinition(spec.namespace.code, entity);
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

    public ServiceDefinition getService(ServiceType type) throws IOException {
        SpecificationEntity spec = getSpecification(type.ns());
        if (spec == null) {
            throw new IOException("Specification not found: " + type.ns());
        }

        var entity = spec.services.get(type.name());
        if (entity == null) {
            throw new IOException("Device not found: " + type.name());
        }

        return ServiceDefinitionMapper.toDefinition(spec.namespace.code, entity);
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

    public PropertyDefinition<?> getProperty(PropertyType type) throws IOException {
        SpecificationEntity spec = getSpecification(type.ns());
        if (spec == null) {
            throw new IOException("Specification not found: " + type.ns());
        }

        var entity = spec.properties.get(type.name());
        if (entity == null) {
            throw new IOException("Property not found: " + type.name());
        }

        return PropertyDefinitionMapper.toDefinition(spec.namespace.code, entity);
    }
    public PropertyDefinition<?> getService(PropertyType type) throws IOException {
        SpecificationEntity spec = getSpecification(type.ns());
        if (spec == null) {
            throw new IOException("Specification not found: " + type.ns());
        }

        var entity = spec.properties.get(type.name());
        if (entity == null) {
            throw new IOException("Device not found: " + type.name());
        }

        return PropertyDefinitionMapper.toDefinition(spec.namespace.code, entity);
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

    public List<EventDefinition> getEvents(String namespace) throws IOException {
        SpecificationEntity entity = getSpecification(namespace);
        if (entity == null) {
            throw new IOException("Specification not found: " + namespace);
        }

        return entity.events.values().stream()
                .map(x -> EventDefinitionMapper.toDefinition(entity.namespace.code, x))
                .toList();
    }

    public EventDefinition getEvent(EventType type) throws IOException {
        SpecificationEntity spec = getSpecification(type.ns());
        if (spec == null) {
            throw new IOException("Specification not found: " + type.ns());
        }

        var entity = spec.events.get(type.name());
        if (entity == null) {
            throw new IOException("Event not found: " + type.name());
        }

        return EventDefinitionMapper.toDefinition(spec.namespace.code, entity);
    }

    public List<FormatDefinition> getFormats(String namespace) throws IOException {
        SpecificationEntity entity = getSpecification(namespace);
        if (entity == null) {
            throw new IOException("Specification not found: " + namespace);
        }

        return entity.formats.values().stream()
                .map(x -> FormatDefinitionMapper.toDefinition(entity.namespace.code, x))
                .toList();
    }

    public FormatDefinition getFormat(FormatType type) throws IOException {
        SpecificationEntity spec = getSpecification(type.ns());
        if (spec == null) {
            throw new IOException("Specification not found: " + type.ns());
        }

        var entity = spec.formats.get(type.name());
        if (entity == null) {
            throw new IOException("Event not found: " + type.name());
        }

        return FormatDefinitionMapper.toDefinition(spec.namespace.code, entity);
    }

    public List<UnitDefinition> getUnits(String namespace) throws IOException {
        SpecificationEntity entity = getSpecification(namespace);
        if (entity == null) {
            throw new IOException("Specification not found: " + namespace);
        }

        return entity.units.values().stream()
                .map(x -> UnitDefinitionMapper.toDefinition(entity.namespace.code, x))
                .toList();
    }

    public UnitDefinition getUnit(UnitType type) throws IOException {
        SpecificationEntity spec = getSpecification(type.ns());
        if (spec == null) {
            throw new IOException("Specification not found: " + type.ns());
        }

        var entity = spec.units.get(type.name());
        if (entity == null) {
            throw new IOException("Event not found: " + type.name());
        }

        return UnitDefinitionMapper.toDefinition(spec.namespace.code, entity);
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
