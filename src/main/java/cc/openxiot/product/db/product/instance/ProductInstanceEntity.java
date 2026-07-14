package cc.openxiot.product.db.product.instance;

import cc.openxiot.common.person.Person;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ProductInstanceEntity {

    public int version;

    public String type;

    public String content;

    public String lifecycle;

    public Person creator;

    public Person updater;
}
