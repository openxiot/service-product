package cc.openxiot.product.db.specification.event;

import java.util.List;
import java.util.Map;

public class EventDefinitionEntity {

    public String lifecycle;

    public String code;

    public int value;

    public Map<String, String> description;

    public List<String> arguments;
}
