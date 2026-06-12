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

import java.util.List;

@MongoEntity(collection = "namespace")
@BsonDiscriminator
public class SpecificationEntity extends PanacheMongoEntity {

    public NamespaceDefinitionEntity namespace;

    public List<DeviceDefinitionEntity> devices;

    public List<ServiceDefinitionEntity> services;

    public List<PropertyDefinitionEntity> properties;

    public List<ActionDefinitionEntity> actions;

    public List<EventDefinitionEntity> events;

    public List<FormatDefinitionEntity> formats;

    public List<UnitDefinitionEntity> units;

    public SpecificationEntity() {
    }
}
