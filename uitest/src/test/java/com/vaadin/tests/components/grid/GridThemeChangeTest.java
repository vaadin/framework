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
package com.vaadin.tests.components.grid;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridThemeChangeTest extends MultiBrowserTest {
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Seems like stylesheet onload is not fired on PhantomJS
        // https://github.com/ariya/phantomjs/issues/12332
        return super.getBrowsersExcludingPhantomJS();
    }

    @Test
    public void testThemeChange() {
        openTestURL("debug");

        GridElement grid = $(GridElement.class).first();

        int reindeerHeight = grid.getRow(0).getSize().getHeight();

        grid.getCell(0, 0).click();

        grid = $(GridElement.class).first();
        int valoHeight = grid.getRow(0).getSize().getHeight();

        Assert.assertTrue(
                "Row height should increase when changing from Reindeer to Valo",
                valoHeight > reindeerHeight);
    }
}
