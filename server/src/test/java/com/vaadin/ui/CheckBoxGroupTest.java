/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.data.DataSource;
import com.vaadin.shared.data.selection.SelectionModel.Multi;
import com.vaadin.shared.data.selection.SelectionServerRpc;

public class CheckBoxGroupTest {
    private CheckBoxGroup<String> checkBoxGroup;
    private Multi<String> selectionModel;

    @Before
    public void setUp() {
        checkBoxGroup = new CheckBoxGroup<>();
        // Intentional deviation from upcoming selection order
        checkBoxGroup
                .setDataSource(DataSource.create("Third", "Second", "First"));
        selectionModel = checkBoxGroup.getSelectionModel();
    }

    @Test
    public void stableSelectionOrder() {
        selectionModel.select("First");
        selectionModel.select("Second");
        selectionModel.select("Third");

        assertSelectionOrder(selectionModel, "First", "Second", "Third");

        selectionModel.deselect("First");
        assertSelectionOrder(selectionModel, "Second", "Third");

        selectionModel.select("First");
        assertSelectionOrder(selectionModel, "Second", "Third", "First");
    }

    @Test
    public void apiSelectionChange_notUserOriginated() {
        AtomicInteger listenerCount = new AtomicInteger(0);

        checkBoxGroup.addSelectionListener(event -> {
            listenerCount.incrementAndGet();
            Assert.assertFalse(event.isUserOriginated());
        });

        checkBoxGroup.select("First");
        checkBoxGroup.select("Second");

        checkBoxGroup.deselect("Second");
        checkBoxGroup.getSelectionModel().deselectAll();

        Assert.assertEquals(4, listenerCount.get());
    }

    @Test
    public void rpcSelectionChange_userOriginated() {
        AtomicInteger listenerCount = new AtomicInteger(0);

        checkBoxGroup.addSelectionListener(event -> {
            listenerCount.incrementAndGet();
            Assert.assertTrue(event.isUserOriginated());
        });

        SelectionServerRpc rpc = ComponentTest.getRpcProxy(checkBoxGroup,
                SelectionServerRpc.class);

        rpc.select(getItemKey("First"));
        rpc.select(getItemKey("Second"));
        rpc.deselect(getItemKey("Second"));

        Assert.assertEquals(3, listenerCount.get());
    }

    private String getItemKey(String dataObject) {
        return checkBoxGroup.getDataCommunicator().getKeyMapper()
                .key(dataObject);
    }

    private static void assertSelectionOrder(Multi<String> selectionModel,
            String... selectionOrder) {
        Assert.assertEquals(Arrays.asList(selectionOrder),
                new ArrayList<>(selectionModel.getSelectedItems()));
    }
}
