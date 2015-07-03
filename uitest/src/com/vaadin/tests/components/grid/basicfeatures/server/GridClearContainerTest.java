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
package com.vaadin.tests.components.grid.basicfeatures.server;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that removing and adding rows doesn't cause an infinite loop in the
 * browser.
 * 
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridClearContainerTest extends MultiBrowserTest {

    private final String ERRORNOTE = "Unexpected cell contents.";

    @Test
    public void clearAndReadd() {
        openTestURL();
        ButtonElement button = $(ButtonElement.class).caption(
                "Clear and re-add").first();
        GridElement grid = $(GridElement.class).first();
        Assert.assertEquals(ERRORNOTE, "default", grid.getCell(0, 0).getText());
        Assert.assertEquals(ERRORNOTE, "default", grid.getCell(1, 0).getText());
        button.click();
        Assert.assertEquals(ERRORNOTE, "Updated value 1", grid.getCell(0, 0)
                .getText());
        Assert.assertEquals(ERRORNOTE, "Updated value 2", grid.getCell(1, 0)
                .getText());
    }
}
