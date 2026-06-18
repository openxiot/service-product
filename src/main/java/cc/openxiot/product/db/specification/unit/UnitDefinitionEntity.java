package cc.openxiot.product.db.specification.unit;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Map;

@RegisterForReflection
public class UnitDefinitionEntity {

    public String lifecycle;

    public String code;

    public int value;

    public Map<String, String> description;
}
