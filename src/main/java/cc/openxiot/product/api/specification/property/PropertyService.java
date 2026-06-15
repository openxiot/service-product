package cc.openxiot.product.api.specification.property;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cc.openxiot.product.db.specification.SpecificationRepository;
import cc.openxiot.product.db.specification.property.PropertyDefinitionEntity;
import cc.openxiot.product.db.specification.property.PropertyDefinitionMapper;
import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.resource.NamespacePermission;
import cn.geekcity.xiot.spec.definition.PropertyDefinition;
import cn.geekcity.xiot.spec.definition.urn.PropertyType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PropertyService {

    @Inject
    SpecificationRepository repository;

    public void add(PropertyDefinition<?> def, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(def.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        PropertyDefinitionEntity found = spec.properties.get(def.type().name());
        if (found != null) {
            throw new IllegalArgumentException("property already exist!");
        }

        PropertyDefinitionEntity entity = PropertyDefinitionMapper.toEntity(def, null);
        spec.properties.put(entity.code, entity);
        spec.update();
    }

    public void delete(PropertyType type, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        PropertyDefinitionEntity found = spec.properties.get(type.name());
        if (found == null) {
            throw new IllegalArgumentException("property not found!");
        }

        spec.properties.remove(type.name());
        spec.update();
    }

    public void update(PropertyDefinition<?> def, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(def.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        PropertyDefinitionEntity found = spec.properties.get(def.type().name());
        if (found == null) {
            throw new IllegalArgumentException("property not found!");
        }

        PropertyDefinitionEntity entity = PropertyDefinitionMapper.toEntity(def, null);
        spec.properties.put(entity.code, entity);
        spec.update();
    }

    public PropertyDefinition<?> find(PropertyType type) {
        SpecificationEntity spec = repository.findByNamespace(type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        PropertyDefinitionEntity found = spec.properties.get(type.name());
        if (found == null) {
            throw new IllegalArgumentException("property not found!");
        }

        return PropertyDefinitionMapper.toDefinition(spec.namespace.code, found);
    }

    public List<PropertyDefinition<?>> findByNamespace(String ns) {
        SpecificationEntity spec = repository.findByNamespace(ns);
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        return spec.properties.values()
                .stream()
                .map(x -> PropertyDefinitionMapper.toDefinition(spec.namespace.code, x))
                .collect(Collectors.toList());
    }

    public List<PropertyDefinition<?>> findAll() {
        List<PropertyDefinition<?>> result = new ArrayList<>();

        List<SpecificationEntity> list = repository.listAll();

        for (SpecificationEntity spec : list) {
            List<PropertyDefinition<?>> properties = spec.properties.values().stream()
                    .map(x -> PropertyDefinitionMapper.toDefinition(spec.namespace.code, x))
                    .collect(Collectors.toList());

            result.addAll(properties);
        }

        return result;
    }
}