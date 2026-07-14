package cc.openxiot.common.resource;

import cc.openxiot.account.db.account.developer.Developer;
import cc.openxiot.account.db.organization.Organization;
import cc.openxiot.account.db.organization.member.MemberRole;
import cc.openxiot.common.exception.OxException;
import cc.openxiot.common.role.OxRole;
import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.JsonWebToken;

public abstract class AbstractResource {

    @Inject
    SecurityIdentity securityIdentity;

    protected String getAccountId() {
        return securityIdentity.getPrincipal().getName();
    }

    protected String getUserName() {
        if (securityIdentity.getPrincipal() instanceof JsonWebToken jwt) {
            return jwt.getClaim("username");
        }
        return null;
    }

    protected boolean hasRole(String role) {
        return securityIdentity.hasRole(role);
    }

    protected void checkManagerPermission(String organizationId) throws OxException {
        switch (organizationId) {
            case "admin", "operator":
                break;
            default:
                if (hasRole(OxRole.DEVELOPER)) {
                    Organization.check(organizationId, getAccountId(), MemberRole.ADMIN);
                }
                break;
        }
    }

    protected Creator getCreator(String organizationId) throws OxException {
        Developer developer = findDeveloper(organizationId, MemberRole.MEMBER);
        return new Creator()
                .id(developer.id.toString())
                .name(developer.username)
                .timestamp(System.currentTimeMillis());
    }

    protected Updater getUpdater(String organizationId) throws OxException {
        Developer developer = findDeveloper(organizationId, MemberRole.MEMBER);
        return new Updater()
                .id(developer.id.toString())
                .name(developer.username)
                .timestamp(System.currentTimeMillis());
    }

    private Developer findDeveloper(String organizationId, String minRole) throws OxException {
        if (!hasRole(OxRole.DEVELOPER)) {
            throw new OxException("account not developer");
        }

        Organization.check(organizationId, getAccountId(), minRole);

        Developer developer = Developer.findById(new ObjectId(getAccountId()));
        if (developer == null) {
            throw new OxException("developer not found!");
        }

        return developer;
    }
}
