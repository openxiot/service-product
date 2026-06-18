package cc.openxiot.product.response;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class OxOk {

    public boolean success;

    public OxOk() {
        this.success = true;
    }
}
