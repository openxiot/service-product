package cc.openxiot.product.db.specification.device;

import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.definition.DeviceDefinition;
import cn.geekcity.xiot.spec.definition.urn.DeviceType;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;

public class DeviceDefinitionMapper {

    public static DeviceDefinitionEntity toEntity(DeviceDefinition definition, Creator creator) {
        if (definition == null) {
            return null;
        }

        DeviceDefinitionEntity entity = new DeviceDefinitionEntity();
        entity.category = definition.category();
        entity.code = definition.type().name();
        entity.value = definition.type().value();
        entity.description = definition.description();
        entity.lifecycle = Lifecycle.DEVELOPMENT;
//        entity.creator = creator;

        return entity;
    }

    public static DeviceDefinition toDefinition(String ns, DeviceDefinitionEntity entity) {
        if (entity == null) {
            return null;
        }

        DeviceType type = new DeviceType(ns, entity.code, entity.value);

        DeviceDefinition def = new DeviceDefinition(entity.category, type, entity.description);
        def.lifecycle(entity.lifecycle);
        return def;
    }
}