package cc.openxiot.product.db.specification.event;

import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;

import java.util.List;
import java.util.Map;

public class EventDefinitionEntity {

    public String code;

    public int value;

    public Map<String, String> description;

    public List<String> arguments;

//    public Creator creator;
//
//    public Updater updater;
}
