package cc.openxiot.product.db.history;

import cc.openxiot.product.db.account.coo.employee.Employee;
import cc.openxiot.product.db.account.developer.Developer;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.json.JsonObject;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

@MongoEntity(collection = "account-histories")
public class History extends PanacheMongoEntity {

    public static final String ORGANIZATION_ID_ADVERTISER = "advertiser";
    public static final String ORGANIZATION_ID_OPERATOR = "operator";
    public static final String ORGANIZATION_ID_ADMIN = "admin";

    @BsonProperty("organizationId")
    public String organizationId;

    @BsonProperty("accountId")
    public String accountId;

    @BsonProperty("accountName")
    public String accountName;

    @BsonProperty("accountEmail")
    public String accountEmail;

    // add/remove/update
    @BsonProperty("operation")
    public String operation;

    // app
    // unit
    @BsonProperty("target")
    public String target;

    // 主细节（应用、组等）
    @BsonProperty("detail")
    public String detail;

    // 次细节（应用的广告位、组的成员）
    @BsonProperty("detail2")
    public String detail2;

    @BsonProperty("date")
    public Date date;

    public static History findByAccountId(String accountId) {
        return find("accountId", accountId).firstResult();
    }

    public static List<History> findByDateRange(Date start, Date end) {
        Document query = new Document()
                .append("date", new Document("$gte", start).append("$lte", end))
                ;

        return History.list(query);
    }

    public static List<History> findByAccountIdAndDateRange(String accountId, Date start, Date end) {
        Document query = new Document()
                .append("accountId", accountId)
                .append("date", new Document("$gte", start).append("$lte", end));

        return History.list(query);
    }

    public static History findByOrganizationId(String organizationId) {
        return find("organizationId", organizationId).firstResult();
    }

    public static List<History> findByOrganizationIdAndDateRange(String organizationId, Date start, Date end) {
        Document query = new Document()
                .append("organizationId", organizationId)
                .append("date", new Document("$gte", start).append("$lte", end))
                ;

        return History.list(query);
    }

    // ---------------------------------------------------------------------------
    // 投放者日志
    // ---------------------------------------------------------------------------

    public static void addAdvertiser(String accountId, String operation, String target, String detail) {
        addAdvertiser(accountId, operation, target, detail, "{}");
    }

    public static void addAdvertiser(String accountId, String operation, String target, String detail, String detail2) {
        Employee employee = Employee.findById(new ObjectId(accountId));
        if (employee != null) {
            History history = new History();
            history.organizationId = ORGANIZATION_ID_ADVERTISER;
            history.accountId = accountId;
            history.accountName = employee.username;
            history.operation = operation;
            history.target = target;
            history.detail = detail;
            history.detail2 = detail2;
            history.date = new Date();
            history.persist();
        }
    }

    // ---------------------------------------------------------------------------
    // 运营者日志
    // ---------------------------------------------------------------------------
    public static void addOperator(String accountId, String operation, String target, JsonObject detail) {
        addOperator(accountId, operation, target, detail.toString());
    }

    public static void addOperator(String accountId, String operation, String target, String detail) {
        Employee employee = Employee.findById(new ObjectId(accountId));
        if (employee != null) {
            History history = new History();
            history.organizationId = ORGANIZATION_ID_OPERATOR;
            history.accountId = accountId;
            history.accountName = employee.username;
            history.operation = operation;
            history.target = target;
            history.detail = detail;
            history.date = new Date();
            history.persist();
        }
    }

    // ---------------------------------------------------------------------------
    // 管理者日志
    // ---------------------------------------------------------------------------
    public static void addAdmin(String accountId, String operation, String target, JsonObject detail) {
        addAdmin(accountId, operation, target, detail.toString());
    }

    public static void addAdmin(String accountId, String operation, String target, String detail) {
        Employee employee = Employee.findById(new ObjectId(accountId));
        if (employee != null) {
            History history = new History();
            history.organizationId = ORGANIZATION_ID_ADMIN;
            history.accountId = accountId;
            history.accountName = employee.username;
            history.operation = operation;
            history.target = target;
            history.detail = detail;
            history.date = new Date();
            history.persist();
        }
    }

    // ---------------------------------------------------------------------------
    // 开发者日志
    // ---------------------------------------------------------------------------
    public static void addDeveloper(String organizationId, String developerId, String operation, String target, JsonObject detail) {
        addDeveloper(organizationId, developerId, operation, target, detail.toString());
    }

    public static void addDeveloper(String organizationId, String developerId, String operation, String target, String detail) {
        addDeveloper(organizationId, developerId, operation, target, detail, "{}");
    }

    public static void addDeveloper(String organizationId, String developerId, String operation, String target, String detail, String detail2) {
        Developer developer = Developer.findById(new ObjectId(developerId));
        if (developer != null) {
            History history = new History();
            history.organizationId = organizationId;
            history.accountId = developerId;
            history.accountName = developer.username;
            history.accountEmail = developer.email;
            history.operation = operation;
            history.target = target;
            history.detail = detail;
            history.detail2 = detail2;
            history.date = new Date();
            history.persist();
        }
    }
}
