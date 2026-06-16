package cc.openxiot.product.db.specification.format;

import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.definition.FormatDefinition;
import cn.geekcity.xiot.spec.definition.urn.FormatType;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;

public class FormatDefinitionMapper {

    public static FormatDefinitionEntity toEntity(FormatDefinition definition) {
        if (definition == null) {
            return null;
        }

        FormatDefinitionEntity entity = new FormatDefinitionEntity();
        entity.code = definition.type().name();
        entity.value = definition.type().value();
        entity.description = definition.description();
        entity.lifecycle = definition.lifecycle();

        return entity;
    }

    public static FormatDefinition toDefinition(String ns, FormatDefinitionEntity entity) {
        if (entity == null) {
            return null;
        }

        FormatType type = new FormatType(ns, entity.code, entity.value);

        FormatDefinition def = new FormatDefinition(type, entity.description);
        def.lifecycle(entity.lifecycle);
        return def;
    }
}