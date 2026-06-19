package cc.openxiot.product.db.template;

import io.quarkus.mongodb.panache.common.ProjectionFor;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Map;

@ProjectionFor(TemplateEntity.class)  // 指明是哪个实体的投影
@RegisterForReflection                // 强制注册反射，更稳
 public class TemplateSummaryEntity {
    public String type;
    public String namespace;
    public String device;
    public String organization;
    public String model;
    public int version;
    public String lifecycle;
    public Map<String, String> description;
}
