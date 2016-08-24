package com.vaadin.tests.core;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.SingleBrowserTest;
import com.vaadin.tests.tb3.SingleBrowserTestPhantomJS2;

public class SpecialCharactersEncodingUITest extends SingleBrowserTest {

    @Test
    public void checkEncoding() {
        openTestURL();
        String textFieldValue = $(TextFieldElement.class).id("textfield")
                .getValue();
        Assert.assertEquals(SpecialCharactersEncodingUI.textWithZwnj,
                textFieldValue);
        LabelElement label = $(LabelElement.class).id("label");
        String labelValue = getHtml(label); // getText() strips some characters
        Assert.assertEquals(SpecialCharactersEncodingUI.textWithZwnj,
                labelValue);

        MenuBarElement menubar = $(MenuBarElement.class).first();
        WebElement menuItem = menubar
                .findElement(By.className("v-menubar-menuitem-caption"));
        Assert.assertEquals(SpecialCharactersEncodingUI.textWithZwnj,
                getHtml(menuItem));
    }

    private String getHtml(WebElement element) {
        return element.getAttribute("innerHTML");
    }
}
