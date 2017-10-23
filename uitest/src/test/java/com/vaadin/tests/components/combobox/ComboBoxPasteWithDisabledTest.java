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

        String os = System.getProperty("os.name").toLowerCase();
        String paste;
        if (os.contains("windows")) {
            paste = Keys.chord(Keys.CONTROL, "v");
        } else if (os.contains("linux")) {
            paste = Keys.chord(Keys.CONTROL, Keys.SHIFT, "v");
        } else {
            // mac
            paste = Keys.chord(Keys.COMMAND, "v");
        }

        input.sendKeys(paste);

        assertFalse(cb.isPopupOpen());
    }

}
