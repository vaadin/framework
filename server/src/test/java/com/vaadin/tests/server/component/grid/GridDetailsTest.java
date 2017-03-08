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
package com.vaadin.tests.server.component.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;

public class GridDetailsTest {

    private final class DummyLabel extends Label {
        private DummyLabel(String content) {
            super(content);
        }

        @Override
        public String getConnectorId() {
            return "";
        }
    }

    public static class TestGrid extends Grid<String> {

        /**
         * Used to execute data generation
         */
        public void runDataGeneration() {
            super.getDataCommunicator().beforeClientResponse(true);
        }
    }

    private TestGrid grid;
    private List<String> data;

    @Before
    public void setUp() {
        grid = new TestGrid();
        // Setup Grid and generate some details
        data = new ArrayList<>(Arrays.asList("Foo", "Bar"));
        grid.setItems(data);
        grid.setDetailsGenerator(s -> new DummyLabel(s));

        data.forEach(s -> grid.setDetailsVisible(s, true));

        grid.runDataGeneration();
    }

    @Test
    public void testGridComponentIteratorContainsDetailsComponents() {
        Iterator<Component> i = grid.iterator();

        while (i.hasNext()) {
            Component c = i.next();
            if (c instanceof Label) {
                String value = ((Label) c).getValue();
                Assert.assertTrue(
                        "Unexpected label in component iterator with value "
                                + value,
                        data.remove(value));
            } else {
                Assert.fail(
                        "Iterator contained a component that is not a label.");
            }
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGridComponentIteratorNotModifiable() {
        Iterator<Component> iterator = grid.iterator();
        iterator.next();
        // This should fail
        iterator.remove();
    }

    @Test
    public void testGridComponentIteratorIsEmptyAfterHidingDetails() {
        Assert.assertTrue("Component iterator should have components.",
                grid.iterator().hasNext());
        data.forEach(s -> grid.setDetailsVisible(s, false));
        Assert.assertFalse("Component iterator should not have components.",
                grid.iterator().hasNext());
    }
}
