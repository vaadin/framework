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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.provider.bov.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.GridSelectionModel;

public class GridNoSelectionModelTest {

    public static final Person PERSON_C = new Person("c", 3);
    public static final Person PERSON_B = new Person("b", 2);
    public static final Person PERSON_A = new Person("a", 1);

    private Grid<Person> grid;
    private GridSelectionModel<Person> model;

    @Before
    public void setUp() {
        grid = new Grid<>();
        grid.setItems(PERSON_A, PERSON_B, PERSON_C);

        model = grid.setSelectionMode(SelectionMode.NONE);
    }

    @Test
    public void select() {
        model.select(PERSON_A);

        Assert.assertFalse(model.isSelected(PERSON_A));
        Assert.assertEquals(0, model.getSelectedItems().size());
        Assert.assertEquals(Optional.empty(), model.getFirstSelectedItem());

        model.select(PERSON_B);

        Assert.assertFalse(model.isSelected(PERSON_B));
        Assert.assertEquals(0, model.getSelectedItems().size());
        Assert.assertEquals(Optional.empty(), model.getFirstSelectedItem());
    }

    @Test
    public void changingToSingleSelectionModel() {
        grid.setSelectionMode(SelectionMode.SINGLE);

        grid.getSelectionModel().select(PERSON_B);
        Assert.assertEquals(PERSON_B,
                grid.getSelectionModel().getFirstSelectedItem().get());
    }

    @Test
    public void changingToMultiSelectionModel() {
        grid.setSelectionMode(SelectionMode.MULTI);

        grid.getSelectionModel().select(PERSON_B);
        Assert.assertEquals(new LinkedHashSet<>(Arrays.asList(PERSON_B)),
                grid.getSelectionModel().getSelectedItems());
    }

}
