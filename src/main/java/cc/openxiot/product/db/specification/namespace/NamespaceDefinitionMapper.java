package cc.openxiot.product.db.specification.namespace;

import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.definition.NamespaceDefinition;

public class NamespaceDefinitionMapper {

    public static NamespaceDefinitionEntity toEntity(NamespaceDefinition definition, Creator creator) {
        if (definition == null) {
            return null;
        }

        NamespaceDefinitionEntity entity = new NamespaceDefinitionEntity();
        entity.code = definition.namespace();
        entity.description = definition.description();
        entity.visibility = definition.visibility();
        entity.organization = definition.organization();
//        entity.creator = creator;

        return entity;
    }

    public static NamespaceDefinition toDefinition(NamespaceDefinitionEntity entity) {
        if (entity == null) {
            return null;
        }

        NamespaceDefinition def = new NamespaceDefinition(entity.code, entity.description);
        def.visibility(entity.visibility);
        def.organization(entity.organization);

        return def;
    }
}