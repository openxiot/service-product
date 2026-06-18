package cc.openxiot.account.db.account.coo.employee;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;

@MongoEntity(collection = "account-employees")
@RegisterForReflection
public class Employee extends PanacheMongoEntity {

    @BsonProperty("avatar")
    public String avatar;

    @BsonProperty("email")
    public String email;

    @BsonProperty("username")
    public String username;

    @BsonProperty("platformId")
    public String platformId;

    @BsonProperty("platformAccountId")
    public String platformAccountId;

    public static List<Employee> findByPlatform(String platformId) {
        Document query = new Document().append("platformId", platformId);
        return Employee.list(query);
    }

    public static Employee findByPlatform(String platformId, String platformAccountId) {
        Document query = new Document()
                .append("platformId", platformId)
                .append("platformAccountId", platformAccountId);
        return Employee.find(query).firstResult();
    }
}
