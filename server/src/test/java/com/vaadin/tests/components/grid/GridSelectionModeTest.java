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
package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;
import com.vaadin.ui.components.grid.NoSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModelImpl;

public class GridSelectionModeTest {

    private Grid<String> grid;

    @Before
    public void setup() {
        grid = new Grid<>();
        grid.setItems("foo", "bar", "baz");
    }

    @Test
    public void testSelectionModes() {
        Assert.assertEquals(SingleSelectionModelImpl.class,
                grid.getSelectionModel().getClass());

        Assert.assertEquals(MultiSelectionModelImpl.class,
                grid.setSelectionMode(SelectionMode.MULTI).getClass());
        Assert.assertEquals(MultiSelectionModelImpl.class,
                grid.getSelectionModel().getClass());

        Assert.assertEquals(NoSelectionModel.class,
                grid.setSelectionMode(SelectionMode.NONE).getClass());
        Assert.assertEquals(NoSelectionModel.class,
                grid.getSelectionModel().getClass());

        Assert.assertEquals(SingleSelectionModelImpl.class,
                grid.setSelectionMode(SelectionMode.SINGLE).getClass());
        Assert.assertEquals(SingleSelectionModelImpl.class,
                grid.getSelectionModel().getClass());
    }

    @Test(expected = NullPointerException.class)
    public void testNullSelectionMode() {
        grid.setSelectionMode(null);
    }

}
