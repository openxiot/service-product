package cc.openxiot.account.db.developer.organization;

import cc.openxiot.account.db.developer.organization.member.Member;
import cc.openxiot.account.db.developer.organization.member.MemberRole;
import cc.openxiot.common.exception.OxException;
import cc.openxiot.common.person.Person;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@MongoEntity(database = "account", collection = "developer-organizations")
@RegisterForReflection
public class DeveloperOrganization extends PanacheMongoEntityBase {

    @BsonId
    public String code;

    @BsonProperty("name")
    public String name;

    @BsonProperty("creator")
    public Person creator;

    @BsonProperty("members")
    public List<Member> members;

//    @BsonProperty("personal")
//    public boolean personal;

    public static List<DeveloperOrganization> findByPersonal(String accountId) {
        Document query = new Document()
                .append("creator.id", accountId)
                .append("personal", true);

        return DeveloperOrganization.list(query);
    }

    public static List<DeveloperOrganization> findByCreator(String accountId) {
        Document query = new Document()
                .append("creator.id", accountId);

        return DeveloperOrganization.list(query);
    }

    public static List<DeveloperOrganization> findByMember(String accountId) {
        // 服务端查询：通过 members 数组中的 developerId 字段过滤，避免全表扫描 + 内存遍历
        return DeveloperOrganization.find("members.developerId", accountId).list();
    }

    public static void check(String organizationId, String accountId, String role) throws OxException {
        DeveloperOrganization organization = DeveloperOrganization.findById(organizationId);
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
