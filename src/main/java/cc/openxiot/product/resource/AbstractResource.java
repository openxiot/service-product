package cc.openxiot.product.resource;

import cc.openxiot.product.db.account.developer.Developer;
import cc.openxiot.product.db.organization.Organization;
import cc.openxiot.product.db.organization.member.MemberRole;
import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.role.OxRole;
import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.JsonWebToken;

public abstract class AbstractResource {

    @Inject
    JsonWebToken jwt;

//    // 检查浏览权限：管理人员、运营人员、开发组里的成员，有浏览权限
//    protected void checkBrowserPermission(JsonWebToken jwt, String organizationId) throws OxException {
//        switch (organizationId) {
//            case "admin", "operator": {
//                break;
//            }
//
//            default: {
//                Organization.check(organizationId, jwt.getName(), MemberRole.MEMBER);
//                break;
//            }
//        }
//    }

    protected String getName() {
        return jwt.getName();
    }

    protected void checkManagerPermission(String organizationId) throws OxException {
        switch (organizationId) {
            case "admin", "operator": {
                break;
            }

            default: {
                if (jwt.getGroups().contains(OxRole.DEVELOPER)) {
                    Organization.check(organizationId, jwt.getName(), MemberRole.ADMIN);
                }
                break;
            }
        }
    }


    protected Creator getCreator(String organizationId) throws OxException {
        if (jwt.getGroups().contains(OxRole.DEVELOPER)) {
            Organization.check(organizationId, jwt.getName(), MemberRole.MEMBER);

            Developer developer = Developer.findById(new ObjectId(jwt.getName()));
            if (developer == null) {
                throw new OxException("developer not found!");
            }

            return new Creator()
                    .id(developer.id.toString())
                    .name(developer.username)
                    .timestamp(System.currentTimeMillis());
        }

        throw  new OxException("account not developer");
    }

    protected Updater getUpdater(String organizationId) throws OxException {
        if (jwt.getGroups().contains(OxRole.DEVELOPER)) {
            Organization.check(organizationId, jwt.getName(), MemberRole.MEMBER);

            Developer developer = Developer.findById(new ObjectId(jwt.getName()));
            if (developer == null) {
                throw new OxException("developer not found!");
            }

            return new Updater()
                    .id(developer.id.toString())
                    .name(developer.username)
                    .timestamp(System.currentTimeMillis());
        }

        throw  new OxException("account not developer");
    }

//    protected Person getPerson(JsonWebToken jwt, String organizationId) throws OxException {
//        switch (organizationId) {
//            case "admin", "operator": {
//                Employee employee = Employee.findById(new ObjectId(jwt.getName()));
//                if (employee == null) {
//                    throw new OxException("employee not found!");
//                }
//
//                return Person.create(employee.id.toString(), employee.username, organizationId);
//            }
//
//            default: {
//                if (jwt.getGroups().contains(OxRole.DEVELOPER)) {
//                    Developer developer = Developer.findById(new ObjectId(jwt.getName()));
//                    if (developer == null) {
//                        throw new OxException("developer not found!");
//                    }
//
//                    return Person.create(developer.id.toString(), developer.username, organizationId);
//                } else {
//                    Employee employee = Employee.findById(new ObjectId(jwt.getName()));
//                    if (employee == null) {
//                        throw new OxException("employee not found!");
//                    }
//
//                    return Person.create(employee.id.toString(), employee.username, organizationId);
//                }
//            }
//        }
//    }
}
