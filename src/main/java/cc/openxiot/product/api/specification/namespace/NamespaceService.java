package cc.openxiot.product.api.specification.namespace;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cc.openxiot.product.db.specification.SpecificationRepository;
import cc.openxiot.product.db.specification.namespace.NamespaceDefinitionMapper;
import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.resource.NamespacePermission;
import cn.geekcity.xiot.spec.definition.NamespaceDefinition;
import cn.geekcity.xiot.spec.visibility.Visibility;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class NamespaceService {

    @Inject
    SpecificationRepository repository;

    public void add(NamespaceDefinition def) {
        if (repository.findOptionalByNamespace(def.namespace()).isPresent()) {
            throw new IllegalArgumentException("namespace already exist");
        }

        SpecificationEntity entity = new SpecificationEntity();
        entity.namespace = NamespaceDefinitionMapper.toEntity(def);
        entity.persist();
    }

    public void delete(String namespace, NamespacePermission permission) throws OxException {
        var spec = repository.findByNamespace(namespace);
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        if (!spec.devices.isEmpty()) {
            throw new IllegalArgumentException("namespace has devices");
        }

        if (!spec.services.isEmpty()) {
            throw new IllegalArgumentException("namespace has services");
        }

        if (!spec.properties.isEmpty()) {
            throw new IllegalArgumentException("namespace has properties");
        }

        if (!spec.actions.isEmpty()) {
            throw new IllegalArgumentException("namespace has actions");
        }

        if (!spec.events.isEmpty()) {
            throw new IllegalArgumentException("namespace has events");
        }

        if (!spec.formats.isEmpty()) {
            throw new IllegalArgumentException("namespace has formats");
        }

        if (!spec.units.isEmpty()) {
            throw new IllegalArgumentException("namespace has units");
        }

        spec.delete();
    }

    public void update(NamespaceDefinition def, NamespacePermission permission) throws OxException {
        var spec = repository.findByNamespace(def.namespace());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        spec.namespace.description = def.description();
        spec.namespace.visibility = def.visibility().toString();

        spec.update();
    }

    public NamespaceDefinition find(String namespace) {
        var spec = repository.findByNamespace(namespace);
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        return NamespaceDefinitionMapper.toDefinition(spec.namespace);
    }

    public List<NamespaceDefinition> findByOrganization(String organization) {
        return repository.findByOrganization(organization)
                .stream()
                .map(x -> NamespaceDefinitionMapper.toDefinition(x.namespace))
                .collect(Collectors.toList());
    }

    public List<NamespaceDefinition> findAll() {
        return repository.listAll().stream()
                .map(x -> NamespaceDefinitionMapper.toDefinition(x.namespace))
                .collect(Collectors.toList());
    }

    public List<NamespaceDefinition> findVisible(String organization) {
        return repository.listAll().stream()
                .map(x -> NamespaceDefinitionMapper.toDefinition(x.namespace))
                .filter(x -> {
                    if (x.visibility() == Visibility.PUBLIC) {
                        return true;
                    }

                    return x.organization().equals(organization);
                })
                .collect(Collectors.toList());
    }

    public List<NamespaceDefinition> findPublic() {
        return repository.listAll().stream()
                .map(x -> NamespaceDefinitionMapper.toDefinition(x.namespace))
                .filter(x -> x.visibility() == Visibility.PUBLIC)
                .collect(Collectors.toList());
    }
}