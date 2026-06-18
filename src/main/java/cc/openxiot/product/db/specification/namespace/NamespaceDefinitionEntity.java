package cc.openxiot.product.db.specification.namespace;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Map;

@RegisterForReflection
public class NamespaceDefinitionEntity {

    public String organization;

    public String visibility;

    public String code;

    public Map<String, String> description;
}