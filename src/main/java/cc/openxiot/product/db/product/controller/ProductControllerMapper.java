package cc.openxiot.product.db.product.controller;

import cc.openxiot.common.person.PersonConvertor;
import cn.geekcity.xiot.spec.definition.urn.Urn;
import cn.geekcity.xiot.spec.definition.urn.UrnType;
import cn.geekcity.xiot.spec.product.controller.ProductController;
import cn.geekcity.xiot.spec.version.GenericVersion;

import java.util.Arrays;
import java.util.List;

public class ProductControllerMapper {

    // 与 ProductController.instance(String) 保持一致
    private static final List<UrnType> INSTANCE_TYPES = Arrays.asList(UrnType.DEVICE, UrnType.GROUP);

    public static ProductControllerEntity toEntity(ProductController controller) {
        if (controller == null) {
            return null;
        }

        ProductControllerEntity entity = new ProductControllerEntity();
        entity.instance = controller.instance().toString();
        entity.category = controller.category();
        entity.type = controller.type();
        entity.versionName = controller.version().name();
        entity.versionCode = controller.version().code();
        entity.lifecycle = controller.lifecycle().toString();
        entity.creator = PersonConvertor.of(controller.creator());
        entity.updater = PersonConvertor.of(controller.updater());

        ProductController.Web web = controller.web();
        if (web != null) {
            entity.format = web.format();
            entity.url = web.url();
        }

        return entity;
    }

    public static ProductController toController(ProductControllerEntity entity) {
        if (entity == null) {
            return null;
        }

        ProductController controller = new ProductController()
                .instance(of(entity.instance))
                .category(entity.category)
                .type(entity.type)
                .version(new GenericVersion().name(entity.versionName).code(entity.versionCode))
                .lifecycle(entity.lifecycle);

        if (entity.format != null || entity.url != null) {
            controller.web(new ProductController.Web()
                    .format(entity.format)
                    .url(entity.url));
        }

        if (entity.creator != null) {
            controller.creator(PersonConvertor.toCreator(entity.creator));
        }

        controller.updater(PersonConvertor.toUpdater(entity.updater));

        return controller;
    }

    // 非抛异常的 Urn 构造，解析失败时返回 invalid urn，由调用方判断
    public static Urn of(String instance) {
        return new Urn(INSTANCE_TYPES, instance);
    }
}
