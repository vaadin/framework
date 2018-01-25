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

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vaadin Ltd
 */
public class GridAssistiveDeviceOnlyTextTest extends SingleBrowserTest {

    @Test
    public void checkAssistiveDeviceOnlyText() {
        openTestURL();

        GridElement.GridCellElement selectAllCell = $(GridElement.class).id("first")
                .getHeaderCell(0, 0);

        Assert.assertTrue("The select all label should be empty.",
                selectAllCell.findElement(By.cssSelector("label")).getText().isEmpty());

        selectAllCell = $(GridElement.class).id("second").getHeaderCell(0, 0);

        Assert.assertEquals("The select all label should contain the given string.",
                "Selects all rows of the table.",
                selectAllCell.findElement(By.cssSelector("label")).getText());
    }
}
