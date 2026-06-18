package cc.openxiot.product.api.specification.action;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cc.openxiot.product.db.specification.SpecificationRepository;
import cc.openxiot.product.db.specification.action.ActionDefinitionEntity;
import cc.openxiot.product.db.specification.action.ActionDefinitionMapper;
import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.resource.NamespacePermission;
import cn.geekcity.xiot.spec.definition.ActionDefinition;
import cn.geekcity.xiot.spec.definition.urn.ActionType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ActionService {

    @Inject
    SpecificationRepository repository;

    public void add(ActionDefinition def, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(def.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        ActionDefinitionEntity found = spec.actions.get(def.type().name());
        if (found != null) {
            throw new IllegalArgumentException("action already exist!");
        }

        ActionDefinitionEntity entity = ActionDefinitionMapper.toEntity(def);
        spec.actions.put(entity.code, entity);
        spec.update();
    }

    public void delete(ActionType type, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        ActionDefinitionEntity found = spec.actions.get(type.name());
        if (found == null) {
            throw new IllegalArgumentException("action not found!");
        }

        spec.actions.remove(type.name());
        spec.update();
    }

    public void update(ActionDefinition def, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(def.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        ActionDefinitionEntity found = spec.actions.get(def.type().name());
        if (found == null) {
            throw new IllegalArgumentException("action not found!");
        }

        ActionDefinitionEntity entity = ActionDefinitionMapper.toEntity(def);
        spec.actions.put(entity.code, entity);
        spec.update();
    }

    public ActionDefinition find(ActionType type) {
        SpecificationEntity spec = repository.findByNamespace(type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        ActionDefinitionEntity found = spec.actions.get(type.name());
        if (found == null) {
            throw new IllegalArgumentException("action not found!");
        }

        return ActionDefinitionMapper.toDefinition(spec.namespace.code, found);
    }

    public List<ActionDefinition> findByNamespace(String ns) {
        SpecificationEntity spec = repository.findByNamespace(ns);
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        return spec.actions.values()
                .stream()
                .map(x -> ActionDefinitionMapper.toDefinition(spec.namespace.code, x))
                .collect(Collectors.toList());
    }

    public List<ActionDefinition> findAll() {
        List<ActionDefinition> result = new ArrayList<>();

        List<SpecificationEntity> list = repository.listAll();

        for (SpecificationEntity spec : list) {
            if (spec.actions != null) {
                List<ActionDefinition> actions = spec.actions.values()
                        .stream()
                        .map(x -> ActionDefinitionMapper.toDefinition(spec.namespace.code, x))
                        .toList();
                result.addAll(actions);
            }
        }

        return result;
    }
}