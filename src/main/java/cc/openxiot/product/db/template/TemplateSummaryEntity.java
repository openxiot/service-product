package cc.openxiot.product.db.template;

import io.quarkus.mongodb.panache.common.ProjectionFor;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Map;

@ProjectionFor(TemplateEntity.class)  // 指明是哪个实体的投影
@RegisterForReflection                // 强制注册反射，更稳
 public class TemplateSummaryEntity {
    @BsonProperty("_id")
    public String type;
    public String ns;
    public String name;
    public String organization;
    public String model;
    public int version;
    public String lifecycle;
    public Map<String, String> description;
}
