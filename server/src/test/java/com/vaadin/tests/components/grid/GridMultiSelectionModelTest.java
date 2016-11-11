package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.easymock.Capture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.server.data.provider.bov.Person;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.GridSelectionModel;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;
import com.vaadin.ui.components.grid.SingleSelectionModelImpl;

import elemental.json.JsonObject;

public class GridMultiSelectionModelTest {

    public static final Person PERSON_C = new Person("c", 3);
    public static final Person PERSON_B = new Person("b", 2);
    public static final Person PERSON_A = new Person("a", 1);

    private Grid<Person> grid;
    private MultiSelectionModelImpl<Person> selectionModel;
    private Capture<List<Person>> currentSelectionCapture;
    private Capture<List<Person>> oldSelectionCapture;
    private AtomicInteger events;

    public static class CustomMultiSelectionModel
            extends MultiSelectionModelImpl<String> {
        public final Map<String, Boolean> generatedData = new LinkedHashMap<>();

        public CustomMultiSelectionModel(Grid<String> grid) {
            super(grid);
        }

        @Override
        public void generateData(String item, JsonObject jsonObject) {
            super.generateData(item, jsonObject);
            // capture updated row
            generatedData.put(item, isSelected(item));
        }

    }

    @Before
    public void setUp() {
        grid = new Grid<>();
        selectionModel = new MultiSelectionModelImpl<>(grid);
        grid.setSelectionModel(selectionModel);
        grid.setItems(PERSON_A, PERSON_B, PERSON_C);

        currentSelectionCapture = new Capture<>();
        oldSelectionCapture = new Capture<>();
        events = new AtomicInteger();

        selectionModel.addSelectionListener(event -> {
            currentSelectionCapture
                    .setValue(new ArrayList<>(event.getNewSelection()));
            oldSelectionCapture
                    .setValue(new ArrayList<>(event.getOldSelection()));
            events.incrementAndGet();
        });
    }

    @Test(expected = IllegalStateException.class)
    public void selectionModelChanged_usingPreviousSelectionModel_throws() {
        grid.setSelectionModel(new SingleSelectionModelImpl<>(grid));

        selectionModel.select(PERSON_A);
    }

    @Test
    public void changingSelectionModel_firesSelectionEvent() {
        Grid<String> customGrid = new Grid<>();
        customGrid.setSelectionModel(new MultiSelectionModelImpl<>(customGrid));
        customGrid.setItems("Foo", "Bar", "Baz");

        List<String> selectionChanges = new ArrayList<>();
        Capture<List<String>> oldSelectionCapture = new Capture<>();
        ((MultiSelectionModelImpl<String>) customGrid.getSelectionModel())
                .addSelectionListener(e -> {
                    selectionChanges.addAll(e.getValue());
                    oldSelectionCapture
                            .setValue(new ArrayList<>(e.getOldSelection()));
                });

        customGrid.getSelectionModel().select("Foo");
        assertEquals(Arrays.asList("Foo"), selectionChanges);
        selectionChanges.clear();

        customGrid.getSelectionModel().select("Bar");
        assertEquals("Foo",
                customGrid.getSelectionModel().getFirstSelectedItem().get());
        assertEquals(Arrays.asList("Foo", "Bar"), selectionChanges);
        selectionChanges.clear();

        customGrid.setSelectionModel(new SingleSelectionModelImpl<>(customGrid));
        assertFalse(customGrid.getSelectionModel().getFirstSelectedItem()
                .isPresent());
        assertEquals(Arrays.asList(), selectionChanges);
        assertEquals(Arrays.asList("Foo", "Bar"),
                oldSelectionCapture.getValue());
    }

    @Test
    public void serverSideSelection_GridChangingSelectionModel_sendsUpdatedRowsToClient() {
        Grid<String> customGrid = new Grid<>();
        customGrid.setItems("Foo", "Bar", "Baz");

        CustomMultiSelectionModel customModel = new CustomMultiSelectionModel(
                customGrid);
        customGrid.setSelectionModel(customModel);
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

        customModel.generatedData.clear();

        customModel.updateSelection(asSet("Bar"), asSet("Foo"));
        customGrid.getDataCommunicator().beforeClientResponse(false);

        Assert.assertFalse("Item should have been updated as NOT selected",
                customModel.generatedData.get("Foo"));
        Assert.assertTrue("Item should have been updated as selected",
                customModel.generatedData.get("Bar"));
        Assert.assertFalse("Item should have NOT been updated",
                customModel.generatedData.containsKey("Baz"));

        // switch to single to cause event
        customModel.generatedData.clear();
        customGrid.setSelectionModel(new SingleSelectionModelImpl<>(customGrid));
        customGrid.getDataCommunicator().beforeClientResponse(false);

        // changing selection model should trigger row updates, but the old
        // selection model is not triggered as it has been removed
        Assert.assertTrue(customModel.generatedData.isEmpty()); // not triggered
    }

    @Test
    public void select_gridWithStrings() {
        Grid<String> gridWithStrings = new Grid<>();
        gridWithStrings
                .setSelectionModel(new MultiSelectionModelImpl<>(gridWithStrings));
        gridWithStrings.setItems("Foo", "Bar", "Baz");

        GridSelectionModel<String> model = gridWithStrings.getSelectionModel();
        Assert.assertFalse(model.isSelected("Foo"));

        model.select("Foo");
        Assert.assertTrue(model.isSelected("Foo"));
        Assert.assertEquals(Optional.of("Foo"), model.getFirstSelectedItem());

        model.select("Bar");
        Assert.assertTrue(model.isSelected("Foo"));
        Assert.assertTrue(model.isSelected("Bar"));
        Assert.assertEquals(Arrays.asList("Foo", "Bar"),
                new ArrayList<>(model.getSelectedItems()));

        model.deselect("Bar");
        Assert.assertFalse(model.isSelected("Bar"));
        Assert.assertTrue(model.getFirstSelectedItem().isPresent());
        Assert.assertEquals(Arrays.asList("Foo"),
                new ArrayList<>(model.getSelectedItems()));
    }

    @Test
    public void select() {
        selectionModel.select(PERSON_B);

        assertEquals(PERSON_B,
                selectionModel.getFirstSelectedItem().orElse(null));
        assertEquals(Optional.of(PERSON_B),
                selectionModel.getFirstSelectedItem());

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertTrue(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));

        assertEquals(Arrays.asList(PERSON_B),
                currentSelectionCapture.getValue());

        selectionModel.select(PERSON_A);
        assertEquals(PERSON_B,
                selectionModel.getFirstSelectedItem().orElse(null));

        assertTrue(selectionModel.isSelected(PERSON_A));
        assertTrue(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));

        assertEquals(Arrays.asList(PERSON_B, PERSON_A),
                currentSelectionCapture.getValue());
        assertEquals(2, events.get());
    }

    @Test
    public void deselect() {
        selectionModel.select(PERSON_B);
        selectionModel.deselect(PERSON_B);

        assertFalse(selectionModel.getFirstSelectedItem().isPresent());

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));

        assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        assertEquals(2, events.get());
    }

    @Test
    public void selectItems() {
        selectionModel.selectItems(PERSON_C, PERSON_B);

        assertEquals(PERSON_C,
                selectionModel.getFirstSelectedItem().orElse(null));
        assertEquals(Optional.of(PERSON_C),
                selectionModel.getFirstSelectedItem());

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertTrue(selectionModel.isSelected(PERSON_B));
        assertTrue(selectionModel.isSelected(PERSON_C));

        assertEquals(Arrays.asList(PERSON_C, PERSON_B),
                currentSelectionCapture.getValue());

        selectionModel.selectItems(PERSON_A, PERSON_C); // partly NOOP
        assertTrue(selectionModel.isSelected(PERSON_A));
        assertTrue(selectionModel.isSelected(PERSON_B));
        assertTrue(selectionModel.isSelected(PERSON_C));

        assertEquals(Arrays.asList(PERSON_C, PERSON_B, PERSON_A),
                currentSelectionCapture.getValue());
        assertEquals(2, events.get());
    }

    @Test
    public void deselectItems() {
        selectionModel.selectItems(PERSON_C, PERSON_A, PERSON_B);

        selectionModel.deselectItems(PERSON_A);
        assertEquals(PERSON_C,
                selectionModel.getFirstSelectedItem().orElse(null));
        assertEquals(Optional.of(PERSON_C),
                selectionModel.getFirstSelectedItem());

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertTrue(selectionModel.isSelected(PERSON_B));
        assertTrue(selectionModel.isSelected(PERSON_C));

        assertEquals(Arrays.asList(PERSON_C, PERSON_B),
                currentSelectionCapture.getValue());

        selectionModel.deselectItems(PERSON_A, PERSON_B, PERSON_C);
        assertNull(selectionModel.getFirstSelectedItem().orElse(null));
        assertEquals(Optional.empty(), selectionModel.getFirstSelectedItem());

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));

        assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        assertEquals(3, events.get());
    }

    @Test
    public void selectionEvent_newSelection_oldSelection() {
        selectionModel.selectItems(PERSON_C, PERSON_A, PERSON_B);

        assertEquals(Arrays.asList(PERSON_C, PERSON_A, PERSON_B),
                currentSelectionCapture.getValue());
        assertEquals(Arrays.asList(), oldSelectionCapture.getValue());

        selectionModel.deselect(PERSON_A);

        assertEquals(Arrays.asList(PERSON_C, PERSON_B),
                currentSelectionCapture.getValue());
        assertEquals(Arrays.asList(PERSON_C, PERSON_A, PERSON_B),
                oldSelectionCapture.getValue());

        selectionModel.deselectItems(PERSON_A, PERSON_B, PERSON_C);
        assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        assertEquals(Arrays.asList(PERSON_C, PERSON_B),
                oldSelectionCapture.getValue());

        selectionModel.selectItems(PERSON_A);
        assertEquals(Arrays.asList(PERSON_A),
                currentSelectionCapture.getValue());
        assertEquals(Arrays.asList(), oldSelectionCapture.getValue());

        selectionModel.updateSelection(
                new LinkedHashSet<>(Arrays.asList(PERSON_B, PERSON_C)),
                new LinkedHashSet<>(Arrays.asList(PERSON_A)));
        assertEquals(Arrays.asList(PERSON_B, PERSON_C),
                currentSelectionCapture.getValue());
        assertEquals(Arrays.asList(PERSON_A), oldSelectionCapture.getValue());

        selectionModel.deselectAll();
        assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        assertEquals(Arrays.asList(PERSON_B, PERSON_C),
                oldSelectionCapture.getValue());

        selectionModel.select(PERSON_C);
        assertEquals(Arrays.asList(PERSON_C),
                currentSelectionCapture.getValue());
        assertEquals(Arrays.asList(), oldSelectionCapture.getValue());

        selectionModel.deselect(PERSON_C);
        assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        assertEquals(Arrays.asList(PERSON_C), oldSelectionCapture.getValue());
    }

    @Test
    public void deselectAll() {
        selectionModel.selectItems(PERSON_A, PERSON_C, PERSON_B);

        assertTrue(selectionModel.isSelected(PERSON_A));
        assertTrue(selectionModel.isSelected(PERSON_B));
        assertTrue(selectionModel.isSelected(PERSON_C));
        assertEquals(Arrays.asList(PERSON_A, PERSON_C, PERSON_B),
                currentSelectionCapture.getValue());
        assertEquals(1, events.get());

        selectionModel.deselectAll();
        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));
        assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        assertEquals(Arrays.asList(PERSON_A, PERSON_C, PERSON_B),
                oldSelectionCapture.getValue());
        assertEquals(2, events.get());

        selectionModel.select(PERSON_C);
        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertTrue(selectionModel.isSelected(PERSON_C));
        assertEquals(Arrays.asList(PERSON_C),
                currentSelectionCapture.getValue());
        assertEquals(Arrays.asList(), oldSelectionCapture.getValue());
        assertEquals(3, events.get());

        selectionModel.deselectAll();
        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));
        assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        assertEquals(Arrays.asList(PERSON_C), oldSelectionCapture.getValue());
        assertEquals(4, events.get());

        selectionModel.deselectAll();
        assertEquals(4, events.get());
    }

    @Test
    public void updateSelection() {
        selectionModel.updateSelection(asSet(PERSON_A), Collections.emptySet());

        assertTrue(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));
        assertEquals(Arrays.asList(PERSON_A),
                currentSelectionCapture.getValue());
        assertEquals(1, events.get());

        selectionModel.updateSelection(asSet(PERSON_B), asSet(PERSON_A));

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertTrue(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));
        assertEquals(Arrays.asList(PERSON_B),
                currentSelectionCapture.getValue());
        assertEquals(Arrays.asList(PERSON_A), oldSelectionCapture.getValue());
        assertEquals(2, events.get());

        selectionModel.updateSelection(asSet(PERSON_B), asSet(PERSON_A)); // NOOP

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertTrue(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));
        assertEquals(Arrays.asList(PERSON_B),
                currentSelectionCapture.getValue());
        assertEquals(Arrays.asList(PERSON_A), oldSelectionCapture.getValue());
        assertEquals(2, events.get());

        selectionModel.updateSelection(asSet(PERSON_A, PERSON_C),
                asSet(PERSON_A)); // partly NOOP

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertTrue(selectionModel.isSelected(PERSON_B));
        assertTrue(selectionModel.isSelected(PERSON_C));
        assertEquals(Arrays.asList(PERSON_B, PERSON_C),
                currentSelectionCapture.getValue());
        assertEquals(Arrays.asList(PERSON_B), oldSelectionCapture.getValue());
        assertEquals(3, events.get());

        selectionModel.updateSelection(asSet(PERSON_B, PERSON_A),
                asSet(PERSON_B)); // partly NOOP

        assertTrue(selectionModel.isSelected(PERSON_A));
        assertTrue(selectionModel.isSelected(PERSON_B));
        assertTrue(selectionModel.isSelected(PERSON_C));
        assertEquals(Arrays.asList(PERSON_B, PERSON_C, PERSON_A),
                currentSelectionCapture.getValue());
        assertEquals(Arrays.asList(PERSON_B, PERSON_C),
                oldSelectionCapture.getValue());
        assertEquals(4, events.get());

        selectionModel.updateSelection(asSet(),
                asSet(PERSON_B, PERSON_A, PERSON_C));

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));
        assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        assertEquals(Arrays.asList(PERSON_B, PERSON_C, PERSON_A),
                oldSelectionCapture.getValue());
        assertEquals(5, events.get());
    }

    private <T> Set<T> asSet(@SuppressWarnings("unchecked") T... people) {
        return new LinkedHashSet<>(Arrays.asList(people));
    }

    @Test
    public void selectTwice() {
        selectionModel.select(PERSON_C);
        selectionModel.select(PERSON_C);

        assertEquals(PERSON_C,
                selectionModel.getFirstSelectedItem().orElse(null));

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertTrue(selectionModel.isSelected(PERSON_C));

        assertEquals(Optional.of(PERSON_C),
                selectionModel.getFirstSelectedItem());

        assertEquals(Arrays.asList(PERSON_C),
                currentSelectionCapture.getValue());
        assertEquals(1, events.get());
    }

    @Test
    public void deselectTwice() {
        selectionModel.select(PERSON_C);
        assertEquals(Arrays.asList(PERSON_C),
                currentSelectionCapture.getValue());
        assertEquals(1, events.get());

        selectionModel.deselect(PERSON_C);

        assertFalse(selectionModel.getFirstSelectedItem().isPresent());
        assertFalse(selectionModel.isSelected(PERSON_C));
        assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        assertEquals(2, events.get());

        selectionModel.deselect(PERSON_C);

        assertFalse(selectionModel.getFirstSelectedItem().isPresent());
        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));
        assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        assertEquals(2, events.get());
    }

    @SuppressWarnings({ "serial" })
    @Test
    public void addValueChangeListener() {
        AtomicReference<SingleSelectionListener<String>> selectionListener = new AtomicReference<>();
        Registration registration = Mockito.mock(Registration.class);
        Grid<String> grid = new Grid<>();
        grid.setItems("foo", "bar");
        String value = "foo";
        SingleSelectionModelImpl<String> select = new SingleSelectionModelImpl<String>(
                grid) {
            @Override
            public Registration addSelectionListener(
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
        Registration actualRegistration = select.addSelectionListener(evt -> {
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
