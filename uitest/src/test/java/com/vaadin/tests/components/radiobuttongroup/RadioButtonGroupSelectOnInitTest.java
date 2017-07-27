package com.vaadin.tests.components.radiobuttongroup;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class RadioButtonGroupSelectOnInitTest extends SingleBrowserTest {

    @Test
    public void testSelectNull() {
        openTestURL();
        assertInitial();

        $(ButtonElement.class).caption("Deselect").first().click();
        Assert.assertNull("No value should be selected",
                getRadioButtonGroup().getValue());
    }

    @Test
    public void testSelectOnClientAndRefresh() {
        openTestURL();
        assertInitial();

        RadioButtonGroupElement rbg = getRadioButtonGroup();
        rbg.selectByText("Baz");
        Assert.assertEquals("Value should change", "Baz", rbg.getValue());

        $(ButtonElement.class).caption("Refresh").first().click();
        Assert.assertEquals("Value should stay the same through refreshAll",
                "Baz", rbg.getValue());
    }

    @Test
    public void testSelectOnClientAndResetValueOnServer() {
        openTestURL();
        assertInitial();

        RadioButtonGroupElement rbg = getRadioButtonGroup();
        rbg.selectByText("Baz");
        Assert.assertEquals("Value should change", "Baz", rbg.getValue());

        $(ButtonElement.class).caption("Select Bar").first().click();
        Assert.assertEquals("Original value should be selected again", "Bar",
                rbg.getValue());
    }

    @Test
    public void testSelectOnClientAndResetValueOnServerInListener() {
        openTestURL();
        assertInitial();

        RadioButtonGroupElement rbg = getRadioButtonGroup();
        rbg.selectByText("Reset");
        // Selecting "Reset" selects "Bar" on server. Value was initially "Bar"
        Assert.assertEquals("Original value should be selected again", "Bar",
                rbg.getValue());
    }

    private void assertInitial() {
        Assert.assertEquals("Initial state unexpected", "Bar",
                getRadioButtonGroup().getValue());
    }

    private RadioButtonGroupElement getRadioButtonGroup() {
        return $(RadioButtonGroupElement.class).first();
    }
}
