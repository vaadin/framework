package com.vaadin.tests.tooltip;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.PopupViewElement;
import com.vaadin.testbench.elements.UIElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.TooltipTest;

public class NestedOverlayTooltipTest extends TooltipTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    private void openDropDownAndTestTooltip(WebElement base, String firstItem,
            String tooltip) throws Exception {
        WebElement menuBar = base.findElement(By.className("v-menubar"));
        menuBar.findElement(By.vaadin("#" + firstItem)).click();
        WebElement popup = findElement(By.className("v-menubar-popup"));
        WebElement item = popup.findElement(By.className("v-menubar-menuitem"));
        checkTooltip(item, tooltip);
    }

    @Test
    public void testMenuItemTooltip() throws Exception {
        UIElement ui = $(UIElement.class).first();
        openDropDownAndTestTooltip(ui, "First item", "Dropdown Item tooltip");
    }

    @Test
    public void testMenuItemTooltipWithinWindow() throws Exception {
        $(ButtonElement.class).first().click();
        WindowElement window = $(WindowElement.class).first();
        openDropDownAndTestTooltip(window, "First item in window",
                "Window dropdown item tooltip");
    }

    @Test
    public void testMenuItemTooltipWithinNestedWindow() throws Exception {
        $(ButtonElement.class).first().click();
        WindowElement window = $(WindowElement.class).first();
        window.$(ButtonElement.class).first().click();
        WindowElement subWindow = $(WindowElement.class).get(1);
        openDropDownAndTestTooltip(subWindow, "First item in inner window",
                "Inner window dropdown item tooltip");
    }

    @Test
    public void testMenuItemTooltipWithinPopupView() throws Exception {
        $(PopupViewElement.class).first().click();
        WebElement popup = findElement(By.className("v-popupview-popup"));
        openDropDownAndTestTooltip(popup, "First item in popupView",
                "PopupView dropdown item tooltip");
    }

    @Test
    public void testMenuItemTooltipWithinNestedPopupView() throws Exception {
        $(PopupViewElement.class).first().click();
        WebElement popup = findElement(By.className("v-popupview-popup"));
        popup.findElement(By.className("v-popupview")).click();
        WebElement innerPopup = findElements(By.className("v-popupview-popup"))
                .get(1);
        openDropDownAndTestTooltip(innerPopup, "First item in inner popupView",
                "Inner popupView dropdown item tooltip");
    }

}
