package cc.openxiot.product.db.specification;

import cc.openxiot.product.db.specification.action.ActionDefinitionEntity;
import cc.openxiot.product.db.specification.device.DeviceDefinitionEntity;
import cc.openxiot.product.db.specification.event.EventDefinitionEntity;
import cc.openxiot.product.db.specification.format.FormatDefinitionEntity;
import cc.openxiot.product.db.specification.namespace.NamespaceDefinitionEntity;
import cc.openxiot.product.db.specification.property.PropertyDefinitionEntity;
import cc.openxiot.product.db.specification.service.ServiceDefinitionEntity;
import cc.openxiot.product.db.specification.unit.UnitDefinitionEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.Map;

@MongoEntity(collection = "namespace")
@BsonDiscriminator
public class SpecificationEntity extends PanacheMongoEntity {

    public String organization;

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

}
