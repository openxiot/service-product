package cc.openxiot.product.db.person;

import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;

import java.util.Date;

public class PersonConvertor {

    public static Person of(Creator creator) {
        Person p = new Person();
        p.id = creator.id();
        p.name = creator.name();
        p.timestamp = new Date(creator.timestamp());
        return p;
    }

    public static Person of(Updater updater) {
        Person p = new Person();
        p.id = updater.id();
        p.name = updater.name();
        p.timestamp = new Date(updater.timestamp());
        return p;
    }

    public static Creator toCreator(Person person) {
        return new Creator()
                .id(person.id)
                .name(person.name)
                .timestamp(person.timestamp.getTime());
    }

    public static Updater toUpdater(Person person) {
        return new Updater()
                .id(person.id)
                .name(person.name)
                .timestamp(person.timestamp.getTime());
    }
}
