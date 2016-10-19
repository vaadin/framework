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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.shared.ui.grid.selection.MultiSelectionModelServerRpc;
import com.vaadin.shared.ui.grid.selection.MultiSelectionModelState;
import com.vaadin.ui.ComponentTest;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionModel.HasUserSelectionAllowed;

public class MultiSelectionModelTest {

    private static class MultiSelectionModel
            extends com.vaadin.ui.Grid.MultiSelectionModel {
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
        Assert.assertFalse("Some expected select event did not happen.",
                expectingEvent);
        Assert.assertFalse("Some expected deselect event did not happen.",
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
        Assert.assertFalse(model.getState().allSelected);
        dataSource.removeAllItems();
        Assert.assertFalse(model.getState().allSelected);
        model.select();
        Assert.assertFalse(model.getState().allSelected);
        model.deselect();
        Assert.assertFalse(model.getState().allSelected);
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
                Assert.assertTrue("Selection did not contain expected items",
                        event.getAdded().containsAll(select));
                Assert.assertTrue("Selection contained unexpected items",
                        select.containsAll(event.getAdded()));
                select = new ArrayList<Object>();

                Assert.assertTrue("Deselection did not contain expected items",
                        event.getRemoved().containsAll(deselect));
                Assert.assertTrue("Deselection contained unexpected items",
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
        Assert.fail("Not all items were correctly selected");
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
