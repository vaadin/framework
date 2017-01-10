package com.vaadin.tests.server.component.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.easymock.Capture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.SortOrder;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridTest {

    private Grid<String> grid;

    @Before
    public void setUp() {
        grid = new Grid<>();

        grid.addColumn(ValueProvider.identity()).setId("foo");
        grid.addColumn(String::length, new NumberRenderer());
        grid.addColumn(string -> new Object());
        grid.addColumn(ValueProvider.identity()).setId("randomColumnId");
    }

    @Test
    public void testGridHeightModeChange() {
        assertEquals("Initial height mode was not CSS", HeightMode.CSS,
                grid.getHeightMode());
        grid.setHeightByRows(13.24);
        assertEquals("Setting height by rows did not change height mode",
                HeightMode.ROW, grid.getHeightMode());
        grid.setHeight("100px");
        assertEquals("Setting height did not change height mode.",
                HeightMode.CSS, grid.getHeightMode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFrozenColumnCountTooBig() {
        grid.setFrozenColumnCount(5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFrozenColumnCountTooSmall() {
        grid.setFrozenColumnCount(-2);
    }

    @Test()
    public void testSetFrozenColumnCount() {
        for (int i = -1; i < 2; ++i) {
            grid.setFrozenColumnCount(i);
            assertEquals("Frozen column count not updated", i,
                    grid.getFrozenColumnCount());
        }
    }

    @Test
    public void testGridColumnIdentifier() {
        grid.getColumn("foo").setCaption("Bar");
        assertEquals("Column header not updated correctly", "Bar",
                grid.getHeaderRow(0).getCell("foo").getText());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGridMultipleColumnsWithSameIdentifier() {
        grid.addColumn(t -> t).setId("foo");
    }

    @Test
    public void testAddSelectionListener_singleSelectMode() {
        grid.setItems("foo", "bar", "baz");

        Capture<SelectionEvent<String>> eventCapture = new Capture<>();

        grid.addSelectionListener(event -> eventCapture.setValue(event));

        grid.getSelectionModel().select("foo");

        SelectionEvent<String> event = eventCapture.getValue();
        assertNotNull(event);
        assertFalse(event.isUserOriginated());
        assertEquals("foo", event.getFirstSelected().get());
        assertEquals("foo",
                event.getAllSelectedItems().stream().findFirst().get());

        grid.getSelectionModel().select("bar");

        event = eventCapture.getValue();
        assertNotNull(event);
        assertFalse(event.isUserOriginated());
        assertEquals("bar", event.getFirstSelected().get());
        assertEquals("bar",
                event.getAllSelectedItems().stream().findFirst().get());

        grid.getSelectionModel().deselect("bar");

        event = eventCapture.getValue();
        assertNotNull(event);
        assertFalse(event.isUserOriginated());
        assertEquals(Optional.empty(), event.getFirstSelected());
        assertEquals(0, event.getAllSelectedItems().size());
    }

    @Test
    public void testAddSelectionListener_multiSelectMode() {
        grid.setItems("foo", "bar", "baz");
        grid.setSelectionMode(SelectionMode.MULTI);

        Capture<SelectionEvent<String>> eventCapture = new Capture<>();

        grid.addSelectionListener(event -> eventCapture.setValue(event));

        grid.getSelectionModel().select("foo");

        SelectionEvent<String> event = eventCapture.getValue();
        assertNotNull(event);
        assertFalse(event.isUserOriginated());
        assertEquals("foo", event.getFirstSelected().get());
        assertEquals("foo",
                event.getAllSelectedItems().stream().findFirst().get());

        grid.getSelectionModel().select("bar");

        event = eventCapture.getValue();
        assertNotNull(event);
        assertFalse(event.isUserOriginated());
        assertEquals("foo", event.getFirstSelected().get());
        assertEquals("foo",
                event.getAllSelectedItems().stream().findFirst().get());
        Assert.assertArrayEquals(new String[] { "foo", "bar" },
                event.getAllSelectedItems().toArray(new String[2]));

        grid.getSelectionModel().deselect("foo");

        assertNotNull(event);
        assertFalse(event.isUserOriginated());
        assertEquals("bar", event.getFirstSelected().get());
        assertEquals("bar",
                event.getAllSelectedItems().stream().findFirst().get());
        Assert.assertArrayEquals(new String[] { "bar" },
                event.getAllSelectedItems().toArray(new String[1]));

        grid.getSelectionModel().deselectAll();

        event = eventCapture.getValue();
        assertNotNull(event);
        assertFalse(event.isUserOriginated());
        assertEquals(Optional.empty(), event.getFirstSelected());
        assertEquals(0, event.getAllSelectedItems().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddSelectionListener_noSelectionMode() {
        grid.setSelectionMode(SelectionMode.NONE);

        grid.addSelectionListener(
                event -> Assert.fail("never ever happens (tm)"));
    }

    @Test
    public void sortByColumn_sortOrderIsAscendingOneColumn() {
        Column<String, ?> column = grid.getColumns().get(1);
        grid.sort(column);

        SortOrder<Column<String, ?>> sortOrder = grid.getSortOrder().get(0);
        Assert.assertEquals(column, sortOrder.getSorted());
        Assert.assertEquals(SortDirection.ASCENDING, sortOrder.getDirection());
    }

    @Test
    public void sortByColumnDesc_sortOrderIsDescendingOneColumn() {
        Column<String, ?> column = grid.getColumns().get(1);
        grid.sort(column, SortDirection.DESCENDING);

        SortOrder<Column<String, ?>> sortOrder = grid.getSortOrder().get(0);
        Assert.assertEquals(column, sortOrder.getSorted());
        Assert.assertEquals(SortDirection.DESCENDING, sortOrder.getDirection());
    }

    @Test
    public void setSortOrder() {
        Column<String, ?> column1 = grid.getColumns().get(1);
        Column<String, ?> column2 = grid.getColumns().get(2);
        List<SortOrder<Column<String, ?>>> order = Arrays.asList(
                new SortOrder<>(column2, SortDirection.DESCENDING),
                new SortOrder<>(column1, SortDirection.ASCENDING));
        grid.setSortOrder(order);

        List<SortOrder<Column<String, ?>>> sortOrder = grid.getSortOrder();
        Assert.assertEquals(column2, sortOrder.get(0).getSorted());
        Assert.assertEquals(SortDirection.DESCENDING,
                sortOrder.get(0).getDirection());

        Assert.assertEquals(column1, sortOrder.get(1).getSorted());
        Assert.assertEquals(SortDirection.ASCENDING,
                sortOrder.get(1).getDirection());
    }

    @Test
    public void clearSortOrder() {
        Column<String, ?> column = grid.getColumns().get(1);
        grid.sort(column);

        grid.clearSortOrder();

        assertEquals(0, grid.getSortOrder().size());
    }

    @Test
    public void sortListener_eventIsFired() {
        Column<String, ?> column1 = grid.getColumns().get(1);
        Column<String, ?> column2 = grid.getColumns().get(2);

        List<SortOrder<Column<String, ?>>> list = new ArrayList<>();
        AtomicReference<Boolean> fired = new AtomicReference<>();
        grid.addSortListener(event -> {
            Assert.assertTrue(list.isEmpty());
            fired.set(true);
            list.addAll(event.getSortOrder());
        });
        grid.sort(column1, SortDirection.DESCENDING);

        Assert.assertEquals(column1, list.get(0).getSorted());
        Assert.assertEquals(SortDirection.DESCENDING,
                list.get(0).getDirection());

        List<SortOrder<Column<String, ?>>> order = Arrays.asList(
                new SortOrder<>(column2, SortDirection.DESCENDING),
                new SortOrder<>(column1, SortDirection.ASCENDING));
        list.clear();

        grid.setSortOrder(order);

        Assert.assertEquals(column2, list.get(0).getSorted());
        Assert.assertEquals(SortDirection.DESCENDING,
                list.get(0).getDirection());

        Assert.assertEquals(column1, list.get(1).getSorted());
        Assert.assertEquals(SortDirection.ASCENDING,
                list.get(1).getDirection());

        list.clear();
        fired.set(false);
        grid.clearSortOrder();
        Assert.assertEquals(0, list.size());
        Assert.assertTrue(fired.get());
    }
}
