package com.vaadin.tests.components.checkboxgroup;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CheckBoxGroupFocusTest extends MultiBrowserTest {

    @Test
    public void focusOnInit() {
        openTestURL();
        WebElement focused = getFocusedElement();
        assertNotNull(
                "No focused element found in RadioButtonGroup after initial focus()",
                focused);
        CheckBoxGroupElement checkBoxGroup = $(CheckBoxGroupElement.class)
                .first();
        Boolean isChild = (Boolean) executeScript(
                "return (arguments[0].querySelector(\"#gwt-uid-12\") == arguments[1]);",
                checkBoxGroup, focused);
        assertTrue("Focused element is in the first RadioButtonGroup", isChild);
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Focus does not move when expected with Selenium/TB and Firefox 45
        return getBrowsersExcludingFirefox();
    }

}
