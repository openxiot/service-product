package cc.openxiot.product.db.template;

import cn.geekcity.xiot.spec.codec.vertx.template.DeviceTemplateCodec;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;
import cn.geekcity.xiot.spec.template.DeviceTemplate;
import io.vertx.core.json.JsonObject;

public class TemplateMapper {

    public static TemplateEntity toEntity(DeviceTemplate definition) {
        if (definition == null) {
            return null;
        }

        TemplateEntity entity = new TemplateEntity();
        entity.namespace = definition.type().ns();
        entity.organization = definition.type().organization();
        entity.model = definition.type().model();
        entity.version = definition.type().version();
        entity.content = DeviceTemplateCodec.encode(definition).toString();
        entity.lifecycle = Lifecycle.DEVELOPMENT;

        return entity;
    }

    public static DeviceTemplate toDefinition(TemplateEntity entity) {
        if (entity == null) {
            return null;
        }

        JsonObject o = new JsonObject(entity.content);
        return DeviceTemplateCodec.decode(o);
    }
}