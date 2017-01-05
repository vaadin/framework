package com.vaadin.tests.components.textfield;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TextChangeEventsTest extends SingleBrowserTest {

    @Test
    public void textAreaWaitsForTextChangeEvents() {
        openTestURL();

        TextAreaElement taDefault = $(TextAreaElement.class)
                .caption("Default text area").first();
        taDefault.sendKeys("abc");
        Assert.assertEquals(getLogRow(0),
                "1. Text change event for Default text area, text content currently:'abc' Cursor at index:3");

        TextAreaElement taTimeout = $(TextAreaElement.class)
                .caption("Timeout 3s").first();
        taTimeout.sendKeys("abc");
        Assert.assertEquals(getLogRow(0),
                "2. Text change event for Timeout 3s, text content currently:'abc' Cursor at index:3");
    }

    @Test
    public void textFieldWaitsForTextChangeEvents() {
        openTestURL();

        TextFieldElement tfDefault = $(TextFieldElement.class)
                .caption("Default").first();
        tfDefault.sendKeys("abc");
        Assert.assertEquals(getLogRow(0),
                "1. Text change event for Default, text content currently:'abc' Cursor at index:3");

        TextFieldElement tfEager = $(TextFieldElement.class).caption("Eager")
                .first();
        tfEager.sendKeys("abc");
        Assert.assertTrue(getLogRow(0).contains(
                "Text change event for Eager, text content currently:'abc' Cursor at index:3"));

        TextFieldElement tfTimeout = $(TextFieldElement.class)
                .caption("Timeout 3s").first();
        tfTimeout.sendKeys("abc");
        Assert.assertTrue(getLogRow(0).contains(
                "Text change event for Timeout 3s, text content currently:'abc' Cursor at index:3"));
    }
}
