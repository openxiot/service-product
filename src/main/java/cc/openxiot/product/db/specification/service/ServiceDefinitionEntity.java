package cc.openxiot.product.db.specification.service;

import java.util.List;
import java.util.Map;

public class ServiceDefinitionEntity {

    public String lifecycle;

    public String code;

    public int value;

    public Map<String, String> description;

    public List<String> optionalProperties;

    public List<String> requiredProperties;

    public List<String> optionalActions;

    public List<String> requiredActions;

    public List<String> optionalEvents;

    public List<String> requiredEvents;
}
