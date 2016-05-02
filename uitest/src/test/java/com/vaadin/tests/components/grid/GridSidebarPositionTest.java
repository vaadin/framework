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
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridSidebarPositionTest extends MultiBrowserTest {

    @Test
    public void heightRestrictedToBrowserWindow() {
        openTestURL();
        GridElement gridWithVeryManyColumns = $(GridElement.class).id(
                GridSidebarPosition.POPUP_WINDOW_HEIGHT);
        getSidebarOpenButton(gridWithVeryManyColumns).click();
        Dimension popupSize = getSidebarPopup().getSize();
        Dimension browserWindowSize = getDriver().manage().window().getSize();

        Assert.assertTrue(popupSize.getHeight() <= browserWindowSize
                .getHeight());
    }

    @Test
    public void popupNotBelowBrowserWindow() {
        openTestURL();
        GridElement gridAtBottom = $(GridElement.class).id(
                GridSidebarPosition.POPUP_WINDOW_MOVED_UP);
        getSidebarOpenButton(gridAtBottom).click();
        WebElement sidebarPopup = getSidebarPopup();
        Dimension popupSize = sidebarPopup.getSize();
        Point popupLocation = sidebarPopup.getLocation();
        int popupBottom = popupLocation.getY() + popupSize.getHeight();
        Dimension browserWindowSize = getDriver().manage().window().getSize();

        Assert.assertTrue(popupBottom <= browserWindowSize.getHeight());
    }

    @Test
    public void popupAbove() {
        openTestURL();
        GridElement gridPopupAbove = $(GridElement.class).id(
                GridSidebarPosition.POPUP_ABOVE);
        WebElement sidebarOpenButton = getSidebarOpenButton(gridPopupAbove);
        sidebarOpenButton.click();
        WebElement sidebarPopup = getSidebarPopup();
        Dimension popupSize = sidebarPopup.getSize();
        Point popupLocation = sidebarPopup.getLocation();
        int popupBottom = popupLocation.getY() + popupSize.getHeight();
        int sideBarButtonTop;
        if (BrowserUtil.isIE8(getDesiredCapabilities())) {
            // IE8 gets the top coordinate for the button completely wrong for
            // some reason
            sideBarButtonTop = 660;
        } else {
            sideBarButtonTop = sidebarOpenButton.getLocation().getY();
        }
        Assert.assertTrue(popupBottom <= sideBarButtonTop);
    }

    protected WebElement getSidebarOpenButton(GridElement grid) {
        List<WebElement> elements = grid.findElements(By
                .className("v-grid-sidebar-button"));
        return elements.isEmpty() ? null : elements.get(0);
    }

    protected WebElement getSidebarPopup() {
        List<WebElement> elements = findElements(By
                .className("v-grid-sidebar-popup"));
        return elements.isEmpty() ? null : elements.get(0);
    }

}
