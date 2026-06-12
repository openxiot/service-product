package cc.openxiot.product.db.specification.unit;

import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.definition.UnitDefinition;
import cn.geekcity.xiot.spec.definition.urn.UnitType;

public class UnitDefinitionMapper {

    public static UnitDefinitionEntity toEntity(UnitDefinition definition, Creator creator) {
        if (definition == null) {
            return null;
        }

        UnitDefinitionEntity entity = new UnitDefinitionEntity();
        entity.code = definition.type().name();
        entity.value = definition.type().value();
        entity.description = definition.description();
        entity.creator = creator;

        return entity;
    }

    public static UnitDefinition toDefinition(String ns, UnitDefinitionEntity entity) {
        if (entity == null) {
            return null;
        }

        UnitType type = new UnitType(ns, entity.code, entity.value);

        return new UnitDefinition(type, entity.description);
    }
}