package cc.openxiot.product.db.product.instance;

import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;

public class ProductInstanceEntity {

    public int version;

    public String type;

    public String content;

    public Lifecycle lifecycle;

    public Creator creator;

    public Updater updater;
}
