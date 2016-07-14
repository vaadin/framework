package com.vaadin.tokka.data;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.tests.data.bean.Person;
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
        bindName();
        assertEquals("Johannes", nameField.getValue());
    }

    @Test
    public void testValueChangeOnSave() {
        bindName();

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
        bindName();

        binder.bind(null);
        nameField.setValue("Teemu");
        binder.save();
        assertEquals("Johannes", p.getFirstName());
    }

    @Test
    public void testValidationEmptyNameFails() {
        bindNameNonEmpty();

        nameField.setValue("");
        binder.save();
        assertEquals("Johannes", p.getFirstName());
    }

    @Test
    public void testValidationNonEmptyNamePasses() {
        bindNameNonEmpty();

        nameField.setValue("Leif");
        binder.save();
        assertEquals("Leif", p.getFirstName());
    }

    @Test
    public void testMultipleValidatorsEmptyNameFails() {
        bindNameTwoValidators();

        nameField.setValue("");
        binder.save();
        assertEquals("Johannes", p.getFirstName());
    }

    @Test
    public void testMultipleValidatorsTooLongNameFails() {
        bindNameTwoValidators();
        nameField.setValue("This Name Is Very Long Indeed");
        binder.save();
        assertEquals("Johannes", p.getFirstName());
    }

    @Test
    public void testMultipleValidatorsShortEnoughNamePasses() {
        bindNameTwoValidators();
        nameField.setValue("Ilia");
        binder.save();
        assertEquals("Ilia", p.getFirstName());
    }

    private void bindName() {
        binder.addField(nameField, Person::getFirstName, Person::setFirstName);
        binder.bind(p);
    }

    private void bindNameNonEmpty() {
        binder.addField(nameField)
                .addValidator(Validator.from(val -> !val.isEmpty(),
                        "Name cannot be empty"))
                .bind(Person::getFirstName, Person::setFirstName);
        binder.bind(p);
    }

    private void bindNameTwoValidators() {
        binder.addField(nameField)
                .addValidator(Validator.from(val -> !val.isEmpty(),
                        "Name cannot be empty"))
                .addValidator(Validator.from(val -> val.length() < 20,
                        "Name must be shorter than 20 letters"))

                .bind(Person::getFirstName, Person::setFirstName);
        binder.bind(p);
    }
}
