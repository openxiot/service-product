package cc.openxiot.product.db.specification.event;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cn.geekcity.xiot.spec.definition.EventDefinition;
import cn.geekcity.xiot.spec.definition.ArgumentDefinition;
import cn.geekcity.xiot.spec.definition.urn.EventType;

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

    public static EventDefinition toDefinition(SpecificationEntity spec, EventDefinitionEntity entity) {
        if (entity == null) {
            return null;
        }

        EventType type = new EventType(spec.namespace.code, entity.code, entity.value);
        List<ArgumentDefinition> arguments = entity.arguments == null ? List.of() :
                entity.arguments.stream()
                        .map(x -> new ArgumentDefinition(spec.getPropertyType(x)))
                        .toList();

        EventDefinition def = new EventDefinition(type, entity.description, arguments);
        def.lifecycle(entity.lifecycle);
        return def;
    }
}