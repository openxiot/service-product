package cc.openxiot.product.db.product.basic.name;

import cn.geekcity.xiot.spec.name.LocalizedName;

import java.util.List;

public class LocalizedNameMapper {

    public static List<LocalizedNameEntity> toEntities(List<LocalizedName> names) {
        return names.stream().map(LocalizedNameMapper::toEntity).toList();
    }

    public static LocalizedNameEntity toEntity(LocalizedName name) {
        if (name == null) {
            return null;
        }

        LocalizedNameEntity entity = new LocalizedNameEntity();
        entity.value = name.value();

        return entity;
    }

    public static LocalizedName toName(LocalizedNameEntity entity) {
        if (entity == null) {
            return null;
        }

        return new LocalizedName(entity.value);
    }

    public static List<LocalizedName> toNames(List<LocalizedNameEntity> entities) {
        return entities.stream().map(LocalizedNameMapper::toName).toList();
    }
}
