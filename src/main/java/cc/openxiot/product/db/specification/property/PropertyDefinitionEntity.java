package cc.openxiot.product.db.specification.property;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;
import java.util.Map;

@RegisterForReflection
public class PropertyDefinitionEntity {

    public String lifecycle;

    public String code;

    public int value;

    public Map<String, String> description;

    public String format;

    public List<String> access;

    public String unit;

    public ConstraintValueEntity constraintValue;

    public List<String> members;

    public static class ConstraintValueEntity {

        public String type;

        public String list;

        public String range;

        public String length;
    }
}
