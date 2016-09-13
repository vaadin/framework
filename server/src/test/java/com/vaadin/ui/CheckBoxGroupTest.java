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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.server.data.DataSource;
import com.vaadin.shared.data.selection.SelectionModel.Multi;

public class CheckBoxGroupTest {
    @Test
    public void stableSelectionOrder() {
        CheckBoxGroup<String> checkBoxGroup = new CheckBoxGroup<>();
        // Intentional deviation from upcoming selection order
        checkBoxGroup
                .setDataSource(DataSource.create("Third", "Second", "First"));
        Multi<String> selectionModel = checkBoxGroup.getSelectionModel();

        selectionModel.select("First");
        selectionModel.select("Second");
        selectionModel.select("Third");

        assertSelectionOrder(selectionModel, "First", "Second", "Third");

        selectionModel.deselect("First");
        assertSelectionOrder(selectionModel, "Second", "Third");

        selectionModel.select("First");
        assertSelectionOrder(selectionModel, "Second", "Third", "First");
    }

    private static void assertSelectionOrder(Multi<String> selectionModel,
            String... selectionOrder) {
        Assert.assertEquals(Arrays.asList(selectionOrder),
                new ArrayList<>(selectionModel.getSelectedItems()));
    }
}
