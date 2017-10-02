package com.vaadin.v7.tests.components.window;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UndefinedHeightSubWindowAndContentTest extends MultiBrowserTest {

    @Test
    public void testUndefinedHeight() {
        openTestURL();

        TextFieldElement textField = $(TextFieldElement.class).first();

        textField.click();
        textField.sendKeys("invalid", Keys.ENTER);

        WindowElement window = $(WindowElement.class).first();
        int height = window.getSize().getHeight();
        assertTrue("Window height with validation failure",
                161 <= height && height <= 164);

        textField.setValue("valid");
        textField.sendKeys(Keys.ENTER);
        height = window.getSize().getHeight();
        assertTrue("Window height with validation success",
                136 <= height && height <= 139);
    }

}
