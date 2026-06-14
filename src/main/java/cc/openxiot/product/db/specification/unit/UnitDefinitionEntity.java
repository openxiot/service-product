package cc.openxiot.product.db.specification.unit;

import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;

import java.util.Map;

public class UnitDefinitionEntity {

    public Lifecycle lifecycle;

    public String code;

    public int value;

    public Map<String, String> description;

//    public Creator creator;
//
//    public Updater updater;
}
