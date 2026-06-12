package cc.openxiot.product.api.specification.namespace;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cc.openxiot.product.db.specification.SpecificationRepository;
import cc.openxiot.product.db.specification.namespace.NamespaceDefinitionMapper;
import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.definition.NamespaceDefinition;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class NamespaceService {

    @Inject
    SpecificationRepository repository;

    public void add(NamespaceDefinition def, Creator creator) {
        if (repository.find(def.organization(), def.namespace()).isPresent()) {
            throw new IllegalArgumentException("namespace already exist");
        }

        SpecificationEntity entity = new SpecificationEntity();
        entity.namespace = NamespaceDefinitionMapper.toEntity(def, creator);
        entity.persist();
    }

    public void delete(String organization, String namespace) {
        var entity = repository.find(organization, namespace);
        if (entity.isEmpty()) {
            throw new IllegalArgumentException("namespace not found");
        }

        if (!entity.get().devices.isEmpty()) {
            throw new IllegalArgumentException("namespace has devices");
        }

        if (!entity.get().services.isEmpty()) {
            throw new IllegalArgumentException("namespace has services");
        }

        if (!entity.get().properties.isEmpty()) {
            throw new IllegalArgumentException("namespace has properties");
        }

        if (!entity.get().actions.isEmpty()) {
            throw new IllegalArgumentException("namespace has actions");
        }

        if (!entity.get().events.isEmpty()) {
            throw new IllegalArgumentException("namespace has events");
        }

        if (!entity.get().formats.isEmpty()) {
            throw new IllegalArgumentException("namespace has formats");
        }

        if (!entity.get().units.isEmpty()) {
            throw new IllegalArgumentException("namespace has units");
        }

        entity.get().delete();
    }

    public void update(NamespaceDefinition def) {
        var entity = repository.find(def.organization(), def.namespace());
        if (entity.isEmpty()) {
            throw new IllegalArgumentException("namespace not found");
        }

        entity.get().namespace.description = def.description();
        entity.get().update();
    }

    public NamespaceDefinition find(String organization, String namespace) {
        var entity = repository.find(organization, namespace);
        if (entity.isEmpty()) {
            throw new IllegalArgumentException("namespace not found");
        }

        return NamespaceDefinitionMapper.toDefinition(entity.get().namespace);
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
}