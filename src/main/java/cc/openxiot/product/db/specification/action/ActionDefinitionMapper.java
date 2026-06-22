package cc.openxiot.product.db.specification.action;

import cn.geekcity.xiot.spec.definition.ActionDefinition;
import cn.geekcity.xiot.spec.definition.ArgumentDefinition;
import cn.geekcity.xiot.spec.definition.urn.ActionType;
import cn.geekcity.xiot.spec.definition.urn.PropertyType;

import java.util.List;

public class ActionDefinitionMapper {

    public static ActionDefinitionEntity toEntity(ActionDefinition definition) {
        if (definition == null) {
            return null;
        }

        ActionDefinitionEntity entity = new ActionDefinitionEntity();
        entity.code = definition.type().name();
        entity.value = definition.type().value();
        entity.description = definition.description();
        entity.in = definition.in().stream().map(x -> x.type().name()).toList();
        entity.out = definition.out().stream().map(x -> x.type().name()).toList();
        entity.lifecycle = definition.lifecycle().toString();

        return entity;
    }

    public static ActionDefinition toDefinition(String ns, ActionDefinitionEntity entity) {
        if (entity == null) {
            return null;
        }

        ActionType type = new ActionType(ns, entity.code, entity.value);
        List<ArgumentDefinition> in = entity.in == null ? List.of() :
                entity.in.stream()
                        .map(x -> new ArgumentDefinition(new PropertyType(ns, x, 0)))
                        .toList();
        List<ArgumentDefinition> out = entity.out == null ? List.of() :
                entity.out.stream()
                        .map(x -> new ArgumentDefinition(new PropertyType(ns, x, 0)))
                        .toList();

        ActionDefinition def = new ActionDefinition(type, entity.description, in, out);
        def.lifecycle(entity.lifecycle);
        return def;
    }
}