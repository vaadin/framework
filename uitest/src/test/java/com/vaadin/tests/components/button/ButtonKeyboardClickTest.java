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
package com.vaadin.tests.components.button;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for availability (x,y) coordinates for button activated via keyboard.
 * 
 * @author Vaadin Ltd
 */
public class ButtonKeyboardClickTest extends MultiBrowserTest {

    @Test
    public void testCoordinatesForClickedButtonViaSpace() {
        openTestURL();

        WebElement button = getDriver().findElement(By.className("v-button"));
        button.sendKeys(Keys.SPACE);

        checkCoordinates(button);
    }

    @Test
    public void testCoordinatesForClickedButtonViaEnter() {
        openTestURL();

        WebElement button = getDriver().findElement(By.className("v-button"));
        button.sendKeys(Keys.ENTER);

        checkCoordinates(button);
    }

    private void checkCoordinates(WebElement button) {
        int xRelative = getValue("xRelative");
        Assert.assertTrue(
                "X relative click coordinate is greater than middle of the button",
                button.getSize().getWidth() / 2 >= xRelative - 1);
        Assert.assertTrue(
                "X relative click coordinate is lower than middle of the button",
                button.getSize().getWidth() / 2 <= xRelative + 1);

        int yRelative = getValue("yRelative");
        Assert.assertTrue(
                "Y relative click coordinate is greater than middle of the button",
                button.getSize().getHeight() / 2 >= yRelative - 1);
        Assert.assertTrue(
                "Y relative click coordinate is lower than middle of the button",
                button.getSize().getHeight() / 2 <= yRelative + 1);

        Assert.assertTrue(
                "Client X click cooridnate is lower than X button coordinate",
                getValue("x") > button.getLocation().getX());
        Assert.assertTrue(
                "Client X click cooridnate is greater than right button "
                        + "border coordinate", getValue("x") < button
                        .getLocation().getX() + button.getSize().getWidth());

        Assert.assertTrue(
                "Client Y click cooridnate is lower than Y button coordinate",
                getValue("y") > button.getLocation().getY());
        Assert.assertTrue(
                "Client Y click cooridnate is greater than bottom button "
                        + "border coordinate", getValue("y") < button
                        .getLocation().getY() + button.getSize().getHeight());

        Assert.assertTrue(
                "Client X click cooridnate is greater than X middle button "
                        + "coordinate", button.getLocation().getX()
                        + button.getSize().getWidth() / 2 >= getValue("x") - 1);
        Assert.assertTrue(
                "Client Y click cooridnate is greater than Y middle button coordinate",
                button.getLocation().getY() + button.getSize().getHeight() / 2 >= getValue("y") - 1);

        Assert.assertTrue(
                "Client X click cooridnate is lower than X middle button "
                        + "coordinate", button.getLocation().getX()
                        + button.getSize().getWidth() / 2 <= getValue("x") + 1);
        Assert.assertTrue(
                "Client Y click cooridnate is lower than Y middle button coordinate",
                button.getLocation().getY() + button.getSize().getHeight() / 2 <= getValue("y") + 1);
    }

    private int getValue(String style) {
        return Integer.parseInt(getDriver().findElement(
                By.className("v-label-" + style)).getText());
    }

}
