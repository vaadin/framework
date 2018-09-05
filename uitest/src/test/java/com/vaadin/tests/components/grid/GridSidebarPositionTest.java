package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridSidebarPositionTest extends MultiBrowserTest {

    @Test
    public void heightRestrictedToBrowserWindow() {
        openTestURL();
        GridElement gridWithVeryManyColumns = $(GridElement.class)
                .id(GridSidebarPosition.POPUP_WINDOW_HEIGHT);
        getSidebarOpenButton(gridWithVeryManyColumns).click();
        Dimension popupSize = getSidebarPopup().getSize();
        Dimension browserWindowSize = getDriver().manage().window().getSize();

        assertTrue(popupSize.getHeight() <= browserWindowSize.getHeight());
    }

    @Test
    public void popupNotBelowBrowserWindow() {
        openTestURL();
        GridElement gridAtBottom = $(GridElement.class)
                .id(GridSidebarPosition.POPUP_WINDOW_MOVED_UP);
        getSidebarOpenButton(gridAtBottom).click();
        WebElement sidebarPopup = getSidebarPopup();
        Dimension popupSize = sidebarPopup.getSize();
        Point popupLocation = sidebarPopup.getLocation();
        int popupBottom = popupLocation.getY() + popupSize.getHeight();
        Dimension browserWindowSize = getDriver().manage().window().getSize();

        assertTrue(popupBottom <= browserWindowSize.getHeight());
    }

    @Test
    public void popupAbove() {
        openTestURL();
        GridElement gridPopupAbove = $(GridElement.class)
                .id(GridSidebarPosition.POPUP_ABOVE);
        WebElement sidebarOpenButton = getSidebarOpenButton(gridPopupAbove);
        sidebarOpenButton.click();
        WebElement sidebarPopup = getSidebarPopup();
        Dimension popupSize = sidebarPopup.getSize();
        Point popupLocation = sidebarPopup.getLocation();
        int popupBottom = popupLocation.getY() + popupSize.getHeight();
        int sideBarButtonTop = sidebarOpenButton.getLocation().getY();
        assertTrue(popupBottom <= sideBarButtonTop);
    }

    protected WebElement getSidebarOpenButton(GridElement grid) {
        List<WebElement> elements = grid
                .findElements(By.className("v-grid-sidebar-button"));
        return elements.isEmpty() ? null : elements.get(0);
    }

    protected WebElement getSidebarPopup() {
        List<WebElement> elements = findElements(
                By.className("v-grid-sidebar-popup"));
        return elements.isEmpty() ? null : elements.get(0);
    }

}
