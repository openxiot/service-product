package cc.openxiot.product.db.specification.service;

import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;

import java.util.List;
import java.util.Map;

public class ServiceDefinitionEntity {

    public String name;

    public int value;

    public Map<String, String> description;

    public List<String> optionalProperties;

    public List<String> requiredProperties;

    public List<String> optionalActions;

    public List<String> requiredActions;

    public List<String> optionalEvents;

    public List<String> requiredEvents;

    public Creator creator;

    public Updater updater;
}
