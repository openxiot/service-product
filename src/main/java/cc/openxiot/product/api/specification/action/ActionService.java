package cc.openxiot.product.api.specification.action;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cc.openxiot.product.db.specification.SpecificationRepository;
import cc.openxiot.product.db.specification.action.ActionDefinitionEntity;
import cc.openxiot.product.db.specification.action.ActionDefinitionMapper;
import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.definition.ActionDefinition;
import cn.geekcity.xiot.spec.definition.urn.ActionType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class ActionService {

    @Inject
    SpecificationRepository repository;

    public void add(ActionDefinition def, Creator creator) {
        SpecificationEntity spec = repository.find(organization, def.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        ActionDefinitionEntity found = spec.actions.get(def.type().name());
        if (found != null) {
            throw new IllegalArgumentException("action already exist!");
        }

        ActionDefinitionEntity action = ActionDefinitionMapper.toEntity(def, creator);
        spec.actions.put(action.name, action);
        spec.update();
    }

    public void delete(String organization, ActionType type) {
        SpecificationEntity spec = repository.find(organization, type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        ActionDefinitionEntity found = spec.actions.get(type.name());
        if (found == null) {
            throw new IllegalArgumentException("action not found!");
        }

        spec.actions.remove(type.name());
        spec.update();
    }

    public void update(String organization, ActionDefinition def) {
        SpecificationEntity spec = repository.find(organization, def.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        ActionDefinitionEntity found = spec.actions.get(def.type().name());
        if (found == null) {
            throw new IllegalArgumentException("action not found!");
        }

        ActionDefinitionEntity action = ActionDefinitionMapper.toEntity(def, null);
        spec.actions.put(action.name, action);
        spec.update();
    }

    public ActionDefinition find(String organization, ActionType type) {
        SpecificationEntity spec = repository.find(organization, type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        ActionDefinitionEntity found = spec.actions.get(type.name());
        if (found == null) {
            throw new IllegalArgumentException("action not found!");
        }

        return ActionDefinitionMapper.toDefinition(spec.namespace.code, found);
    }

    public List<ActionDefinition> findByNamespace(String organization, String ns) {
        SpecificationEntity spec = repository.find(organization, ns);
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        return spec.actions.values()
                .stream()
                .map(x -> ActionDefinitionMapper.toDefinition(spec.namespace.code, x))
                .collect(Collectors.toList());
    }

    public Map<String, List<ActionDefinition>> findAll() {
        Map<String, List<ActionDefinition>> result = new HashMap<>();

        List<SpecificationEntity> list = repository.listAll();

        for (SpecificationEntity spec : list) {
            List<ActionDefinition> actions = spec.actions.values()
                    .stream()
                    .map(x -> ActionDefinitionMapper.toDefinition(spec.namespace.code, x))
                    .collect(Collectors.toList());

            result.put(spec.namespace.organization, actions);
        }

        return result;
    }
}