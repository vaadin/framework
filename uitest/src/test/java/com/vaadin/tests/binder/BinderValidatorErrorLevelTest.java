package com.vaadin.tests.binder;

import java.io.IOException;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class BinderValidatorErrorLevelTest extends SingleBrowserTest {

    @Test
    public void testErrorLevelStyleNames() throws IOException {
        openTestURL();

        for (ErrorLevel l : ErrorLevel.values()) {
            TextFieldElement textField = $(TextFieldElement.class)
                    .caption(l.name()).first();

            // Screenshot the whole slot
            compareScreen(textField.findElement(By.xpath("..")),
                    l.name().toLowerCase(Locale.ROOT));

            Assert.assertTrue("Error style for " + l.name() + " not present",
                    textField.getAttribute("class")
                            .contains("v-textfield-error-"
                                    + l.name().toLowerCase(Locale.ROOT)));
            textField.setValue("long enough text");
            Assert.assertFalse("Error style for " + l.name() + " still present",
                    textField.getAttribute("class")
                            .contains("v-textfield-error-"
                                    + l.name().toLowerCase(Locale.ROOT)));
            textField.setValue("foo");
            Assert.assertTrue(
                    "Error style for " + l.name() + " should be present again.",
                    textField.getAttribute("class")
                            .contains("v-textfield-error-"
                                    + l.name().toLowerCase(Locale.ROOT)));
        }
    }
}
