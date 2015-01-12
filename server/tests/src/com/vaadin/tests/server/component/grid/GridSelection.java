/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.server.component.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SelectionModel;

public class GridSelection {

    private static class MockSelectionChangeListener implements
            SelectionListener {
        private SelectionEvent event;

        @Override
        public void select(final SelectionEvent event) {
            this.event = event;
        }

        public Collection<?> getAdded() {
            return event.getAdded();
        }

        public Collection<?> getRemoved() {
            return event.getRemoved();
        }

        public void clearEvent() {
            /*
             * This method is not strictly needed as the event will simply be
             * overridden, but it's good practice, and makes the code more
             * obvious.
             */
            event = null;
        }

        public boolean eventHasHappened() {
            return event != null;
        }
    }

    private Grid grid;
    private MockSelectionChangeListener mockListener;

    private final Object itemId1Present = "itemId1Present";
    private final Object itemId2Present = "itemId2Present";

    private final Object itemId1NotPresent = "itemId1NotPresent";
    private final Object itemId2NotPresent = "itemId2NotPresent";

    @Before
    public void setup() {
        final IndexedContainer container = new IndexedContainer();
        container.addItem(itemId1Present);
        container.addItem(itemId2Present);
        for (int i = 2; i < 10; i++) {
            container.addItem(new Object());
        }

        assertEquals("init size", 10, container.size());
        assertTrue("itemId1Present", container.containsId(itemId1Present));
        assertTrue("itemId2Present", container.containsId(itemId2Present));
        assertFalse("itemId1NotPresent",
                container.containsId(itemId1NotPresent));
        assertFalse("itemId2NotPresent",
                container.containsId(itemId2NotPresent));

        grid = new Grid(container);

        mockListener = new MockSelectionChangeListener();
        grid.addSelectionListener(mockListener);

        assertFalse("eventHasHappened", mockListener.eventHasHappened());
    }

    @Test
    public void defaultSelectionModeIsSingle() {
        assertTrue(grid.getSelectionModel() instanceof SelectionModel.Single);
    }

    @Test(expected = IllegalStateException.class)
    public void getSelectedRowThrowsExceptionMulti() {
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.getSelectedRow();
    }

    @Test(expected = IllegalStateException.class)
    public void getSelectedRowThrowsExceptionNone() {
        grid.setSelectionMode(SelectionMode.NONE);
        grid.getSelectedRow();
    }

    @Test(expected = IllegalStateException.class)
    public void selectThrowsExceptionNone() {
        grid.setSelectionMode(SelectionMode.NONE);
        grid.select(itemId1Present);
    }

    @Test(expected = IllegalStateException.class)
    public void deselectRowThrowsExceptionNone() {
        grid.setSelectionMode(SelectionMode.NONE);
        grid.deselect(itemId1Present);
    }

    @Test
    public void selectionModeMapsToMulti() {
        assertTrue(grid.setSelectionMode(SelectionMode.MULTI) instanceof SelectionModel.Multi);
    }

    @Test
    public void selectionModeMapsToSingle() {
        assertTrue(grid.setSelectionMode(SelectionMode.SINGLE) instanceof SelectionModel.Single);
    }

    @Test
    public void selectionModeMapsToNone() {
        assertTrue(grid.setSelectionMode(SelectionMode.NONE) instanceof SelectionModel.None);
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectionModeNullThrowsException() {
        grid.setSelectionMode(null);
    }

    @Test
    public void noSelectModel_isSelected() {
        grid.setSelectionMode(SelectionMode.NONE);
        assertFalse("itemId1Present", grid.isSelected(itemId1Present));
        assertFalse("itemId1NotPresent", grid.isSelected(itemId1NotPresent));
    }

    @Test(expected = IllegalStateException.class)
    public void noSelectModel_getSelectedRow() {
        grid.setSelectionMode(SelectionMode.NONE);
        grid.getSelectedRow();
    }

    @Test
    public void noSelectModel_getSelectedRows() {
        grid.setSelectionMode(SelectionMode.NONE);
        assertTrue(grid.getSelectedRows().isEmpty());
    }

    @Test
    public void selectionCallsListenerMulti() {
        grid.setSelectionMode(SelectionMode.MULTI);
        selectionCallsListener();
    }

    @Test
    public void selectionCallsListenerSingle() {
        grid.setSelectionMode(SelectionMode.SINGLE);
        selectionCallsListener();
    }

    private void selectionCallsListener() {
        grid.select(itemId1Present);
        assertEquals("added size", 1, mockListener.getAdded().size());
        assertEquals("added item", itemId1Present, mockListener.getAdded()
                .iterator().next());
        assertEquals("removed size", 0, mockListener.getRemoved().size());
    }

    @Test
    public void deselectionCallsListenerMulti() {
        grid.setSelectionMode(SelectionMode.MULTI);
        deselectionCallsListener();
    }

    @Test
    public void deselectionCallsListenerSingle() {
        grid.setSelectionMode(SelectionMode.SINGLE);
        deselectionCallsListener();
    }

    private void deselectionCallsListener() {
        grid.select(itemId1Present);
        mockListener.clearEvent();

        grid.deselect(itemId1Present);
        assertEquals("removed size", 1, mockListener.getRemoved().size());
        assertEquals("removed item", itemId1Present, mockListener.getRemoved()
                .iterator().next());
        assertEquals("removed size", 0, mockListener.getAdded().size());
    }

    @Test
    public void deselectPresentButNotSelectedItemIdShouldntFireListenerMulti() {
        grid.setSelectionMode(SelectionMode.MULTI);
        deselectPresentButNotSelectedItemIdShouldntFireListener();
    }

    @Test
    public void deselectPresentButNotSelectedItemIdShouldntFireListenerSingle() {
        grid.setSelectionMode(SelectionMode.SINGLE);
        deselectPresentButNotSelectedItemIdShouldntFireListener();
    }

    private void deselectPresentButNotSelectedItemIdShouldntFireListener() {
        grid.deselect(itemId1Present);
        assertFalse(mockListener.eventHasHappened());
    }

    @Test
    public void deselectNotPresentItemIdShouldNotThrowExceptionMulti() {
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.deselect(itemId1NotPresent);
    }

    @Test
    public void deselectNotPresentItemIdShouldNotThrowExceptionSingle() {
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.deselect(itemId1NotPresent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectNotPresentItemIdShouldThrowExceptionMulti() {
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.select(itemId1NotPresent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectNotPresentItemIdShouldThrowExceptionSingle() {
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.select(itemId1NotPresent);
    }

    @Test
    public void selectAllMulti() {
        grid.setSelectionMode(SelectionMode.MULTI);
        final SelectionModel.Multi select = (SelectionModel.Multi) grid
                .getSelectionModel();
        select.selectAll();
        assertEquals("added size", 10, mockListener.getAdded().size());
        assertEquals("removed size", 0, mockListener.getRemoved().size());
        assertTrue("itemId1Present",
                mockListener.getAdded().contains(itemId1Present));
        assertTrue("itemId2Present",
                mockListener.getAdded().contains(itemId2Present));
    }

    @Test
    public void deselectAllMulti() {
        grid.setSelectionMode(SelectionMode.MULTI);
        final SelectionModel.Multi select = (SelectionModel.Multi) grid
                .getSelectionModel();
        select.selectAll();
        mockListener.clearEvent();

        select.deselectAll();
        assertEquals("removed size", 10, mockListener.getRemoved().size());
        assertEquals("added size", 0, mockListener.getAdded().size());
        assertTrue("itemId1Present",
                mockListener.getRemoved().contains(itemId1Present));
        assertTrue("itemId2Present",
                mockListener.getRemoved().contains(itemId2Present));
        assertTrue("selectedRows is empty", grid.getSelectedRows().isEmpty());
    }

    @Test
    public void reselectionDeselectsPreviousSingle() {
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.select(itemId1Present);
        mockListener.clearEvent();

        grid.select(itemId2Present);
        assertEquals("added size", 1, mockListener.getAdded().size());
        assertEquals("removed size", 1, mockListener.getRemoved().size());
        assertEquals("added item", itemId2Present, mockListener.getAdded()
                .iterator().next());
        assertEquals("removed item", itemId1Present, mockListener.getRemoved()
                .iterator().next());
        assertEquals("selectedRows is correct", itemId2Present,
                grid.getSelectedRow());
    }
}
