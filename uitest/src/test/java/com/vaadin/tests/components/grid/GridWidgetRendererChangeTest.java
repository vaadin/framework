package com.vaadin.tests.components.grid;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridWidgetRendererChangeTest extends SingleBrowserTest {

    @Test
    public void testChangeWidgetRenderer() {
        setDebug(true);
        openTestURL();

        selectMenuPath("Component", "Change first renderer");

        assertNoErrorNotifications();

        selectMenuPath("Component", "Change first renderer");

        assertNoErrorNotifications();

        // First renderer OK

        selectMenuPath("Component", "Change second renderer");

        assertNoErrorNotifications();

        selectMenuPath("Component", "Change second renderer");

        assertNoErrorNotifications();

    }

    @Override
    protected void selectMenu(String menuCaption) {
        // GWT menu does not need to be clicked.
        selectMenu(menuCaption, false);
    }

    @Override
    protected WebElement getMenuElement(String menuCaption) {
        return getDriver()
                .findElement(By.xpath("//td[text() = '" + menuCaption + "']"));
    }

}
