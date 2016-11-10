package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.server.data.provider.bov.Person;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.DataCommunicatorClientRpc;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.GridSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModel;

public class GridSingleSelectionModelTest {

    public static final Person PERSON_C = new Person("c", 3);
    public static final Person PERSON_B = new Person("b", 2);
    public static final Person PERSON_A = new Person("a", 1);
    public static final String RPC_INTERFACE = DataCommunicatorClientRpc.class
            .getName();

    private class CustomSelectionModelGrid extends Grid<String> {
        public void switchSelectionModel() {
            // just switch selection model to cause event
            setSelectionModel(new SingleSelectionModel(this));
        }
    }

    private List<Person> selectionChanges;
    private Grid<Person> grid;
    private SingleSelectionModel<Person> selectionModel;

    @Before
    public void setUp() {
        grid = new Grid<>();
        grid.setItems(PERSON_A, PERSON_B, PERSON_C);
        selectionModel = (SingleSelectionModel<Person>) grid
                .getSelectionModel();

        selectionChanges = new ArrayList<>();
        selectionModel.addSelectionChangeListener(
                e -> selectionChanges.add(e.getValue()));
    }

    @Test
    public void testGridChangingSelectionModel_firesSelectionChangeEvent() {
        CustomSelectionModelGrid customGrid = new CustomSelectionModelGrid();
        customGrid.setItems("Foo", "Bar", "Baz");

        List<String> selectionChanges = new ArrayList<>();
        ((SingleSelectionModel<String>) customGrid.getSelectionModel())
                .addSelectionChangeListener(
                        e -> selectionChanges.add(e.getValue()));

        customGrid.getSelectionModel().select("Foo");
        assertEquals("Foo",
                customGrid.getSelectionModel().getFirstSelectedItem().get());
        assertEquals(Arrays.asList("Foo"), selectionChanges);

        customGrid.switchSelectionModel();
        assertEquals(Arrays.asList("Foo", null), selectionChanges);
    }

    @Test
    public void testGridWithSingleSelection() {
        Grid<String> gridWithStrings = new Grid<>();
        gridWithStrings.setItems("Foo", "Bar", "Baz");

        GridSelectionModel<String> model = gridWithStrings.getSelectionModel();
        Assert.assertFalse(model.isSelected("Foo"));

        model.select("Foo");
        Assert.assertTrue(model.isSelected("Foo"));
        Assert.assertEquals(Optional.of("Foo"), model.getFirstSelectedItem());

        model.select("Bar");
        Assert.assertFalse(model.isSelected("Foo"));
        Assert.assertTrue(model.isSelected("Bar"));

        model.deselect("Bar");
        Assert.assertFalse(model.isSelected("Bar"));
        Assert.assertFalse(model.getFirstSelectedItem().isPresent());
    }

    @Test
    public void select_isSelected() {
        selectionModel.select(PERSON_B);

        assertTrue(selectionModel.getSelectedItem().isPresent());

        assertEquals(PERSON_B, selectionModel.getSelectedItem().orElse(null));

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertTrue(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));

        assertEquals(Optional.of(PERSON_B), selectionModel.getSelectedItem());

        assertEquals(Arrays.asList(PERSON_B), selectionChanges);
    }

    @Test
    public void selectDeselect() {

        selectionModel.select(PERSON_B);
        selectionModel.deselect(PERSON_B);

        assertFalse(selectionModel.getSelectedItem().isPresent());

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));

        assertFalse(selectionModel.getSelectedItem().isPresent());

        assertEquals(Arrays.asList(PERSON_B, null), selectionChanges);
    }

    @Test
    public void reselect() {
        selectionModel.select(PERSON_B);
        selectionModel.select(PERSON_C);

        assertEquals(PERSON_C, selectionModel.getSelectedItem().orElse(null));

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertTrue(selectionModel.isSelected(PERSON_C));

        assertEquals(Optional.of(PERSON_C), selectionModel.getSelectedItem());

        assertEquals(Arrays.asList(PERSON_B, PERSON_C), selectionChanges);
    }

    @Test
    public void selectTwice() {

        selectionModel.select(PERSON_C);
        selectionModel.select(PERSON_C);

        assertEquals(PERSON_C, selectionModel.getSelectedItem().orElse(null));

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertTrue(selectionModel.isSelected(PERSON_C));

        assertEquals(Optional.of(PERSON_C), selectionModel.getSelectedItem());

        assertEquals(Arrays.asList(PERSON_C), selectionChanges);
    }

    @Test
    public void deselectTwice() {

        selectionModel.select(PERSON_C);
        selectionModel.deselect(PERSON_C);
        selectionModel.deselect(PERSON_C);

        assertFalse(selectionModel.getSelectedItem().isPresent());

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));

        assertFalse(selectionModel.getSelectedItem().isPresent());

        assertEquals(Arrays.asList(PERSON_C, null), selectionChanges);
    }

    @Test
    public void getSelectedItem() {
        selectionModel.setSelectedItem(PERSON_B);

        Assert.assertEquals(PERSON_B, selectionModel.getSelectedItem().get());

        selectionModel.deselect(PERSON_B);
        Assert.assertFalse(selectionModel.getSelectedItem().isPresent());
    }

    @Test
    public void select_deselect_getSelectedItem() {
        selectionModel.select(PERSON_C);

        Assert.assertEquals(PERSON_C, selectionModel.getSelectedItem().get());

        selectionModel.deselect(PERSON_C);

        Assert.assertFalse(selectionModel.getSelectedItem().isPresent());
    }

    @SuppressWarnings({ "serial" })
    @Test
    public void addValueChangeListener() {
        AtomicReference<SingleSelectionListener<String>> selectionListener = new AtomicReference<>();
        Registration registration = Mockito.mock(Registration.class);
        Grid<String> grid = new Grid<>();
        grid.setItems("foo", "bar");
        String value = "foo";
        SingleSelectionModel<String> select = new SingleSelectionModel<String>(
                grid) {
            @Override
            public Registration addSelectionChangeListener(
                    SingleSelectionListener<String> listener) {
                selectionListener.set(listener);
                return registration;
            }

            @Override
            public Optional<String> getSelectedItem() {
                return Optional.of(value);
            }
        };

        AtomicReference<ValueChangeEvent<?>> event = new AtomicReference<>();
        Registration actualRegistration = select
                .addSelectionChangeListener(evt -> {
                    Assert.assertNull(event.get());
                    event.set(evt);
                });
        Assert.assertSame(registration, actualRegistration);

        selectionListener.get().accept(new SingleSelectionEvent<>(grid,
                select.asSingleSelect(), true));

        Assert.assertEquals(grid, event.get().getComponent());
        Assert.assertEquals(value, event.get().getValue());
        Assert.assertTrue(event.get().isUserOriginated());
    }

}
