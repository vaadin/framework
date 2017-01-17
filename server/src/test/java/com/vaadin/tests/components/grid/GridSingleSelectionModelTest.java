package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.provider.bov.Person;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.GridSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModelImpl;

import elemental.json.JsonObject;

public class GridSingleSelectionModelTest {

    public static final Person PERSON_C = new Person("c", 3);
    public static final Person PERSON_B = new Person("b", 2);
    public static final Person PERSON_A = new Person("a", 1);

    public static class CustomSingleSelectionModel
            extends SingleSelectionModelImpl<String> {
        public final Map<String, Boolean> generatedData = new LinkedHashMap<>();

        @Override
        public void generateData(String item, JsonObject jsonObject) {
            super.generateData(item, jsonObject);
            // capture updated row
            generatedData.put(item, isSelected(item));
        }

    }

    private static class TestSingleSelectionModel
            extends SingleSelectionModelImpl<Object> {

        public TestSingleSelectionModel() {
            getState(false).selectionAllowed = false;
        }

        @Override
        protected void setSelectedFromClient(String key) {
            super.setSelectedFromClient(key);
        }
    }

    private List<Person> selectionChanges;
    private Grid<Person> grid;
    private SingleSelectionModelImpl<Person> selectionModel;

    @Before
    public void setUp() {
        grid = new Grid<>();
        grid.setItems(PERSON_A, PERSON_B, PERSON_C);
        selectionModel = (SingleSelectionModelImpl<Person>) grid
                .getSelectionModel();

        selectionChanges = new ArrayList<>();
        selectionModel.addSingleSelectionListener(
                e -> selectionChanges.add(e.getValue()));
    }

    @Test(expected = IllegalStateException.class)
    public void throwExceptionWhenSelectionIsDisallowed() {
        TestSingleSelectionModel model = new TestSingleSelectionModel();
        model.setSelectedFromClient("foo");
    }

    @Test(expected = IllegalStateException.class)
    public void selectionModelChanged_usingPreviousSelectionModel_throws() {
        grid.setSelectionMode(SelectionMode.MULTI);

        selectionModel.select(PERSON_A);
    }

    @Test
    public void gridChangingSelectionModel_firesSelectionChangeEvent() {
        Grid<String> customGrid = new Grid<>();
        customGrid.setItems("Foo", "Bar", "Baz");

        List<String> selectionChanges = new ArrayList<>();
        List<String> oldSelectionValues = new ArrayList<>();
        ((SingleSelectionModelImpl<String>) customGrid.getSelectionModel())
                .addSingleSelectionListener(e -> {
                    selectionChanges.add(e.getValue());
                    oldSelectionValues.add(e.getOldValue());
                });

        customGrid.getSelectionModel().select("Foo");
        assertEquals("Foo",
                customGrid.getSelectionModel().getFirstSelectedItem().get());
        assertEquals(Arrays.asList("Foo"), selectionChanges);
        assertEquals(Arrays.asList((String) null), oldSelectionValues);

        customGrid.setSelectionMode(SelectionMode.MULTI);
        assertEquals(Arrays.asList("Foo", null), selectionChanges);
        assertEquals(Arrays.asList(null, "Foo"), oldSelectionValues);
    }

    @Test
    public void serverSideSelection_GridChangingSelectionModel_sendsUpdatedRowsToClient() {

        CustomSingleSelectionModel customModel = new CustomSingleSelectionModel();
        Grid<String> customGrid = new Grid<String>() {
            {
                setSelectionModel(customModel);
            }
        };
        customGrid.setItems("Foo", "Bar", "Baz");

        customGrid.getDataCommunicator().beforeClientResponse(true);

        Assert.assertFalse("Item should have been updated as selected",
                customModel.generatedData.get("Foo"));
        Assert.assertFalse("Item should have been updated as NOT selected",
                customModel.generatedData.get("Bar"));
        Assert.assertFalse("Item should have been updated as NOT selected",
                customModel.generatedData.get("Baz"));

        customModel.generatedData.clear();

        customGrid.getSelectionModel().select("Foo");
        customGrid.getDataCommunicator().beforeClientResponse(false);

        Assert.assertTrue("Item should have been updated as selected",
                customModel.generatedData.get("Foo"));
        Assert.assertFalse("Item should have NOT been updated",
                customModel.generatedData.containsKey("Bar"));
        Assert.assertFalse("Item should have NOT been updated",
                customModel.generatedData.containsKey("Baz"));

        // switch to another selection model to cause event
        customModel.generatedData.clear();
        customGrid.setSelectionMode(SelectionMode.MULTI);
        customGrid.getDataCommunicator().beforeClientResponse(false);

        // since the selection model has been removed, it is no longer a data
        // generator for the data communicator, would need to verify somehow
        // that row is not marked as selected anymore ? (done in UI tests)
        Assert.assertTrue(customModel.generatedData.isEmpty()); // at least
                                                                // removed
                                                                // selection
                                                                // model is not
                                                                // triggered
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
        SingleSelectionModelImpl<String> select = new SingleSelectionModelImpl<String>() {
            @Override
            public Registration addSingleSelectionListener(
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
                .addSingleSelectionListener(evt -> {
                    Assert.assertNull(event.get());
                    event.set(evt);
                });
        Assert.assertSame(registration, actualRegistration);

        selectionListener.get().selectionChange(new SingleSelectionEvent<>(grid,
                select.asSingleSelect(), null, true));

        Assert.assertEquals(grid, event.get().getComponent());
        Assert.assertEquals(value, event.get().getValue());
        Assert.assertEquals(null, event.get().getOldValue());
        Assert.assertTrue(event.get().isUserOriginated());
    }

}
