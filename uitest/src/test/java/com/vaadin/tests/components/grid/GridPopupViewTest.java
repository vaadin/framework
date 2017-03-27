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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.PopupViewElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridPopupViewTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        List<DesiredCapabilities> l = getBrowserCapabilities(Browser.IE11,
                Browser.FIREFOX, Browser.CHROME);
        l.add(PHANTOMJS2());
        return l;
    }

    @Test
    public void gridSizeCorrect() {
        openTestURL();
        PopupViewElement pv = $(PopupViewElement.class).first();

        for (int i = 0; i < 3; i++) {
            pv.click();
            GridElement grid = $(GridElement.class).first();
            Dimension rect = grid.getCell(0, 0).getSize();
            Assert.assertEquals(500, rect.width);
            Assert.assertEquals(38, rect.height);
            findElement(By.className("v-ui")).click();
            Assert.assertTrue($(GridElement.class).all().isEmpty());
        }

    }

}
