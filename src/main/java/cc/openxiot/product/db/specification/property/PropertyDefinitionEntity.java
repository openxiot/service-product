package cc.openxiot.product.db.specification.property;

import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;
import cn.geekcity.xiot.spec.definition.property.data.DataFormat;

import java.util.List;
import java.util.Map;

public class PropertyDefinitionEntity {

    public String name;

    public int value;

    public Map<String, String> description;

    public DataFormat format;

    public List<String> access;

    public String unit;

    public ConstraintValueEntity constraintValue;

    public List<String> members;

    public Creator creator;

    public Updater updater;

    public static class ConstraintValueEntity {

        public String type;

        public String list;

        public String range;

        public String length;
    }
}
