package cc.openxiot.product.db.person;

import static org.junit.jupiter.api.Assertions.*;

import cc.openxiot.common.person.Person;
import cc.openxiot.common.person.PersonConvertor;
import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;
import java.util.Date;
import org.junit.jupiter.api.Test;

class PersonConvertorTest {

    @Test
    void shouldConvertCreatorToPerson() {
        Creator creator = new Creator().id("user-1").name("Alice").timestamp(1000L);
        Person person = PersonConvertor.of(creator);

        assertEquals("user-1", person.id);
        assertEquals("Alice", person.name);
        assertEquals(new Date(1000L), person.timestamp);
    }

    @Test
    void shouldConvertNullCreatorToEmptyPerson() {
        Person person = PersonConvertor.of((Creator) null);

        assertNull(person.id);
        assertNull(person.name);
        assertNull(person.timestamp);
    }

    @Test
    void shouldConvertUpdaterToPerson() {
        Updater updater = new Updater().id("user-2").name("Bob").timestamp(2000L);
        Person person = PersonConvertor.of(updater);

        assertEquals("user-2", person.id);
        assertEquals("Bob", person.name);
        assertEquals(new Date(2000L), person.timestamp);
    }

    @Test
    void shouldConvertNullUpdaterToEmptyPerson() {
        Person person = PersonConvertor.of((Updater) null);

        assertNull(person.id);
        assertNull(person.name);
        assertNull(person.timestamp);
    }

    @Test
    void shouldConvertPersonToCreator() {
        Person person = new Person();
        person.id = "user-3";
        person.name = "Charlie";
        person.timestamp = new Date(3000L);

        Creator creator = PersonConvertor.toCreator(person);
        assertEquals("user-3", creator.id());
        assertEquals("Charlie", creator.name());
        assertEquals(3000L, creator.timestamp());
    }

    @Test
    void shouldConvertPersonToUpdater() {
        Person person = new Person();
        person.id = "user-4";
        person.name = "Diana";
        person.timestamp = new Date(4000L);

        Updater updater = PersonConvertor.toUpdater(person);
        assertEquals("user-4", updater.id());
        assertEquals("Diana", updater.name());
        assertEquals(4000L, updater.timestamp());
    }

    @Test
    void shouldConvertNullPersonToEmptyUpdater() {
        Person person = null;

        Updater updater = PersonConvertor.toUpdater(person);
        assertEquals("", updater.id());
        assertEquals("", updater.name());
    }
}
