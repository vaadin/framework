package com.vaadin.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.SingleSelect;
import com.vaadin.ui.components.grid.SingleSelectionModelImpl;

public class GridAsSingleSelectInBinderTest
        extends BinderTestBase<Binder<Person>, Person> {

    private class GridWithCustomSingleSelectionModel extends Grid<Sex> {
        @Override
        public void setSelectionModel(
                com.vaadin.ui.components.grid.GridSelectionModel<Sex> model) {
            super.setSelectionModel(model);
        }
    }

    private class CustomSingleSelectModel
            extends SingleSelectionModelImpl<Sex> {

        public void setSelectedFromClient(Sex item) {
            setSelectedFromClient(
                    getGrid().getDataCommunicator().getKeyMapper().key(item));
        }
    }

    private Grid<Sex> grid;
    private SingleSelect<Sex> select;

    @Before
    public void setup() {
        binder = new Binder<>();
        item = new Person();
        grid = new Grid<>();
        grid.setItems(Sex.values());
        select = grid.asSingleSelect();
    }

    @Test(expected = IllegalStateException.class)
    public void boundGridInBinder_selectionModelChanged_throws() {
        grid.setSelectionMode(SelectionMode.MULTI);

        select.setValue(Sex.MALE);
    }

    @Test
    public void personBound_bindSelectByShortcut_selectionUpdated() {
        item.setSex(Sex.FEMALE);
        binder.setBean(item);
        binder.bind(select, Person::getSex, Person::setSex);

        assertSame(Sex.FEMALE, select.getValue());
    }

    @Test
    public void personBound_bindSelect_selectionUpdated() {
        item.setSex(Sex.MALE);
        binder.setBean(item);
        binder.forField(select).bind(Person::getSex, Person::setSex);

        assertSame(Sex.MALE, select.getValue());
    }

    @Test
    public void selectBound_bindPersonWithNullSex_selectedItemNotPresent() {
        bindSex();

        assertFalse(select.getValue() != null);
    }

    @Test
    public void selectBound_bindPerson_selectionUpdated() {
        item.setSex(Sex.FEMALE);
        bindSex();

        assertSame(Sex.FEMALE, select.getValue());
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

    @Test
    public void addValueChangeListener_selectionUpdated_eventTriggeredForSelect() {
        GridWithCustomSingleSelectionModel grid = new GridWithCustomSingleSelectionModel();
        CustomSingleSelectModel model = new CustomSingleSelectModel();
        grid.setSelectionModel(model);
        grid.setItems(Sex.values());
        select = grid.asSingleSelect();

        List<Sex> selected = new ArrayList<>();
        List<Sex> oldSelected = new ArrayList<>();
        List<Boolean> userOriginated = new ArrayList<>();
        select.addValueChangeListener(event -> {
            selected.add(event.getValue());
            oldSelected.add(event.getOldValue());
            userOriginated.add(event.isUserOriginated());
            assertSame(grid, event.getComponent());
            // cannot compare that the event source is the select since a new
            // SingleSelect wrapper object has been created for the event
            assertSame(select.getValue(), event.getValue());
        });

        grid.getSelectionModel().select(Sex.UNKNOWN);
        model.setSelectedFromClient(Sex.MALE); // simulates client side
                                               // selection
        grid.getSelectionModel().select(Sex.MALE); // NOOP
        grid.getSelectionModel().deselect(Sex.UNKNOWN); // NOOP
        model.setSelectedFromClient(null); // simulates deselect from client
                                           // side
        grid.getSelectionModel().select(Sex.FEMALE);

        assertEquals(Arrays.asList(Sex.UNKNOWN, Sex.MALE, null, Sex.FEMALE),
                selected);
        assertEquals(Arrays.asList(null, Sex.UNKNOWN, Sex.MALE, null),
                oldSelected);
        assertEquals(Arrays.asList(false, true, true, false), userOriginated);
    }

    protected void bindSex() {
        binder.forField(select).bind(Person::getSex, Person::setSex);
        binder.setBean(item);
    }
}
