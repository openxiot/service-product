package cc.openxiot.product.db.specification.format;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Map;

@RegisterForReflection
public class FormatDefinitionEntity {

    public String lifecycle;

    public String code;

    public int value;

    public Map<String, String> description;
}
