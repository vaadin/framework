package com.vaadin.tests.components.window;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CloseWindowWIthFocusedTextFieldTest extends MultiBrowserTest {

    @Test
    public void OpenWindow_CloseWithEscapeKey_WindowClosed() {
        openTestURL();
        $(ButtonElement.class).first().click();
        assertTrue("Window should be opened", $(WindowElement.class).exists());

        new Actions(getDriver()).sendKeys(Keys.ESCAPE).build().perform();
        assertFalse("Window found when there should be none.",
                $(WindowElement.class).exists());
    }

}
