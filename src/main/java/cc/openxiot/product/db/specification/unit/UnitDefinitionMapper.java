package cc.openxiot.product.db.specification.unit;

import cn.geekcity.xiot.spec.definition.UnitDefinition;
import cn.geekcity.xiot.spec.definition.urn.UnitType;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;

public class UnitDefinitionMapper {

    public static UnitDefinitionEntity toEntity(UnitDefinition definition) {
        if (definition == null) {
            return null;
        }

        UnitDefinitionEntity entity = new UnitDefinitionEntity();
        entity.code = definition.type().name();
        entity.value = definition.type().value();
        entity.description = definition.description();
        entity.lifecycle = definition.lifecycle().toString();

        return entity;
    }

    public static UnitDefinition toDefinition(String ns, UnitDefinitionEntity entity) {
        if (entity == null) {
            return null;
        }

        UnitType type = new UnitType(ns, entity.code, entity.value);

        UnitDefinition def = new UnitDefinition(type, entity.description);
        def.lifecycle(Lifecycle.valueOf(entity.lifecycle));
        return def;
    }
}