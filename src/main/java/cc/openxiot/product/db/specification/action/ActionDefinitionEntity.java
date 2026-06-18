package cc.openxiot.product.db.specification.action;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;
import java.util.Map;

@RegisterForReflection
public class ActionDefinitionEntity {

    public String lifecycle;

    public String code;

    public int value;

    public Map<String, String> description;

    public List<String> in;

    public List<String> out;
}
