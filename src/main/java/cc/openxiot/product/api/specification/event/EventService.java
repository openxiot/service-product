package cc.openxiot.product.api.specification.event;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cc.openxiot.product.db.specification.SpecificationRepository;
import cc.openxiot.product.db.specification.event.EventDefinitionEntity;
import cc.openxiot.product.db.specification.event.EventDefinitionMapper;
import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.resource.NamespacePermission;
import cn.geekcity.xiot.spec.definition.EventDefinition;
import cn.geekcity.xiot.spec.definition.urn.EventType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class EventService {

    @Inject
    SpecificationRepository repository;

    public void add(EventDefinition def, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(def.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        EventDefinitionEntity found = spec.events.get(def.type().name());
        if (found != null) {
            throw new IllegalArgumentException("event already exist!");
        }

        EventDefinitionEntity entity = EventDefinitionMapper.toEntity(def);
        spec.events.put(entity.code, entity);
        spec.update();
    }

    public void delete(EventType type, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        EventDefinitionEntity found = spec.events.get(type.name());
        if (found == null) {
            throw new IllegalArgumentException("event not found!");
        }

        spec.events.remove(type.name());
        spec.update();
    }

    public void update(EventDefinition def, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(def.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        EventDefinitionEntity found = spec.events.get(def.type().name());
        if (found == null) {
            throw new IllegalArgumentException("event not found!");
        }

        EventDefinitionEntity entity = EventDefinitionMapper.toEntity(def);
        spec.events.put(entity.code, entity);
        spec.update();
    }

    public EventDefinition find(EventType type) {
        SpecificationEntity spec = repository.findByNamespace(type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        EventDefinitionEntity found = spec.events.get(type.name());
        if (found == null) {
            throw new IllegalArgumentException("event not found!");
        }

        return EventDefinitionMapper.toDefinition(spec.namespace.code, found);
    }

    public List<EventDefinition> findByNamespace(String ns) {
        SpecificationEntity spec = repository.findByNamespace(ns);
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        return spec.events.values()
                .stream()
                .map(x -> EventDefinitionMapper.toDefinition(spec.namespace.code, x))
                .collect(Collectors.toList());
    }

    public List<EventDefinition> findAll() {
        List<EventDefinition> result = new ArrayList<>();

        List<SpecificationEntity> list = repository.listAll();

        for (SpecificationEntity spec : list) {
            List<EventDefinition> events = spec.events.values()
                    .stream()
                    .map(x -> EventDefinitionMapper.toDefinition(spec.namespace.code, x))
                    .toList();

            result.addAll(events);
        }

        return result;
    }
}