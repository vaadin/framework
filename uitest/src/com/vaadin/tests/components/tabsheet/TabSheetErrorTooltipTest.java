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
package com.vaadin.tests.components.tabsheet;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class TabSheetErrorTooltipTest extends MultiBrowserTest {

    @Test
    public void checkTooltips() throws IOException {
        openTestURL();

        assertTabHasNoTooltipNorError(0);

        assertTabHasTooltipAndError(1, "", "Error!");

        assertTabHasTooltipAndError(2, "This is a tab", "");

        assertTabHasTooltipAndError(3,
                "This tab has both an error and a description", "Error!");
    }

    private void assertTabHasTooltipAndError(int index, String tooltip,
            String errorMessage) {
        showTooltip(index);
        assertTooltip(tooltip);
        assertErrorMessage(errorMessage);
    }

    private void assertTabHasNoTooltipNorError(int index) {
        showTooltip(index);
        WebElement tooltip = getCurrentTooltip();

        assertThat(tooltip.getText(), is(""));

        WebElement errorMessage = getCurrentErrorMessage();
        assertThat(errorMessage.isDisplayed(), is(false));

    }

    private void showTooltip(int index) {
        Coordinates elementCoordinates = ((Locatable) getTab(index))
                .getCoordinates();
        Mouse mouse = ((HasInputDevices) getDriver()).getMouse();
        mouse.mouseMove(elementCoordinates);
    }

    private WebElement getTab(int index) {
        return vaadinElement("/VTabsheet[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild["
                + index + "]/domChild[0]");
    }

    private WebElement getCurrentTooltip() {
        return getDriver().findElement(
                By.xpath("//div[@class='v-tooltip-text']"));
    }

    private WebElement getCurrentErrorMessage() {
        return getDriver().findElement(
                By.xpath("//div[@class='v-errormessage']"));
    }

    private void assertTooltip(String tooltip) {
        Assert.assertEquals(tooltip, getCurrentTooltip().getText());
    }

    private void assertErrorMessage(String message) {
        Assert.assertEquals(message, getCurrentErrorMessage().getText());
    }
}
