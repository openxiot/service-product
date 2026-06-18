package cc.openxiot.product.db.specification.event;

import cn.geekcity.xiot.spec.definition.EventDefinition;
import cn.geekcity.xiot.spec.definition.ArgumentDefinition;
import cn.geekcity.xiot.spec.definition.urn.EventType;
import cn.geekcity.xiot.spec.definition.urn.PropertyType;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;

import java.util.List;

public class EventDefinitionMapper {

    public static EventDefinitionEntity toEntity(EventDefinition definition) {
        if (definition == null) {
            return null;
        }

        EventDefinitionEntity entity = new EventDefinitionEntity();
        entity.code = definition.type().name();
        entity.value = definition.type().value();
        entity.description = definition.description();
        entity.arguments = definition.arguments().stream().map(x -> x.type().name()).toList();
        entity.lifecycle = definition.lifecycle().toString();

        return entity;
    }

    public static EventDefinition toDefinition(String ns, EventDefinitionEntity entity) {
        if (entity == null) {
            return null;
        }

        EventType type = new EventType(ns, entity.code, entity.value);
        List<ArgumentDefinition> arguments = entity.arguments.stream()
                .map(x -> new ArgumentDefinition(new PropertyType(ns, entity.code, entity.value)))
                .toList();

        EventDefinition def = new EventDefinition(type, entity.description, arguments);
        def.lifecycle(Lifecycle.valueOf(entity.lifecycle));
        return def;
    }
}