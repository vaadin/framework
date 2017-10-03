package com.vaadin.v7.tests.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class SpecialCharactersEncodingUITest extends SingleBrowserTest {

    @Test
    public void checkEncoding() {
        openTestURL();
        String textFieldValue = $(TextFieldElement.class).id("textfield")
                .getValue();
        assertEquals(SpecialCharactersEncodingUI.textWithZwnj, textFieldValue);
        LabelElement label = $(LabelElement.class).id("label");
        String labelValue = getHtml(label); // getText() strips some characters
        assertEquals(SpecialCharactersEncodingUI.textWithZwnj, labelValue);

        MenuBarElement menubar = $(MenuBarElement.class).first();
        WebElement menuItem = menubar
                .findElement(By.className("v-menubar-menuitem-caption"));
        assertEquals(SpecialCharactersEncodingUI.textWithZwnj,
                getHtml(menuItem));
    }

    private String getHtml(WebElement element) {
        return element.getAttribute("innerHTML");
    }
}
