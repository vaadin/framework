package com.vaadin.tests.components.upload;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.UploadElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UploadHtmlCaptionTest extends MultiBrowserTest {

    @Test
    public void htmlCaptionToggle() {
        openTestURL();

        UploadElement upload = $(UploadElement.class).first();
        WebElement submitButtonCaption = upload
                .findElement(By.className("v-button-caption"));
        WebElement componentCaption = findElement(By.className("v-caption"));
        ButtonElement toggleButtonCaption = $(ButtonElement.class)
                .id("toggleButtonCaption");
        ButtonElement toggleComponentCaption = $(ButtonElement.class)
                .id("toggleComponentCaption");

        assertTrue(
                "Unexpected submit button caption: "
                        + submitButtonCaption.getText(),
                submitButtonCaption.getText().contains("<b>"));
        assertTrue(
                "Unexpected component caption: " + componentCaption.getText(),
                componentCaption.getText().contains("<b>"));

        toggleButtonCaption.click();

        assertFalse(
                "Unexpected submit button caption: "
                        + submitButtonCaption.getText(),
                submitButtonCaption.getText().contains("<b>"));
        assertTrue(
                "Unexpected component caption: " + componentCaption.getText(),
                componentCaption.getText().contains("<b>"));

        toggleComponentCaption.click();

        assertFalse(
                "Unexpected submit button caption: "
                        + submitButtonCaption.getText(),
                submitButtonCaption.getText().contains("<b>"));
        assertFalse(
                "Unexpected component caption: " + componentCaption.getText(),
                componentCaption.getText().contains("<b>"));

        toggleButtonCaption.click();

        assertTrue(
                "Unexpected submit button caption: "
                        + submitButtonCaption.getText(),
                submitButtonCaption.getText().contains("<b>"));
        assertFalse(
                "Unexpected component caption: " + componentCaption.getText(),
                componentCaption.getText().contains("<b>"));
    }
}
