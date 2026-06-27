package cc.openxiot.product.db.product.basic;

import cc.openxiot.product.db.person.Person;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;
import java.util.Map;

@RegisterForReflection
public class ProductBasicEntity {

    public String organization;

    public String model;

    public String template;

    public String icon;

    public Map<String, String> name;

    public List<Map<String, String>> alias;

    public List<String> upgrade;

    public String protocol;

    public String lifecycle;

    public Person creator;

    public Person updater;
}
