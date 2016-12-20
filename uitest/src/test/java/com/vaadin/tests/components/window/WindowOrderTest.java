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

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for window order position access.
 *
 * @author Vaadin Ltd
 */
public class WindowOrderTest extends MultiBrowserTest {

    @Test
    public void orderGetterTest() {
        openTestURL();

        checkPositionsAfterFirstWindowActivation();

        checkPositionsAfterActivationThirdFirstSecond();

        checkPositionsAfterDetachingThirdWindow();

        checkPositionsAfterNewWindowAttach();
    }

    private void checkPositionsAfterFirstWindowActivation() {
        // Bring the first window to front and check order positions of the
        // windows
        findElement(By.className("bring-to-front-first")).click();
        Assert.assertTrue(
                "The first window has wrong order position after bring first to front",
                hasOrder("window1", 2));
        Assert.assertTrue(
                "The first window position is incorrectly updated via UI listener after bring first to front",
                hasOrderInUi("window1", 2));
        Assert.assertTrue(
                "The second window has wrong order position after bring first to front",
                hasOrder("window2", 0));
        Assert.assertTrue(
                "The second window position is incorrectly updated via UI after bring first to front",
                hasOrderInUi("window2", 0));
        Assert.assertTrue(
                "The third window has wrong order position after bring first to front",
                hasOrder("window3", 1));
        Assert.assertTrue(
                "The third window position is incorrectly updated via UI after bring first to front",
                hasOrderInUi("window3", 1));
        Assert.assertTrue(
                "Last window is not attached and should have '-1' position, but hasn't.",
                lastWindowHasOrder(-1));
    }

    private void checkPositionsAfterActivationThirdFirstSecond() {
        // Bring third, first and second window at once (exactly in this order)
        // to front and check order positions of the
        // windows
        findElement(By.className("bring-to-front-all")).click();

        Assert.assertTrue(
                "The first window has wrong order position after bring all to front",
                hasOrder("window2", 2));
        Assert.assertTrue(
                "The first window position is incorrectly updated via UI after bring all to front",
                hasOrderInUi("window2", 2));
        Assert.assertTrue(
                "The second window has wrong order position after bring all to front",
                hasOrder("window1", 1));
        Assert.assertTrue(
                "The second window position is incorrectly updated via UI after bring all to front",
                hasOrderInUi("window1", 1));
        Assert.assertTrue(
                "The third window has wrong order position after bring all to front",
                hasOrder("window3", 0));
        Assert.assertTrue(
                "The third window position is incorrectly updated via UI after bring all to front",
                hasOrderInUi("window3", 0));
        Assert.assertTrue(
                "Last window is not attached and should have '-1' position, but hasn't.",
                lastWindowHasOrder(-1));
    }

    private void checkPositionsAfterDetachingThirdWindow() {
        // Detach third window and check order positions of the
        // windows
        findElement(By.className("detach-window")).click();

        Assert.assertTrue(
                "The first window has wrong order position after detach last window",
                hasOrder("window2", 1));
        Assert.assertTrue(
                "The first window position is incorrectly updated after detach last window",
                hasOrderInUi("window2", 1));
        Assert.assertTrue(
                "The second window has wrong order position after detach last window",
                hasOrder("window1", 0));
        Assert.assertTrue(
                "The second window position is incorrectly updated after detach last window",
                hasOrderInUi("window1", 0));
        WebElement thirdWindowInfo = findElement(By.className("w3-detached"));
        Assert.assertTrue("The third window has wrong order after detach",
                thirdWindowInfo.getAttribute("class").contains("w3--1"));
        Assert.assertTrue(
                "The third window position is incorrectly updated after detach last window",
                hasOrderInUi("window3", -1));
        Assert.assertTrue(
                "Last window is not attached and should have '-1' position, but hasn't.",
                lastWindowHasOrder(-1));
    }

    private void checkPositionsAfterNewWindowAttach() {
        // Attach new window and check order positions of the
        // windows
        findElement(By.className("add-window")).click();

        Assert.assertTrue(
                "The first window has wrong order position after add new window",
                hasOrder("window2", 1));
        Assert.assertTrue(
                "The first window position is incorrectly updated after add new window",
                hasOrderInUi("window2", 1));
        Assert.assertTrue(
                "The second window has wrong order position after add new window",
                hasOrder("window1", 0));
        Assert.assertTrue(
                "The second window position is incorrectly updated after add new window",
                hasOrderInUi("window1", 0));
        Assert.assertTrue(
                "The last window has wrong order position after add new window",
                hasOrder("window4", 2));
        Assert.assertTrue(
                "The last window position is incorrectly updated after add new window",
                hasOrderInUi("window4", 2));
    }

    private WebElement findElement(String styleName) {
        return findElement(By.className(styleName));
    }

    private boolean hasOrder(String window, int order) {
        WebElement win = findElement(window);
        WebElement content = win.findElement(By.className("v-label"));
        return content.getText().equals(String.valueOf(order)) && content
                .getAttribute("class").contains("event-order" + order);
    }

    private boolean hasOrderInUi(String window, int order) {
        WebElement uiLabel = findElement(By.className("ui-label"));
        return uiLabel.getAttribute("class").contains(window + '-' + order);
    }

    private boolean lastWindowHasOrder(int order) {
        WebElement info = findElement("info-label");
        String clazz = info.getAttribute("class");
        String style = "w4-" + order;
        boolean hasOrder = clazz.contains(style);
        if (!hasOrder) {
            return false;
        }
        clazz = clazz.replace(style, "");
        return !clazz.contains("w4");
    }

}
