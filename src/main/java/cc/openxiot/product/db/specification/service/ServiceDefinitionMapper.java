package cc.openxiot.product.db.specification.service;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cn.geekcity.xiot.spec.definition.ServiceDefinition;
import cn.geekcity.xiot.spec.definition.urn.*;

import java.util.ArrayList;
import java.util.List;

public class ServiceDefinitionMapper {

    public static ServiceDefinitionEntity toEntity(ServiceDefinition definition) {
        if (definition == null) {
            return null;
        }

        ServiceDefinitionEntity entity = new ServiceDefinitionEntity();
        entity.code = definition.type().name();
        entity.value = definition.type().value();
        entity.description = definition.description();
        entity.optionalProperties = definition.optionalProperties().stream().map(Urn::name).toList();
        entity.requiredProperties = definition.requiredProperties().stream().map(Urn::name).toList();
        entity.optionalActions = definition.optionalActions().stream().map(Urn::name).toList();
        entity.requiredActions = definition.requiredActions().stream().map(Urn::name).toList();
        entity.optionalEvents = definition.optionalEvents().stream().map(Urn::name).toList();
        entity.requiredEvents = definition.requiredEvents().stream().map(Urn::name).toList();

        entity.lifecycle = definition.lifecycle().toString();

        return entity;
    }

    public static ServiceDefinition toDefinition(SpecificationEntity spec, ServiceDefinitionEntity entity) {
        if (entity == null) {
            return null;
        }

        ServiceType type = new ServiceType(spec.namespace.code, entity.code, entity.value);

        List<PropertyType> optionalProperties = entity.optionalProperties == null ? new ArrayList<>():
                entity.optionalProperties.stream().map(spec::getPropertyType).toList();

        List<PropertyType> requiredProperties = entity.requiredProperties == null ? new ArrayList<>():
                entity.requiredProperties.stream().map(spec::getPropertyType).toList();

        List<ActionType> optionalActions = entity.optionalActions == null ? new ArrayList<>():
                entity.optionalActions.stream().map(spec::getActionType).toList();

        List<ActionType> requiredActions = entity.requiredActions == null ? new ArrayList<>():
                entity.requiredActions.stream().map(spec::getActionType).toList();

        List<EventType> optionalEvents = entity.optionalEvents == null ? new ArrayList<>():
                entity.optionalEvents.stream().map(spec::getEventType).toList();

        List<EventType> requiredEvents = entity.requiredEvents == null ? new ArrayList<>():
                entity.requiredEvents.stream().map(spec::getEventType).toList();

        ServiceDefinition def =
                new ServiceDefinition(
                        type,
                        entity.description,
                        optionalProperties,
                        requiredProperties,
                        optionalActions,
                        requiredActions,
                        optionalEvents,
                        requiredEvents
                );

        def.lifecycle(entity.lifecycle);

        return def;
    }
}