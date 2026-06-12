package cc.openxiot.product.db.specification.service;

import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.definition.ServiceDefinition;
import cn.geekcity.xiot.spec.definition.urn.ActionType;
import cn.geekcity.xiot.spec.definition.urn.EventType;
import cn.geekcity.xiot.spec.definition.urn.PropertyType;
import cn.geekcity.xiot.spec.definition.urn.ServiceType;

import java.util.List;

public class ServiceDefinitionMapper {

    public static ServiceDefinitionEntity toEntity(ServiceDefinition definition, Creator creator) {
        if (definition == null) {
            return null;
        }

        ServiceDefinitionEntity entity = new ServiceDefinitionEntity();
        entity.name = definition.type().name();
        entity.value = definition.type().value();
        entity.description = definition.description();
        entity.optionalProperties = definition.optionalProperties().stream().map(x -> x.type().name()).toList();
        entity.requiredProperties = definition.requiredProperties().stream().map(x -> x.type().name()).toList();
        entity.optionalActions = definition.optionalActions().stream().map(x -> x.type().name()).toList();
        entity.requiredActions = definition.requiredActions().stream().map(x -> x.type().name()).toList();
        entity.optionalEvents = definition.optionalEvents().stream().map(x -> x.type().name()).toList();
        entity.requiredEvents = definition.requiredEvents().stream().map(x -> x.type().name()).toList();

        entity.creator = creator;

        return entity;
    }

    public static ServiceDefinition toDefinition(String ns, ServiceDefinitionEntity entity) {
        if (entity == null) {
            return null;
        }

        ServiceType type = new ServiceType(ns, entity.name, entity.value);

        List<PropertyType> optionalProperties = entity.optionalProperties.stream()
                .map(x -> new PropertyType(ns, entity.name, entity.value))
                .toList();

        List<PropertyType> requiredProperties = entity.requiredProperties.stream()
                .map(x -> new PropertyType(ns, entity.name, entity.value))
                .toList();

        List<ActionType> optionalActions = entity.optionalActions.stream()
                .map(x -> new ActionType(ns, entity.name, entity.value))
                .toList();

        List<ActionType> requiredActions = entity.requiredActions.stream()
                .map(x -> new ActionType(ns, entity.name, entity.value))
                .toList();

        List<EventType> optionalEvents = entity.optionalEvents.stream()
                .map(x -> new EventType(ns, entity.name, entity.value))
                .toList();

        List<EventType> requiredEvents = entity.requiredEvents.stream()
                .map(x -> new EventType(ns, entity.name, entity.value))
                .toList();

        return new ServiceDefinition(
                type,
                entity.description,
                optionalProperties,
                requiredProperties,
                optionalActions,
                requiredActions,
                optionalEvents,
                requiredEvents
        );
    }
}