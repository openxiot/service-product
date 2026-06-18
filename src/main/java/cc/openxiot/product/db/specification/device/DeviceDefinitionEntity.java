package cc.openxiot.product.db.specification.device;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Map;

@RegisterForReflection
public class DeviceDefinitionEntity {

    public String lifecycle;

    public String code;

    public int value;

    public Map<String, String> description;
}
