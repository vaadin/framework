package com.vaadin.tests.components.upload;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

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
    public void buttonIsReadonly() {
        assertThat(getUploadButtonClass(), not(containsString("v-disabled")));

        clickButton("Set readonly");

        assertThat(getUploadButtonClass(), containsString("v-disabled"));
    }

    @Test
    public void buttonIsDisabled() {
        assertThat(getUploadButtonClass(), not(containsString("v-disabled")));

        clickButton("Set disabled");

        assertThat(getUploadButtonClass(), containsString("v-disabled"));
    }
}