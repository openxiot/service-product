package cc.openxiot.product.db.organization.member;

import java.util.Date;
import java.util.Objects;

public class Member {

    public String developerId;
    public String name;
    public String role;
    public String email;
    public Date update;

    public Member() {
    }

    public Member(String developerId) {
        this(developerId, "member");
    }

    public Member(String developerId, String role) {
        this.developerId = developerId;
        this.role = role;
        this.update = new Date();
    }

    public Member(String developerId, String role, String name, String email) {
        this.developerId = developerId;
        this.role = role;
        this.name = name;
        this.email = email;
        this.update = new Date();
    }

    public String developerId() {
        return developerId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Member member)) return false;
        return Objects.equals(developerId, member.developerId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(developerId);
    }
}
