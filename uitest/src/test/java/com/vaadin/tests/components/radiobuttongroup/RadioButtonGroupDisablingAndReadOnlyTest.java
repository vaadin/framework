package com.vaadin.tests.components.radiobuttongroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.AbstractComponentElement.ReadOnlyException;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test styles, selectByText, and selection by clicking for all enabled/readOnly
 * combinations and how toggling the states affects the selectability. The
 * styles and behaviour should match for all combinations and selection
 * attempts.
 *
 */
public class RadioButtonGroupDisablingAndReadOnlyTest extends MultiBrowserTest {

    private RadioButtonGroupElement group;
    private List<WebElement> options;
    private ButtonElement toggleEnabled;
    private ButtonElement toggleReadOnly;
    private ButtonElement clearSelection;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        group = $(RadioButtonGroupElement.class).first();
        options = group.getOptionElements();
        toggleEnabled = $(ButtonElement.class).caption("Toggle enabled")
                .first();
        toggleReadOnly = $(ButtonElement.class).caption("Toggle readOnly")
                .first();
        clearSelection = $(ButtonElement.class).caption("Clear selection")
                .first();
    }

    private void testSelect() {
        try {
            group.selectByText("b");
        } catch (ReadOnlyException e) {
            // NOP
        }
    }

    @Test
    public void testEnabledToggleWhileReadOnly() {
        assertTrue(group.hasClassName("v-readonly"));
        assertTrue(group.hasClassName("v-disabled"));

        testSelect();
        assertNull(group.getValue());

        options.get(1).click();
        assertNull(group.getValue());

        toggleEnabled.click();

        assertTrue(group.hasClassName("v-readonly"));
        assertFalse(group.hasClassName("v-disabled"));

        testSelect();
        assertNull(group.getValue());

        options.get(1).click();
        assertNull(group.getValue());

        toggleEnabled.click();

        assertTrue(group.hasClassName("v-readonly"));
        assertTrue(group.hasClassName("v-disabled"));

        testSelect();
        assertNull(group.getValue());

        options.get(1).click();
        assertNull(group.getValue());
    }

    @Test
    public void testEnabledToggleWhileNotReadOnly() {
        toggleReadOnly.click();

        assertFalse(group.hasClassName("v-readonly"));
        assertTrue(group.hasClassName("v-disabled"));

        testSelect();
        assertNull(group.getValue());

        options.get(1).click();
        assertNull(group.getValue());

        toggleEnabled.click();

        assertFalse(group.hasClassName("v-readonly"));
        assertFalse(group.hasClassName("v-disabled"));

        testSelect();
        assertEquals("b", group.getValue());

        clearSelection.click();
        assertNull(group.getValue());

        options.get(1).click();
        assertEquals("b", group.getValue());

        clearSelection.click();
        assertNull(group.getValue());

        toggleEnabled.click();

        assertFalse(group.hasClassName("v-readonly"));
        assertTrue(group.hasClassName("v-disabled"));

        testSelect();
        assertNull(group.getValue());

        options.get(1).click();
        assertNull(group.getValue());
    }

    @Test
    public void testReadOnlyToggleWhileDisabled() {
        assertTrue(group.hasClassName("v-readonly"));
        assertTrue(group.hasClassName("v-disabled"));

        testSelect();
        assertNull(group.getValue());

        options.get(1).click();
        assertNull(group.getValue());

        toggleReadOnly.click();

        assertFalse(group.hasClassName("v-readonly"));
        assertTrue(group.hasClassName("v-disabled"));

        testSelect();
        assertNull(group.getValue());

        options.get(1).click();
        assertNull(group.getValue());

        toggleReadOnly.click();

        assertTrue(group.hasClassName("v-readonly"));
        assertTrue(group.hasClassName("v-disabled"));

        testSelect();
        assertNull(group.getValue());

        options.get(1).click();
        assertNull(group.getValue());
    }

    @Test
    public void testReadOnlyToggleWhileEnabled() {
        toggleEnabled.click();

        assertTrue(group.hasClassName("v-readonly"));
        assertFalse(group.hasClassName("v-disabled"));

        testSelect();
        assertNull(group.getValue());

        options.get(1).click();
        assertNull(group.getValue());

        toggleReadOnly.click();

        assertFalse(group.hasClassName("v-readonly"));
        assertFalse(group.hasClassName("v-disabled"));

        testSelect();
        assertEquals("b", group.getValue());

        clearSelection.click();
        assertNull(group.getValue());

        options.get(1).click();
        assertEquals("b", group.getValue());

        clearSelection.click();
        assertNull(group.getValue());

        toggleReadOnly.click();

        assertTrue(group.hasClassName("v-readonly"));
        assertFalse(group.hasClassName("v-disabled"));

        testSelect();
        assertNull(group.getValue());

        options.get(1).click();
        assertNull(group.getValue());
    }
}
