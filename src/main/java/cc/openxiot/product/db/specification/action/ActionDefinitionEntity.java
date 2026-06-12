package cc.openxiot.product.db.specification.action;

import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;

import java.util.List;
import java.util.Map;

public class ActionDefinitionEntity {

    public String name;

    public int value;

    public Map<String, String> description;

    public List<String> in;

    public List<String> out;

    public Creator creator;

    public Updater updater;
}
