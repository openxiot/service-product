package cc.openxiot.product.db.product.controller;

import cc.openxiot.common.person.Person;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;

/**
 * 产品控制页，内嵌在 ProductEntity.controllers 中。
 * 由 instance(urn) + category + version.code 唯一确定。
 */
@RegisterForReflection
public class ProductControllerEntity {

    // 支持的产品实例定义版本：urn:xxx-spec:device:fan:00000000:organization:model:version
    public String instance;

    public String category;

    // 控制页类型：目前只有 web
    public String type;

    // web.format：html 或 zip
    public String format;

    // web.url：上传后的地址
    public String url;

    // 控制页自身版本（name 显示、code 比较；等价 xiot-core 的 GenericVersion）
    public ProductControllerVersion version;

    public String lifecycle;

    public Person creator;

    public Person updater;

    public boolean matches(String instance, String category, int versionCode) {
        return version != null
                && version.code == versionCode
                && Objects.equals(this.instance, instance)
                && Objects.equals(this.category, category);
    }
}
