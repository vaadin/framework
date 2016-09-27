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
import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.vaadin.server.data.DataSource;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.selection.MultiSelectServerRpc;
import com.vaadin.shared.data.selection.SelectionModel.Multi;

@RunWith(Parameterized.class)
public class AbstractMultiSelectTest {

    @Parameters(name = "{0}")
    public static Iterable<AbstractMultiSelect<String>> multiSelects() {
        return Arrays.asList(new CheckBoxGroup<>(), new TwinColSelect<>(),
                new ListSelect<>());
    }

    @Parameter
    public AbstractMultiSelect<String> selectToTest;

    private Multi<String> selectionModel;
    private MultiSelectServerRpc rpc;

    private Registration registration;

    @Before
    public void setUp() {
        selectToTest.getSelectionModel().deselectAll();
        // Intentional deviation from upcoming selection order
        selectToTest.setDataSource(
                DataSource.create("3", "2", "1", "5", "8", "7", "4", "6"));
        selectionModel = selectToTest.getSelectionModel();
        rpc = ComponentTest.getRpcProxy(selectToTest,
                MultiSelectServerRpc.class);
    }

    @After
    public void tearDown() {
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }

    @Test
    public void stableSelectionOrder() {
        selectionModel.select("1");
        selectionModel.select("2");
        selectionModel.select("3");

        assertSelectionOrder(selectionModel, "1", "2", "3");

        selectionModel.deselect("1");
        assertSelectionOrder(selectionModel, "2", "3");

        selectionModel.select("1");
        assertSelectionOrder(selectionModel, "2", "3", "1");

        selectionModel.selectItems("7", "8", "4");
        assertSelectionOrder(selectionModel, "2", "3", "1", "7", "8", "4");

        selectionModel.deselectItems("2", "1", "4", "5");
        assertSelectionOrder(selectionModel, "3", "7", "8");

        selectionModel.updateSelection(
                new LinkedHashSet<>(Arrays.asList("5", "2")),
                new LinkedHashSet<>(Arrays.asList("3", "8")));
        assertSelectionOrder(selectionModel, "7", "5", "2");
    }

    @Test
    public void apiSelectionChange_notUserOriginated() {
        AtomicInteger listenerCount = new AtomicInteger(0);
        listenerCount.set(0);

        registration = selectToTest.addSelectionListener(event -> {
            listenerCount.incrementAndGet();
            Assert.assertFalse(event.isUserOriginated());
        });

        selectToTest.select("1");
        selectToTest.select("2");

        selectToTest.deselect("2");
        selectToTest.getSelectionModel().deselectAll();

        selectToTest.getSelectionModel().selectItems("2", "3", "4");
        selectToTest.getSelectionModel().deselectItems("1", "4");

        Assert.assertEquals(6, listenerCount.get());

        // select partly selected
        selectToTest.getSelectionModel().selectItems("2", "3", "4");
        Assert.assertEquals(7, listenerCount.get());

        // select completely selected
        selectToTest.getSelectionModel().selectItems("2", "3", "4");
        Assert.assertEquals(7, listenerCount.get());

        // deselect partly not selected
        selectToTest.getSelectionModel().selectItems("1", "4");
        Assert.assertEquals(8, listenerCount.get());

        // deselect completely not selected
        selectToTest.getSelectionModel().selectItems("1", "4");
        Assert.assertEquals(8, listenerCount.get());
    }

    @Test
    public void rpcSelectionChange_userOriginated() {
        AtomicInteger listenerCount = new AtomicInteger(0);

        registration = selectToTest.addSelectionListener(event -> {
            listenerCount.incrementAndGet();
            Assert.assertTrue(event.isUserOriginated());
        });

        rpcSelect("1");
        assertSelectionOrder(selectionModel, "1");

        rpcSelect("2");
        assertSelectionOrder(selectionModel, "1", "2");
        rpcDeselect("2");
        assertSelectionOrder(selectionModel, "1");
        rpcSelect("3", "6");
        assertSelectionOrder(selectionModel, "1", "3", "6");
        rpcDeselect("1", "3");
        assertSelectionOrder(selectionModel, "6");

        Assert.assertEquals(5, listenerCount.get());

        // select partly selected
        rpcSelect("2", "3", "4");
        Assert.assertEquals(6, listenerCount.get());
        assertSelectionOrder(selectionModel, "6", "2", "3", "4");

        // select completely selected
        rpcSelect("2", "3", "4");
        Assert.assertEquals(6, listenerCount.get());
        assertSelectionOrder(selectionModel, "6", "2", "3", "4");

        // deselect partly not selected
        rpcDeselect("1", "4");
        Assert.assertEquals(7, listenerCount.get());
        assertSelectionOrder(selectionModel, "6", "2", "3");

        // deselect completely not selected
        rpcDeselect("1", "4");
        Assert.assertEquals(7, listenerCount.get());
        assertSelectionOrder(selectionModel, "6", "2", "3");

        // select completely selected and deselect completely not selected
        rpcUpdateSelection(new String[] { "3" }, new String[] { "1", "4" });
        Assert.assertEquals(7, listenerCount.get());
        assertSelectionOrder(selectionModel, "6", "2", "3");

        // select partly selected and deselect completely not selected
        rpcUpdateSelection(new String[] { "4", "2" },
                new String[] { "1", "8" });
        Assert.assertEquals(8, listenerCount.get());
        assertSelectionOrder(selectionModel, "6", "2", "3", "4");

        // select completely selected and deselect partly not selected
        rpcUpdateSelection(new String[] { "4", "3" },
                new String[] { "1", "2" });
        Assert.assertEquals(9, listenerCount.get());
        assertSelectionOrder(selectionModel, "6", "3", "4");

        // duplicate case - ignored
        rpcUpdateSelection(new String[] { "2" }, new String[] { "2" });
        Assert.assertEquals(9, listenerCount.get());
        assertSelectionOrder(selectionModel, "6", "3", "4");

        // duplicate case - duplicate removed
        rpcUpdateSelection(new String[] { "2" }, new String[] { "2", "3" });
        Assert.assertEquals(10, listenerCount.get());
        assertSelectionOrder(selectionModel, "6", "4");

        // duplicate case - duplicate removed
        rpcUpdateSelection(new String[] { "6", "8" }, new String[] { "6" });
        Assert.assertEquals(11, listenerCount.get());
        assertSelectionOrder(selectionModel, "6", "4", "8");
    }

    private void rpcSelect(String... keysToSelect) {
        rpcUpdateSelection(keysToSelect, new String[] {});
    }

    private void rpcDeselect(String... keysToDeselect) {
        rpcUpdateSelection(new String[] {}, keysToDeselect);
    }

    private void rpcUpdateSelection(String[] added, String[] removed) {
        rpc.updateSelection(
                new LinkedHashSet<>(Stream.of(added).map(this::getItemKey)
                        .collect(Collectors.toList())),
                new LinkedHashSet<>(Stream.of(removed).map(this::getItemKey)
                        .collect(Collectors.toList())));
    }

    private String getItemKey(String dataObject) {
        return selectToTest.getDataCommunicator().getKeyMapper()
                .key(dataObject);
    }

    private static void assertSelectionOrder(Multi<String> selectionModel,
            String... selectionOrder) {
        Assert.assertEquals(Arrays.asList(selectionOrder),
                new ArrayList<>(selectionModel.getSelectedItems()));
    }
}
