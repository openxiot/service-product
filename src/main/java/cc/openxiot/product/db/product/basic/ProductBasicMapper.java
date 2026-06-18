package cc.openxiot.product.db.product.basic;

import cc.openxiot.product.db.person.Person;
import cc.openxiot.product.db.person.PersonConvertor;
import cc.openxiot.product.db.product.ProductEntity;
import cn.geekcity.xiot.spec.definition.urn.DeviceType;
import cn.geekcity.xiot.spec.product.basic.ProductBasic;

public class ProductBasicMapper {

    public static ProductEntity toEntity(ProductBasic product) {
        if (product == null) {
            return null;
        }

        ProductEntity entity = new ProductEntity();
        entity.basic.organization = product.organization();
        entity.basic.model = product.model();
        entity.basic.template = product.template().toString();
        entity.basic.icon = product.icon();
        entity.basic.name = product.name();
        entity.basic.upgrade = product.upgrade();
        entity.basic.protocol = product.protocol();
        entity.basic.lifecycle = product.lifecycle().toString();
        entity.basic.naming = product.naming();
        entity.basic.creator = PersonConvertor.of(product.creator());
        entity.basic.updater = PersonConvertor.of(product.updater());

        return entity;
    }

    public static ProductBasic toProduct(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ProductBasic()
                .id(entity.id.toString())
                .organization(entity.basic.organization)
                .model(entity.basic.model)
                .template(new DeviceType(entity.basic.template))
                .icon(entity.basic.icon)
                .name(entity.basic.name)
                .upgrade(entity.basic.upgrade)
                .protocol(entity.basic.protocol)
                .lifecycle(entity.basic.lifecycle)
                .naming(entity.basic.naming)
                .creator(PersonConvertor.toCreator(entity.basic.creator))
                .updater(PersonConvertor.toUpdater(entity.basic.updater));
    }
}