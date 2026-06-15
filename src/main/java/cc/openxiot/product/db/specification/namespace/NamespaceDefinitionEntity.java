package cc.openxiot.product.db.specification.namespace;

import cn.geekcity.xiot.spec.visibility.Visibility;

import java.util.Map;

public class NamespaceDefinitionEntity {

    public String organization;

    public Visibility visibility;

    public String code;

    public Map<String, String> description;

//    public Creator creator;
//
//    public Updater updater;
}