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
package com.vaadin.tests.elements.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridDetailsTest extends MultiBrowserTest {

    private GridElement gridElement;

    @Override
    protected Class<?> getUIClass() {
        return GridUI.class;
    }

    @Before
    public void init() {
        openTestURL();
        gridElement = $(GridElement.class).first();
    }

    @Test
    public void gridDetails_gridDetailsOpen_elementReturned() {
        gridElement.getCell(0, 0).doubleClick();

        final TestBenchElement details = gridElement.getDetails(0);
        assertEquals("Foo = foo 0 Bar = bar 0",
                details.$(LabelElement.class).first().getText());
    }

    @Test(expected = NoSuchElementException.class)
    public void gridDetails_gridDetailsClosed_exceptionThrown() {
        gridElement.getDetails(0);
    }
}
