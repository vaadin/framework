package com.vaadin.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.AbstractField;

public class BinderTest {

    class TextField extends AbstractField<String> {

        String value = "";

        @Override
        public String getValue() {
            return value;
        }

        @Override
        protected void doSetValue(String value) {
            this.value = value;
        }
    }

    Binder<Person> binder;

    TextField nameField;

    Person p = new Person();

    @Before
    public void setUp() {
        binder = new Binder<>();
        p.setFirstName("Johannes");
        nameField = new TextField();
    }

    @Test(expected = NullPointerException.class)
    public void bindingNullBeanThrows() {
        binder.bind(null);
    }

    @Test(expected = NullPointerException.class)
    public void bindingNullFieldThrows() {
        binder.forField(null);
    }

    @Test(expected = NullPointerException.class)
    public void bindingNullGetterThrows() {
        binder.bind(nameField, null, Person::setFirstName);
    }

    @Test
    public void fieldValueUpdatedOnBeanBind() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);
        binder.bind(p);
        assertEquals("Johannes", nameField.getValue());
    }

    @Test
    public void fieldValueUpdatedWithShortcutBind() {
        bindName();
        assertEquals("Johannes", nameField.getValue());
    }

    @Test
    public void fieldValueUpdatedIfBeanAlreadyBound() {
        binder.bind(p);
        binder.bind(nameField, Person::getFirstName, Person::setFirstName);

        assertEquals("Johannes", nameField.getValue());
        nameField.setValue("Artur");
        assertEquals("Artur", p.getFirstName());
    }

    @Test
    public void getBeanReturnsBoundBeanOrNothing() {
        assertFalse(binder.getBean().isPresent());
        binder.bind(p);
        assertSame(p, binder.getBean().get());
        binder.unbind();
        assertFalse(binder.getBean().isPresent());
    }

    @Test
    public void fieldValueSavedToPropertyOnChange() {
        bindName();
        nameField.setValue("Henri");
        assertEquals("Henri", p.getFirstName());
    }

    @Test
    public void fieldValueNotSavedAfterUnbind() {
        bindName();
        nameField.setValue("Henri");
        binder.unbind();
        nameField.setValue("Aleksi");
        assertEquals("Henri", p.getFirstName());
    }

    @Test
    public void bindNullSetterIgnoresValueChange() {
        binder.bind(nameField, Person::getFirstName, null);
        binder.bind(p);
        nameField.setValue("Artur");
        assertEquals(p.getFirstName(), "Johannes");
    }

    @Test
    public void bindToAnotherBeanStopsUpdatingOriginalBean() {
        bindName();
        nameField.setValue("Leif");

        Person p2 = new Person();
        p2.setFirstName("Marlon");
        binder.bind(p2);
        assertEquals("Marlon", nameField.getValue());
        assertEquals("Leif", p.getFirstName());
        assertSame(p2, binder.getBean().get());

        nameField.setValue("Ilia");
        assertEquals("Ilia", p2.getFirstName());
        assertEquals("Leif", p.getFirstName());
    }

    private void bindName() {
        binder.bind(nameField, Person::getFirstName, Person::setFirstName);
        binder.bind(p);
    }
}
