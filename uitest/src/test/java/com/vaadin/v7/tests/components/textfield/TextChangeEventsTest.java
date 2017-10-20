package com.vaadin.v7.tests.components.textfield;

import static org.junit.Assert.assertEquals;

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
        assertEquals(
                "1. Text change event for Default text area, text content currently:'abc' Cursor at index:3",
                getLogRow(0));

        TextAreaElement taTimeout = $(TextAreaElement.class)
                .caption("Timeout 3s").first();
        taTimeout.sendKeys("abc");
        assertEquals(
                "2. Text change event for Timeout 3s, text content currently:'abc' Cursor at index:3",
                getLogRow(0));

    }

    @Test
    public void textFieldWaitsForTextChangeEvents() {
        openTestURL();

        TextFieldElement tfDefault = $(TextFieldElement.class)
                .caption("Default").first();
        tfDefault.sendKeys("abc");
        assertEquals(
                "1. Text change event for Default, text content currently:'abc' Cursor at index:3",
                getLogRow(0));

        TextFieldElement tfEager = $(TextFieldElement.class).caption("Eager")
                .first();
        tfEager.sendKeys("abc");
        assertEquals(
                "2. Text change event for Eager, text content currently:'abc' Cursor at index:3",
                getLogRow(0));

        TextFieldElement tfTimeout = $(TextFieldElement.class)
                .caption("Timeout 3s").first();
        tfTimeout.sendKeys("abc");
        assertEquals(
                "3. Text change event for Timeout 3s, text content currently:'abc' Cursor at index:3",
                getLogRow(0));

    }
}
