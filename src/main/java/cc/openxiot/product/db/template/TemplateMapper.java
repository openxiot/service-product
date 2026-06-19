package cc.openxiot.product.db.template;

import cn.geekcity.xiot.spec.codec.vertx.template.DeviceTemplateCodec;
import cn.geekcity.xiot.spec.definition.urn.DeviceType;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;
import cn.geekcity.xiot.spec.template.DeviceTemplate;
import cn.geekcity.xiot.spec.template.TemplateSummary;
import io.vertx.core.json.JsonObject;

public class TemplateMapper {

    public static TemplateEntity toEntity(DeviceTemplate definition) {
        if (definition == null) {
            return null;
        }

        TemplateEntity entity = new TemplateEntity();
        entity.namespace = definition.type().ns();
        entity.organization = definition.type().organization();
        entity.device = definition.type().ns();
        entity.model = definition.type().model();
        entity.version = definition.type().version();
        entity.content = DeviceTemplateCodec.encode(definition).toString();
        entity.lifecycle = Lifecycle.DEVELOPMENT.toString();

        return entity;
    }

    public static DeviceTemplate toDefinition(TemplateEntity entity) {
        if (entity == null) {
            return null;
        }

        JsonObject o = new JsonObject(entity.content);
        return DeviceTemplateCodec.decode(o);
    }

    public static TemplateSummary toSummary(TemplateSummaryEntity entity) {
        if (entity == null) {
            return null;
        }

        TemplateSummary summary = new TemplateSummary();
        summary.type(DeviceType.parse(entity.type));
        summary.lifecycle(entity.lifecycle);
        summary.description(entity.description);

        return summary;
    }
}