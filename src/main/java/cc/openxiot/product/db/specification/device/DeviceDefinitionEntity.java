package cc.openxiot.product.db.specification.device;

import cn.geekcity.xiot.spec.lifecycle.Lifecycle;

import java.util.Map;

public class DeviceDefinitionEntity {

    public Lifecycle lifecycle;

    public String code;

    public int value;

    public Map<String, String> description;
}
