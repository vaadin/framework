package com.vaadin.tests.components.window;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ShortcutInWindowTest extends SingleBrowserTest {

    @Test
    public void shortcutFlushesActiveField() {
        openTestURL();
        TextFieldElement tf = $(TextFieldElement.class).first();
        tf.sendKeys("foo" + Keys.ENTER);
        assertEquals("2. Submitted value: foo", getLogRow(0));
    }
}
