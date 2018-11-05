package com.vaadin.v7.tests.server.component.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.ComponentTest;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.event.SelectionEvent;
import com.vaadin.v7.event.SelectionEvent.SelectionListener;
import com.vaadin.v7.shared.ui.grid.selection.MultiSelectionModelServerRpc;
import com.vaadin.v7.shared.ui.grid.selection.MultiSelectionModelState;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.SelectionModel.HasUserSelectionAllowed;

public class MultiSelectionModelTest {

    private static class MultiSelectionModel
            extends com.vaadin.v7.ui.Grid.MultiSelectionModel {
        @Override
        protected MultiSelectionModelState getState() {
            // Overridden to be accessible from test
            return super.getState();
        }
    }

    private Object itemId1Present = "itemId1Present";
    private Object itemId2Present = "itemId2Present";
    private Object itemId3Present = "itemId3Present";

    private Object itemIdNotPresent = "itemIdNotPresent";
    private Container.Indexed dataSource;
    private MultiSelectionModel model;
    private Grid grid;

    private boolean expectingEvent = false;
    private boolean expectingDeselectEvent;
    private List<Object> select = new ArrayList<Object>();
    private List<Object> deselect = new ArrayList<Object>();

    @Before
    public void setUp() {
        dataSource = createDataSource();
        grid = new Grid(dataSource);
        model = new MultiSelectionModel();
        grid.setSelectionModel(model);
    }

    @After
    public void tearDown() {
        assertFalse("Some expected select event did not happen.",
                expectingEvent);
        assertFalse("Some expected deselect event did not happen.",
                expectingDeselectEvent);
    }

    private IndexedContainer createDataSource() {
        final IndexedContainer container = new IndexedContainer();
        container.addItem(itemId1Present);
        container.addItem(itemId2Present);
        container.addItem(itemId3Present);
        for (int i = 3; i < 10; i++) {
            container.addItem(new Object());
        }

        return container;
    }

    @Test
    public void testSelectAndDeselectRow() throws Throwable {
        try {
            expectSelectEvent(itemId1Present);
            model.select(itemId1Present);
            expectDeselectEvent(itemId1Present);
            model.deselect(itemId1Present);
        } catch (Exception e) {
            throw e.getCause();
        }

        verifyCurrentSelection();
    }

    @Test
    public void testAddSelection() throws Throwable {
        try {
            expectSelectEvent(itemId1Present);
            model.select(itemId1Present);
            expectSelectEvent(itemId2Present);
            model.select(itemId2Present);
        } catch (Exception e) {
            throw e.getCause();
        }

        verifyCurrentSelection(itemId1Present, itemId2Present);
    }

    @Test
    public void testSelectAllWithoutItems() throws Throwable {
        assertFalse(model.getState().allSelected);
        dataSource.removeAllItems();
        assertFalse(model.getState().allSelected);
        model.select();
        assertFalse(model.getState().allSelected);
        model.deselect();
        assertFalse(model.getState().allSelected);
    }

    @Test
    public void testSettingSelection() throws Throwable {
        try {
            expectSelectEvent(itemId2Present, itemId1Present);
            model.setSelected(Arrays
                    .asList(new Object[] { itemId1Present, itemId2Present }));
            verifyCurrentSelection(itemId1Present, itemId2Present);

            expectDeselectEvent(itemId1Present);
            expectSelectEvent(itemId3Present);
            model.setSelected(Arrays
                    .asList(new Object[] { itemId3Present, itemId2Present }));
            verifyCurrentSelection(itemId3Present, itemId2Present);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    private void expectSelectEvent(Object... selectArray) {
        select = Arrays.asList(selectArray);
        addListener();
    }

    private void expectDeselectEvent(Object... deselectArray) {
        deselect = Arrays.asList(deselectArray);
        addListener();
    }

    private void addListener() {
        if (expectingEvent) {
            return;
        }

        expectingEvent = true;
        grid.addSelectionListener(new SelectionListener() {

            @Override
            public void select(SelectionEvent event) {
                assertTrue("Selection did not contain expected items",
                        event.getAdded().containsAll(select));
                assertTrue("Selection contained unexpected items",
                        select.containsAll(event.getAdded()));
                select = new ArrayList<Object>();

                assertTrue("Deselection did not contain expected items",
                        event.getRemoved().containsAll(deselect));
                assertTrue("Deselection contained unexpected items",
                        deselect.containsAll(event.getRemoved()));
                deselect = new ArrayList<Object>();

                grid.removeSelectionListener(this);
                expectingEvent = false;
            }
        });
    }

    private void verifyCurrentSelection(Object... selection) {
        final List<Object> selected = Arrays.asList(selection);
        if (model.getSelectedRows().containsAll(selected)
                && selected.containsAll(model.getSelectedRows())) {
            return;
        }
        fail("Not all items were correctly selected");
    }

    @Test(expected = IllegalStateException.class)
    public void refuseSelectWhenUserSelectionDisallowed() {
        ((HasUserSelectionAllowed) grid.getSelectionModel())
                .setUserSelectionAllowed(false);
        MultiSelectionModelServerRpc serverRpc = ComponentTest.getRpcProxy(
                grid.getSelectionModel(), MultiSelectionModelServerRpc.class);
        serverRpc.select(Collections.singletonList("a"));
    }

    @Test(expected = IllegalStateException.class)
    public void refuseDeselectWhenUserSelectionDisallowed() {
        ((HasUserSelectionAllowed) grid.getSelectionModel())
                .setUserSelectionAllowed(false);
        MultiSelectionModelServerRpc serverRpc = ComponentTest.getRpcProxy(
                grid.getSelectionModel(), MultiSelectionModelServerRpc.class);
        serverRpc.deselect(Collections.singletonList("a"));
    }

    @Test(expected = IllegalStateException.class)
    public void refuseSelectAllWhenUserSelectionDisallowed() {
        ((HasUserSelectionAllowed) grid.getSelectionModel())
                .setUserSelectionAllowed(false);
        MultiSelectionModelServerRpc serverRpc = ComponentTest.getRpcProxy(
                grid.getSelectionModel(), MultiSelectionModelServerRpc.class);
        serverRpc.selectAll();
    }

    @Test(expected = IllegalStateException.class)
    public void refuseDeselectAllWhenUserSelectionDisallowed() {
        ((HasUserSelectionAllowed) grid.getSelectionModel())
                .setUserSelectionAllowed(false);
        MultiSelectionModelServerRpc serverRpc = ComponentTest.getRpcProxy(
                grid.getSelectionModel(), MultiSelectionModelServerRpc.class);
        serverRpc.deselectAll();
    }
}
