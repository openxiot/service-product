package cc.openxiot.product.db.specification;

import cc.openxiot.product.db.specification.action.ActionDefinitionEntity;
import cc.openxiot.product.db.specification.device.DeviceDefinitionEntity;
import cc.openxiot.product.db.specification.event.EventDefinitionEntity;
import cc.openxiot.product.db.specification.format.FormatDefinitionEntity;
import cc.openxiot.product.db.specification.namespace.NamespaceDefinitionEntity;
import cc.openxiot.product.db.specification.property.PropertyDefinitionEntity;
import cc.openxiot.product.db.specification.service.ServiceDefinitionEntity;
import cc.openxiot.product.db.specification.unit.UnitDefinitionEntity;
import cn.geekcity.xiot.spec.definition.urn.ActionType;
import cn.geekcity.xiot.spec.definition.urn.EventType;
import cn.geekcity.xiot.spec.definition.urn.PropertyType;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.Map;

@MongoEntity(collection = "specifications")
@BsonDiscriminator
@RegisterForReflection
public class SpecificationEntity extends PanacheMongoEntity {

    public NamespaceDefinitionEntity namespace;

    public Map<String, DeviceDefinitionEntity> devices;

    public Map<String, ServiceDefinitionEntity> services;

    public Map<String, PropertyDefinitionEntity> properties;

    public Map<String, ActionDefinitionEntity> actions;

    public Map<String, EventDefinitionEntity> events;

    public Map<String, FormatDefinitionEntity> formats;

    public Map<String, UnitDefinitionEntity> units;

    public SpecificationEntity() {
    }

    public PropertyType getPropertyType(String name) {
        PropertyDefinitionEntity def = properties.get(name);
        return new PropertyType(namespace.code, name, (def == null) ? 0 : def.value);
    }

    public ActionType getActionType(String name) {
        ActionDefinitionEntity def = actions.get(name);
        return new ActionType(namespace.code, name, (def == null) ? 0 : def.value);
    }

    public EventType getEventType(String name) {
        EventDefinitionEntity def = events.get(name);
        return new EventType(namespace.code, name, (def == null) ? 0 : def.value);
    }
}
