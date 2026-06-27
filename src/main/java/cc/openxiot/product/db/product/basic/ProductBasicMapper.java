package cc.openxiot.product.db.product.basic;

import cc.openxiot.product.db.person.PersonConvertor;
import cc.openxiot.product.db.product.ProductEntity;
import cn.geekcity.xiot.spec.definition.urn.DeviceType;
import cn.geekcity.xiot.spec.name.LocalizedName;
import cn.geekcity.xiot.spec.product.basic.ProductBasic;

import java.util.List;

public class ProductBasicMapper {

    public static ProductEntity toEntity(ProductBasic product) {
        if (product == null) {
            return null;
        }

        ProductEntity entity = new ProductEntity();
        entity.basic = new ProductBasicEntity();
        entity.basic.organization = product.organization();
        entity.basic.model = product.model();
        entity.basic.template = product.template().toString();
        entity.basic.icon = product.icon();
        entity.basic.name = product.name().value();
        entity.basic.alias = product.alias().stream().map(LocalizedName::value).toList();
        entity.basic.upgrade = product.upgrade();
        entity.basic.protocol = product.protocol();
        entity.basic.lifecycle = product.lifecycle().toString();
        entity.basic.creator = PersonConvertor.of(product.creator());
        entity.basic.updater = PersonConvertor.of(product.updater());

        return entity;
    }

    public static ProductBasic toProduct(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        LocalizedName name = new LocalizedName(entity.basic.name);
        List<LocalizedName> alias = entity.basic.alias.stream().map(LocalizedName::new).toList();

        return new ProductBasic()
                .id(entity.id.toString())
                .organization(entity.basic.organization)
                .model(entity.basic.model)
                .template(new DeviceType(entity.basic.template))
                .icon(entity.basic.icon)
                .name(name)
                .alias(alias)
                .upgrade(entity.basic.upgrade)
                .protocol(entity.basic.protocol)
                .lifecycle(entity.basic.lifecycle)
                .creator(PersonConvertor.toCreator(entity.basic.creator))
                .updater(PersonConvertor.toUpdater(entity.basic.updater));
    }
}