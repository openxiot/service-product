package cc.openxiot.product.db.template;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Map;

@MongoEntity(database = "product", collection = "prepared/templates")
@BsonDiscriminator
@RegisterForReflection
@Schema(description = "模板")
public class TemplateEntity extends PanacheMongoEntityBase {

    @BsonId
    @Schema(description = "模板ID（DeviceType）", required = true)
    public String type;

    @Schema(description = "名字空间（DeviceType中的ns字段）", required = true)
    public String ns;

    @Schema(description = "设备类型（DeviceType中的name字段）", required = true)
    public String name;

    @Schema(description = "创建模板的组织（DeviceType中的vendor字段", required = true)
    public String organization;

    @Schema(description = "模板型号（DeviceType中的model字段", required = true)
    public String model;

    @Schema(description = "模板版本（DeviceType中的version字段", required = true)
    public int version;

    @Schema(description = "生命周期", required = true)
    public String lifecycle;

    public Map<String, String> description;

    @Schema(description = "模板内容", required = true)
    public String content;

    public TemplateEntity() {
    }
}
