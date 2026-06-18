package cc.openxiot.product.db.specification.property;

import cn.geekcity.xiot.spec.codec.vertx.definition.ValueLengthCodec;
import cn.geekcity.xiot.spec.codec.vertx.definition.ValueListCodec;
import cn.geekcity.xiot.spec.codec.vertx.definition.ValueRangeCodec;
import cn.geekcity.xiot.spec.definition.PropertyDefinition;
import cn.geekcity.xiot.spec.definition.property.Access;
import cn.geekcity.xiot.spec.definition.property.ValueLength;
import cn.geekcity.xiot.spec.definition.property.ValueList;
import cn.geekcity.xiot.spec.definition.property.ValueRange;
import cn.geekcity.xiot.spec.definition.property.data.DataFormat;
import cn.geekcity.xiot.spec.definition.urn.PropertyType;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;
import io.vertx.core.json.JsonArray;

import java.util.List;

public class PropertyDefinitionMapper {

    public static PropertyDefinitionEntity toEntity(PropertyDefinition<?> def) {
        if (def == null) {
            return null;
        }

        PropertyDefinitionEntity entity = new PropertyDefinitionEntity();
        entity.code = def.type().name();
        entity.value = def.type().value();
        entity.description = def.description();
        entity.format = def.format().toString();
        entity.access = def.access().toList();
        entity.unit = def.unit();
        entity.constraintValue = new PropertyDefinitionEntity.ConstraintValueEntity();

        if (def.constraintValue() != null) {
            if (def.constraintValue() instanceof ValueList) {
                entity.constraintValue.type = "list";
                entity.constraintValue.list = ValueListCodec.encode((ValueList<?>) def.constraintValue()).encode();
            } else if (def.constraintValue() instanceof ValueRange) {
                entity.constraintValue.type = "range";
                entity.constraintValue.range = ValueRangeCodec.encode((ValueRange<?>) def.constraintValue()).encode();
            } else if (def.constraintValue() instanceof ValueLength) {
                entity.constraintValue.type = "length";
                entity.constraintValue.length = ValueLengthCodec.encode((ValueLength<?>) def.constraintValue()).encode();
            }
        } else {
            entity.constraintValue.type = "none";
        }

        entity.members = def.members().stream().map(x -> x.type().name()).toList();

        entity.lifecycle = def.lifecycle().toString();

        return entity;
    }

    public static PropertyDefinition<?> toDefinition(String ns, PropertyDefinitionEntity entity) {
        if (entity == null) {
            return null;
        }

        PropertyType type = new PropertyType(ns, entity.code, entity.value);
        Access access = Access.valueOf(entity.access);

        PropertyDefinition<?> def = new PropertyDefinition<>(type, entity.description, access, DataFormat.from(entity.format), null, entity.unit);

        if (entity.members != null) {
            List<PropertyType> members = entity.members.stream()
                    .map(x -> new PropertyType(ns, entity.code, entity.value))
                    .toList();

            def.members(members);
        }

        if (entity.constraintValue != null) {
            if (entity.constraintValue.type != null) {
                switch (entity.constraintValue.type) {
                    case "list":
                        JsonArray list = new JsonArray(entity.constraintValue.list);
                        def.constraintValue(ValueListCodec.decode(def.format(), list));
                        break;

                    case "range":
                        JsonArray range = new JsonArray(entity.constraintValue.range);
                        def.constraintValue(ValueRangeCodec.decode(def.format(), range));
                        break;

                    case "length":
                        JsonArray length = new JsonArray(entity.constraintValue.length);
                        def.constraintValue(ValueLengthCodec.decode(length));
                        break;
                }
            }
        }

        def.lifecycle(Lifecycle.valueOf(entity.lifecycle));

        return def;
    }
}