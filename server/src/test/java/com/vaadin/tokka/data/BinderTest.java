package com.vaadin.tokka.data;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.tests.data.bean.Person;
import com.vaadin.tokka.data.Binder;
import com.vaadin.tokka.ui.components.fields.TextField;

public class BinderTest {

    Binder<Person> binder;

    TextField nameField;
    TextField ageField;

    Person p = new Person();

    @Before
    public void setUp() {
        binder = new Binder<>();
        p.setFirstName("Johannes");
        p.setAge(32);
        nameField = new TextField();
        ageField = new TextField();
    }

    @Test
    public void testAddFieldAndBind() {
        binder.addField(nameField).bind(Person::getFirstName,
                Person::setFirstName);
        binder.bind(p);

        assertEquals("Johannes", nameField.getValue());
    }

    @Test
    public void testAddFieldShortcut() {
        binder.addField(nameField, Person::getFirstName, Person::setFirstName);
        binder.bind(p);

        assertEquals("Johannes", nameField.getValue());
    }

    @Test
    public void testValueChangeOnSave() {
        binder.addField(nameField, Person::getFirstName, Person::setFirstName);
        binder.bind(p);

        nameField.setValue("Teemu");

        assertEquals("Johannes", p.getFirstName());

        binder.save();

        assertEquals("Teemu", p.getFirstName());
    }

    @Test
    public void testAddFieldAfterBinding() {
        binder.bind(p);
        binder.addField(nameField, Person::getFirstName, Person::setFirstName);

        assertEquals("Johannes", nameField.getValue());
    }

    @Test
    public void testNoValueChangesAfterUnbind() {
        binder.addField(nameField, Person::getFirstName, Person::setFirstName);
        binder.bind(p);

        binder.bind(null);

        nameField.setValue("Teemu");
        binder.save();

        assertEquals("Johannes", p.getFirstName());
    }
}
