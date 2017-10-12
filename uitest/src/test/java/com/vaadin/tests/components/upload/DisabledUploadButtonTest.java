package com.vaadin.tests.components.upload;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.UploadElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DisabledUploadButtonTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    private String getUploadButtonClass() {
        WebElement uploadButton = getUploadButton();

        return uploadButton.getAttribute("class");
    }

    private void clickButton(String caption) {
        $(ButtonElement.class).caption(caption).first().click();
    }

    private WebElement getUploadButton() {
        UploadElement upload = $(UploadElement.class).first();
        return upload.findElement(By.className("v-button"));
    }

    @Test
    public void buttonIsDisabled() {
        assertFalse(getUploadButtonClass().contains("v-disabled"));

        clickButton("Set disabled");

        assertTrue(getUploadButtonClass().contains("v-disabled"));
    }
}
