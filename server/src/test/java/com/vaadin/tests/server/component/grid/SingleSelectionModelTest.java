/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.shared.ui.grid.selection.SingleSelectionModelServerRpc;
import com.vaadin.ui.ComponentTest;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SelectionModel.HasUserSelectionAllowed;
import com.vaadin.ui.Grid.SingleSelectionModel;

public class SingleSelectionModelTest {

    private Object itemId1Present = "itemId1Present";
    private Object itemId2Present = "itemId2Present";

    private Object itemIdNotPresent = "itemIdNotPresent";
    private Container.Indexed dataSource;
    private SingleSelectionModel model;
    private Grid grid;

    private boolean expectingEvent = false;

    @Before
    public void setUp() {
        dataSource = createDataSource();
        grid = new Grid(dataSource);
        grid.setSelectionMode(SelectionMode.SINGLE);
        model = (SingleSelectionModel) grid.getSelectionModel();
    }

    @After
    public void tearDown() {
        Assert.assertFalse("Some expected event did not happen.",
                expectingEvent);
    }

    private IndexedContainer createDataSource() {
        final IndexedContainer container = new IndexedContainer();
        container.addItem(itemId1Present);
        container.addItem(itemId2Present);
        for (int i = 2; i < 10; i++) {
            container.addItem(new Object());
        }

        return container;
    }

    @Test
    public void testSelectAndDeselctRow() throws Throwable {
        try {
            expectEvent(itemId1Present, null);
            model.select(itemId1Present);
            expectEvent(null, itemId1Present);
            model.select(null);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void testSelectAndChangeSelectedRow() throws Throwable {
        try {
            expectEvent(itemId1Present, null);
            model.select(itemId1Present);
            expectEvent(itemId2Present, itemId1Present);
            model.select(itemId2Present);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void testRemovingSelectedRowAndThenDeselecting() throws Throwable {
        try {
            expectEvent(itemId2Present, null);
            model.select(itemId2Present);
            dataSource.removeItem(itemId2Present);
            expectEvent(null, itemId2Present);
            model.select(null);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void testSelectAndReSelectRow() throws Throwable {
        try {
            expectEvent(itemId1Present, null);
            model.select(itemId1Present);
            expectEvent(null, null);
            // This is no-op. Nothing should happen.
            model.select(itemId1Present);
        } catch (Exception e) {
            throw e.getCause();
        }
        Assert.assertTrue("Should still wait for event", expectingEvent);
        expectingEvent = false;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectNonExistentRow() {
        model.select(itemIdNotPresent);
    }

    private void expectEvent(final Object selected, final Object deselected) {
        expectingEvent = true;
        grid.addSelectionListener(new SelectionListener() {

            @Override
            public void select(SelectionEvent event) {
                if (selected != null) {
                    Assert.assertTrue("Selection did not contain expected item",
                            event.getAdded().contains(selected));
                } else {
                    Assert.assertTrue("Unexpected selection",
                            event.getAdded().isEmpty());
                }

                if (deselected != null) {
                    Assert.assertTrue(
                            "DeSelection did not contain expected item",
                            event.getRemoved().contains(deselected));
                } else {
                    Assert.assertTrue("Unexpected selection",
                            event.getRemoved().isEmpty());
                }

                grid.removeSelectionListener(this);
                expectingEvent = false;
            }
        });
    }

    @Test(expected = IllegalStateException.class)
    public void refuseSelectionWhenUserSelectionDisallowed() {
        ((HasUserSelectionAllowed) grid.getSelectionModel())
                .setUserSelectionAllowed(false);
        SingleSelectionModelServerRpc serverRpc = ComponentTest.getRpcProxy(
                grid.getSelectionModel(), SingleSelectionModelServerRpc.class);
        serverRpc.select("a");
    }

}
