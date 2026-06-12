package cc.openxiot.product.db.specification.namespace;

import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;

import java.util.Map;

public class NamespaceDefinitionEntity {

    public String code;

    public Map<String, String> description;

    public Creator creator;

    public Updater updater;
}
