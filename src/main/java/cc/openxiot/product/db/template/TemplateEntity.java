package cc.openxiot.product.db.template;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@MongoEntity(collection = "templates")
@BsonDiscriminator
public class TemplateEntity extends PanacheMongoEntity {

    public String namespace;

    public String organization;

    public String model;

    public int version;

    public String lifecycle;

    public String content;

    public TemplateEntity() {
    }
}
