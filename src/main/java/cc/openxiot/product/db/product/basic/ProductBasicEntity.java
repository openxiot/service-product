package cc.openxiot.product.db.product.basic;

import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;

import java.util.List;

public class ProductBasicEntity {

    public String organization;

    public String model;

    public String template;

    public String icon;

    public String name;

    public List<String> upgrade;

    public String protocol;

    public Lifecycle lifecycle;

    public List<String> naming;

    public Creator creator;

    public Updater updater;
}
