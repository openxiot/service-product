package cc.openxiot.account.db.account.developer;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.List;

@MongoEntity(database = "account", collection = "account-developers")
@RegisterForReflection
public class Developer extends PanacheMongoEntityBase {

    @BsonId
    public ObjectId id;

    @BsonProperty("avatar")
    public String avatar;

    @BsonProperty("email")
    public String email;

    @BsonProperty("username")
    public String username;

    @BsonProperty("password")
    public String password;

    @BsonProperty("platformId")
    public String platformId;

    @BsonProperty("platformAccountId")
    public String platformAccountId;

    public static List<Developer> findByPlatform(String platformId) {
        Document query = new Document().append("platformId", platformId);
        return Developer.list(query);
    }

    public static Developer findByPlatform(String platformId, String platformAccountId) {
        Document query = new Document()
                .append("platformId", platformId)
                .append("platformAccountId", platformAccountId);
        return Developer.find(query).firstResult();
    }
}
