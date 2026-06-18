package cc.openxiot.product.db.person;

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
}
