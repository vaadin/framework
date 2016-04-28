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
package com.vaadin.tests.components.gridlayout;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridLayoutFocusOrderAfterShowChildTest extends MultiBrowserTest {

    @Test
    public void showComponentBreaksFocusOrderFirst()
            throws IOException, Exception {
        openTestURL();

        GridLayoutElement grid = $(GridLayoutElement.class).id("grid");

        $(ButtonElement.class).first().click();

        Assert.assertEquals("First",
                grid.$(LabelElement.class).first().getText());
        grid.$(TextFieldElement.class).first().focus();

        grid.$(TextFieldElement.class).first().sendKeys(Keys.TAB);

        Assert.assertEquals("t2",
                driver.switchTo().activeElement().getAttribute("id"));
    }

    @Test
    public void showComponentBreaksFocusOrderMiddle()
            throws IOException, Exception {
        openTestURL();

        GridLayoutElement grid = $(GridLayoutElement.class).id("grid");

        $(ButtonElement.class).get(1).click();

        Assert.assertEquals("Third",
                grid.$(LabelElement.class).get(1).getText());
        grid.$(TextFieldElement.class).first().focus();

        grid.$(TextFieldElement.class).first().sendKeys(Keys.TAB);

        Assert.assertEquals("t3",
                driver.switchTo().activeElement().getAttribute("id"));
    }

    @Test
    public void showComponentBreaksFocusOrderLast()
            throws IOException, Exception {
        openTestURL();

        GridLayoutElement grid = $(GridLayoutElement.class).id("grid");

        $(ButtonElement.class).get(2).click();

        Assert.assertEquals("Fifth",
                grid.$(LabelElement.class).get(2).getText());
        grid.$(TextFieldElement.class).get(1).focus();

        grid.$(TextFieldElement.class).get(1).sendKeys(Keys.TAB);

        Assert.assertEquals("t5",
                driver.switchTo().activeElement().getAttribute("id"));
    }
}