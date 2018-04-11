package com.vaadin.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.NativeSelect;

public class BinderSingleSelectTest
        extends BinderTestBase<Binder<Person>, Person> {

    private NativeSelect<Sex> select;

    @Before
    public void setUp() {
        binder = new Binder<>();
        item = new Person();
        select = new NativeSelect<>();
        select.setItems(Sex.values());
    }

    @Test
    public void personBound_bindSelectByShortcut_selectionUpdated() {
        item.setSex(Sex.FEMALE);
        binder.setBean(item);
        binder.bind(select, Person::getSex, Person::setSex);

        assertSame(Sex.FEMALE, select.getSelectedItem().orElse(null));
    }

    @Test
    public void personBound_bindSelect_selectionUpdated() {
        item.setSex(Sex.MALE);
        binder.setBean(item);
        binder.forField(select).bind(Person::getSex, Person::setSex);

        assertSame(Sex.MALE, select.getSelectedItem().orElse(null));
    }

    @Test
    public void selectBound_bindPersonWithNullSex_selectedItemNotPresent() {
        bindSex();

        assertFalse(select.getSelectedItem().isPresent());
    }

    @Test
    public void selectBound_bindPerson_selectionUpdated() {
        item.setSex(Sex.FEMALE);
        bindSex();

        assertSame(Sex.FEMALE, select.getSelectedItem().orElse(null));
    }

    @Test
    public void bound_setSelection_beanValueUpdated() {
        bindSex();

        select.setValue(Sex.MALE);

        assertSame(Sex.MALE, item.getSex());
    }

    @Test
    public void bound_deselect_beanValueUpdatedToNull() {
        item.setSex(Sex.MALE);
        bindSex();

        select.setValue(null);

        assertNull(item.getSex());
    }

    @Test
    public void unbound_changeSelection_beanValueNotUpdated() {
        item.setSex(Sex.UNKNOWN);
        bindSex();
        binder.removeBean();

        select.setValue(Sex.FEMALE);

        assertSame(Sex.UNKNOWN, item.getSex());
    }

    protected void bindSex() {
        binder.forField(select).bind(Person::getSex, Person::setSex);
        binder.setBean(item);
    }
}
