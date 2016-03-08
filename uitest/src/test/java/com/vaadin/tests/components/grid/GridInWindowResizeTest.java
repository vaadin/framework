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

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridInWindowResizeTest extends MultiBrowserTest {
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Must test on a browser with animations
        return Collections.singletonList(Browser.CHROME
                .getDesiredCapabilities());
    }

    @Test
    public void resizeWindow() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        int col1WidthBefore = grid.getCell(0, 0).getSize().getWidth();
        $(ButtonElement.class).caption("resize").first().click();
        int col1WidthAfter = grid.getCell(0, 0).getSize().getWidth();

        Assert.assertTrue(col1WidthAfter < col1WidthBefore);
    }
}
