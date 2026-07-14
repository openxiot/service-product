package cc.openxiot.common.person;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Date;

@Schema(description = "人")
@RegisterForReflection
public class Person {

    @Schema(description = "ID", required = true)
    public String id;

    @Schema(description = "名称", required = true)
    public String name;

    @Schema(description = "时间", required = true)
    public Date timestamp;

    public static Person of(String userId) {
        return of(userId, null);
    }

    public static Person of(String userId, String name) {
        Person person = new Person();
        person.id = userId;
        person.name = name;
        person.timestamp = new Date();
        return person;
    }
}
