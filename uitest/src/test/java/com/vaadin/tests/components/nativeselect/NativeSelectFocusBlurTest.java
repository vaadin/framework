package com.vaadin.tests.components.nativeselect;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class NativeSelectFocusBlurTest extends MultiBrowserTest {

    @Test
    public void focusBlurEvents() {
        openTestURL();

        NativeSelectElement nativeSelect = $(NativeSelectElement.class).first();
        nativeSelect.click();

        // Focus event is fired
        assertTrue(logContainsText("1. Focus Event"));

        List<TestBenchElement> options = nativeSelect.getOptions();
        options.get(1).click();
        // No any new event
        assertFalse(logContainsText("2."));

        // click on log label => blur
        $(LabelElement.class).first().click();
        // blur event is fired
        assertTrue(logContainsText("2. Blur Event"));

        nativeSelect.click();
        // Focus event is fired
        assertTrue(logContainsText("3. Focus Event"));

        nativeSelect.sendKeys(Keys.ARROW_UP, Keys.ENTER);
        // No any new event
        assertFalse(logContainsText("4."));
    }
}
