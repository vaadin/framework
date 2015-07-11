package com.vaadin.tests.components.window;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.WindowElement;

public class UndefinedHeightSubWindowAndContentTest extends MultiBrowserTest {

    @Test
    public void testUndefinedHeight() {
        openTestURL();

        TextFieldElement textField = $(TextFieldElement.class).first();

        textField.click();
        textField.sendKeys("invalid", Keys.ENTER);

        WindowElement window = $(WindowElement.class).first();
        int height = window.getSize().getHeight();
        Assert.assertTrue("Window height with validation failure",
                161 <= height && height <= 164);

        textField.setValue("valid");
        textField.sendKeys(Keys.ENTER);
        height = window.getSize().getHeight();
        Assert.assertTrue("Window height with validation success",
                136 <= height && height <= 139);
    }

}
