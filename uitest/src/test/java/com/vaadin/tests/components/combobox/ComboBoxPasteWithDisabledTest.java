package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxPasteWithDisabledTest extends MultiBrowserTest {

    @Test
    public void pasteWithDisabled() throws InterruptedException {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();

        cb.click();

        WebElement input = cb.getInputField();
        JavascriptExecutor js = (JavascriptExecutor) getDriver();

        // .sendKeys() doesn't allow sending to a disabled element
        js.executeScript("arguments[0].removeAttribute('disabled')", input);

        input.sendKeys(Keys.chord(Keys.CONTROL, "v"));

        assertFalse(cb.isPopupOpen());
    }

}
