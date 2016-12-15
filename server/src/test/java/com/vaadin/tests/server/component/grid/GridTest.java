package com.vaadin.tests.server.component.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Optional;

import org.easymock.Capture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.ValueProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridTest {

    private Grid<String> grid;

    @Before
    public void setUp() {
        grid = new Grid<>();
        grid.addColumn("foo", ValueProvider.identity());
        grid.addColumn(String::length, new NumberRenderer());
        grid.addColumn("randomColumnId", ValueProvider.identity());
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

    @Test
    public void testGridColumnGeneratedIdentifier() {
        assertEquals("Unexpected caption on a generated Column",
                "Generated Column0",
                grid.getColumn("generatedColumn0").getCaption());
    }

    @Test
    public void testGridColumnCaptionFromIdentifier() {
        assertEquals("Unexpected caption on a generated Column",
                "Random Column Id",
                grid.getColumn("randomColumnId").getCaption());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGridMultipleColumnsWithSameIdentifier() {
        grid.addColumn("foo", t -> t);
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
}
