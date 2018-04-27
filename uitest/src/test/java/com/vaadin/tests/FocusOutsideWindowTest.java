package com.vaadin.tests;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class FocusOutsideWindowTest extends MultiBrowserTest {

    @Test
    public void verifyTextFieldFocused() throws Exception {
        openTestURL();
        WebElement openW = findElement(By.id("buttonOp"));
        WebElement focusBut = findElement(By.id("focusBut"));
        // Closing window should focus TexField
        openW.click();
        TextFieldElement textField = $(TextFieldElement.class).first();
        WindowElement window = $(WindowElement.class).first();
        window.close();
        assertEquals(textField.getWrappedElement(), getFocusedElement());
        // Closing window should focus button back(default behaviour)
        focusBut.click();
        openW.click();
        $(WindowElement.class).first().close();
        assertEquals(openW, getFocusedElement());
    }
}
