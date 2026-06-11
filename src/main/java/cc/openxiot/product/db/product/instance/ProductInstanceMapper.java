package cc.openxiot.product.db.product.instance;

import cn.geekcity.xiot.spec.definition.urn.DeviceType;
import cn.geekcity.xiot.spec.product.instance.ProductInstance;

public class ProductInstanceMapper {

    public static ProductInstance toInstance(ProductInstanceEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ProductInstance()
                .type(DeviceType.parse(entity.type))
                .lifecycle(entity.lifecycle)
                .creator(entity.creator)
                .updater(entity.updater);
    }
}