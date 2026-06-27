package cc.openxiot.product.db.product.basic.name;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Map;

@RegisterForReflection
public class LocalizedNameEntity {

    public Map<String, String> value;
}
