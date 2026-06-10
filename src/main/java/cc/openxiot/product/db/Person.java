package cc.openxiot.product.db;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Date;

@Schema(description = "人")
public class Person {

    @Schema(description = "ID", required = true)
    public String id;

    @Schema(description = "名称", required = true)
    public String name;

    @Schema(description = "时间", required = true)
    public Date timestamp;

    @Schema(description = "创建时所在的组", required = true)
    public String organizationId;

    public static Person create(String id, String name, String organizationId) {
        Person p = new Person();
        p.id = id;
        p.name = name;
        p.organizationId = organizationId;
        p.timestamp = new Date();
        return p;
    }
}
