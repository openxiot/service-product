package cc.openxiot.product.resource;

import cc.openxiot.product.db.Person;
import cc.openxiot.product.db.account.coo.employee.Employee;
import cc.openxiot.product.db.account.developer.Developer;
import cc.openxiot.product.db.organization.Organization;
import cc.openxiot.product.db.organization.member.MemberRole;
import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.role.OxRole;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.JsonWebToken;

public class ResourceBase {

    // 检查浏览权限：管理人员、运营人员、开发组里的成员，有浏览权限
    protected void checkBrowserPermission(JsonWebToken jwt, String organizationId) throws OxException {
        switch (organizationId) {
            case "admin", "operator": {
                break;
            }

            default: {
                Organization.check(organizationId, jwt.getName(), MemberRole.MEMBER);
                break;
            }
        }
    }

    // 检查管理权限：管理人员、运营人员、开发组里的管理员，有浏览权限
    protected void checkManagerPermission(JsonWebToken jwt, String organizationId) throws OxException {
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

    protected Person getPerson(JsonWebToken jwt, String organizationId) throws OxException {
        switch (organizationId) {
            case "admin", "operator": {
                Employee employee = Employee.findById(new ObjectId(jwt.getName()));
                if (employee == null) {
                    throw new OxException("employee not found!");
                }

                return Person.create(employee.id.toString(), employee.username, organizationId);
            }

            default: {
                if (jwt.getGroups().contains(OxRole.DEVELOPER)) {
                    Developer developer = Developer.findById(new ObjectId(jwt.getName()));
                    if (developer == null) {
                        throw new OxException("developer not found!");
                    }

                    return Person.create(developer.id.toString(), developer.username, organizationId);
                } else {
                    Employee employee = Employee.findById(new ObjectId(jwt.getName()));
                    if (employee == null) {
                        throw new OxException("employee not found!");
                    }

                    return Person.create(employee.id.toString(), employee.username, organizationId);
                }
            }
        }
    }
}
