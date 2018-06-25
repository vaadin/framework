package com.vaadin.tests.components.window;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class OpenModalWindowAndFocusFieldTest extends MultiBrowserTest {

    @Test
    public void openModalAndFocusField() {
        openTestURL();
        $(ButtonElement.class).id("openFocus").click();
        TextAreaElement textArea = $(TextAreaElement.class).first();

        assertElementsEquals(textArea, getActiveElement());
    }

    @Test
    public void openModal() {
        openTestURL();
        $(ButtonElement.class).id("open").click();
        // WindowElement window = $(WindowElement.class).first();
        WebElement windowFocusElement = findElement(By.xpath(
                "//div[@class='v-window-contents']/div[@class='v-scrollable']"));

        assertElementsEquals(windowFocusElement, getActiveElement());
    }

}
