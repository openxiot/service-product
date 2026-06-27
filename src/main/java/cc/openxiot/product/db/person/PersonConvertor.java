package cc.openxiot.product.db.person;

import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;

import java.util.Date;

public class PersonConvertor {

    public static Person of(Creator creator) {
        Person p = new Person();

        if (creator != null) {
            p.id = creator.id();
            p.name = creator.name();
            p.timestamp = new Date(creator.timestamp());
        }

        return p;
    }

    public static Person of(Updater updater) {
        Person p = new Person();

        if (updater != null) {
            p.id = updater.id();
            p.name = updater.name();
            p.timestamp = new Date(updater.timestamp());
        }

        return p;
    }

    public static Creator toCreator(Person person) {
        return new Creator()
                .id(person.id)
                .name(person.name)
                .timestamp(person.timestamp.getTime());
    }

    public static Updater toUpdater(Person person) {
        Updater updater = new Updater();

        if (person != null) {
            updater.id(person.id);
            updater.name(person.name);

            if (person.timestamp != null) {
                updater.timestamp(person.timestamp.getTime());
            }
        } else {
            updater.id("");
            updater.name("");
            updater.timestamp(new Date().getTime());
        }

        return updater;
    }
}
