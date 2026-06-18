package cc.openxiot.product.serializer;

import cn.geekcity.xiot.spec.visibility.Visibility;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

import java.io.IOException;

public class VisibilityDeserializer extends StdScalarDeserializer<Visibility> {

    public VisibilityDeserializer() {
        super(Visibility.class);
    }

    @Override
    public Visibility deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        // 直接调用你现成的 of 方法
        return Visibility.of(value);
    }
}