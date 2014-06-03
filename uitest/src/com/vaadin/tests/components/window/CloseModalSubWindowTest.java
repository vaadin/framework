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
package com.vaadin.tests.components.window;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class CloseModalSubWindowTest extends MultiBrowserTest {

    @Test
    public void testCloseModalSubWindow() throws Exception {

        openTestURL();

        // assert that there's a button with a 'del-btn0' id
        List<WebElement> buttons = getDriver().findElements(
                By.id(CloseModalSubWindow.DELETE_BUTTON + "0"));
        int deleteButtonCount = buttons.size();
        Assert.assertEquals(1, deleteButtonCount);

        // assert that there's no sub-windows open
        List<WebElement> subWindows = getDriver().findElements(
                By.id(CloseModalSubWindow.SUB_WINDOW));
        Assert.assertEquals(0, subWindows.size());

        // click the first delete button
        getFirstDeteleButton(0).click();

        // assert that there's ONE sub-window open
        subWindows = getDriver().findElements(
                By.id(CloseModalSubWindow.SUB_WINDOW));
        Assert.assertEquals(1, subWindows.size());

        WebElement confirm = getDriver().findElement(
                By.id(CloseModalSubWindow.CONFIRM_BUTTON));

        // click the confirm button in the sub-window
        confirm.click();

        // assert that there's no sub-windows open
        subWindows = getDriver().findElements(
                By.id(CloseModalSubWindow.SUB_WINDOW));
        Assert.assertEquals(0, subWindows.size());

        // assert that there's no button with 'del-btn0' id anymore
        buttons = getDriver().findElements(
                By.id(CloseModalSubWindow.DELETE_BUTTON + "0"));
        Assert.assertEquals(0, buttons.size());
    }

    private WebElement getFirstDeteleButton(int index) {
        WebElement button = getDriver().findElement(
                By.id(CloseModalSubWindow.DELETE_BUTTON + index));
        return button;
    }
}
