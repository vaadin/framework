package com.vaadin.tests.applicationservlet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MultipleServletConfigurationTest extends MultiBrowserTest {

    @Override
    protected void closeApplication() {
    }

    @Test
    public void testMultipleServletConfiguration() throws Exception {
        getDriver().get(getBaseURL() + "/embed1");
        assertLabelText("Verify that Button HTML rendering works");
        getDriver().get(getBaseURL() + "/embed2");
        assertLabelText(
                "Margins inside labels should not be allowed to collapse out of the label as it causes problems with layotus measuring the label.");
        getDriver().get(getBaseURL() + "/embed1");
        assertLabelText("Verify that Button HTML rendering works");
    }

    private void assertLabelText(String expected) {
        assertEquals("Unexpected label text,", expected,
                $(LabelElement.class).first().getText());
    }
}
