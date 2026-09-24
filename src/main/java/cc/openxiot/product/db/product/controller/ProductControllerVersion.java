package cc.openxiot.product.db.product.controller;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * 控制页自身版本，对应 xiot-core 的 GenericVersion。
 * name 是版本名称（显示用），code 是版本代码（比较 / 唯一 key 用）。
 * 存成 ProductControllerEntity.version 这个内嵌对象。
 */
@RegisterForReflection
public class ProductControllerVersion {

    public String name;

    public int code;

    public ProductControllerVersion() {
    }

    public ProductControllerVersion(String name, int code) {
        this.name = name;
        this.code = code;
    }
}