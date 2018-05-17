package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class AriaDisabledTest extends MultiBrowserTest {

    @Test
    public void verifyAriaDisabledAttributes() {
        openTestURL();

        // Expect aria-disabled="false" on the enabled DateField.
        String ariaDisabled = driver
                .findElement(By.vaadin(
                        "/VVerticalLayout[0]/VPopupCalendar[1]#popupButton"))
                .getAttribute("aria-disabled");
        assertEquals("false", ariaDisabled);

        // Expect aria-disabled="true" on the disabled DateField.
        ariaDisabled = driver.findElement(By.cssSelector(".v-disabled button"))
                .getAttribute("aria-disabled");
        assertEquals("true", ariaDisabled);
    }

}
