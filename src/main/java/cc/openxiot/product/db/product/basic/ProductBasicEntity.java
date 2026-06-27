package cc.openxiot.product.db.product.basic;

import cc.openxiot.product.db.person.Person;
import cc.openxiot.product.db.product.basic.name.LocalizedNameEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public class ProductBasicEntity {

    public String organization;

    public String model;

    public String template;

    public String icon;

    public LocalizedNameEntity name;

    public List<LocalizedNameEntity> alias;

    public List<String> upgrade;

    public String protocol;

    public String lifecycle;

    public Person creator;

    public Person updater;
}
