package cc.openxiot.account.db.organization;

import cc.openxiot.account.db.organization.member.Member;
import cc.openxiot.account.db.organization.member.MemberRole;
import cc.openxiot.common.exception.OxException;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@MongoEntity(database = "account", collection = "account-organizations")
@RegisterForReflection
public class Organization extends PanacheMongoEntityBase {

    @BsonId
    public String code;

    @BsonProperty("name")
    public String name;

    @BsonProperty("creator")
    public String creator;

    @BsonProperty("creatorName")
    public String creatorName;

    @BsonProperty("createAt")
    public Date createAt;

    @BsonProperty("members")
    public List<Member> members;

//    @BsonProperty("personal")
//    public boolean personal;

    public static List<Organization> findByPersonal(String accountId) {
        Document query = new Document()
                .append("creator", accountId)
                .append("personal", true);

        return Organization.list(query);
    }

    public static List<Organization> findByCreator(String accountId) {
        Document query = new Document()
                .append("creator", accountId);

        return Organization.list(query);
    }

    public static List<Organization> findByMember(String accountId) {
        List<Organization> organizations = new ArrayList<>();

        List<Organization> list = findAll().list();
        for (Organization organization : list) {
            if (organization.members != null) {
                for (Member member : organization.members) {
                    if (accountId.equals(member.developerId)) {
                        organizations.add(organization);
                        break;
                    }
                }
            }
        }

        return organizations;
    }

    public static void check(String organizationId, String accountId, String role) throws OxException {
        Organization organization = Organization.findById(organizationId);
        if (organization == null) {
            throw new OxException("organization not found");
        }

        Map<String, Member> members = organization.members.stream().collect(Collectors.toMap(Member::developerId, Function.identity()));
        Member member = members.get(accountId);
        if (member == null) {
            throw new OxException("accountId not found in organization");
        }

        // 普通成员，但是要求管理员
        if (MemberRole.MEMBER.equals(member.role) && MemberRole.ADMIN.equals(role)) {
            throw new OxException("accountId not admin");
        }
    }
}
