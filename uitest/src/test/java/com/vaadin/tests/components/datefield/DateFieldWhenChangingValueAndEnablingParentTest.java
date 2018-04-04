package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.AbstractDateFieldElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DateFieldWhenChangingValueAndEnablingParentTest
        extends SingleBrowserTest {

    @Test
    public void ensureCorrectStateAfterEnabling() {
        openTestURL();
        $(CheckBoxElement.class).first().click();

        assertState($(AbstractDateFieldElement.class).id("DATEFIELD_ENABLED"),
                true, true);
        assertState($(AbstractDateFieldElement.class).id("DATEFIELD_DISABLED"),
                false, false);

        assertState($(DateFieldElement.class).id("DATEFIELD_ENABLED_ENABLED"),
                true, true);
        assertState($(DateFieldElement.class).id("DATEFIELD_ENABLED_DISABLED"),
                true, false);

        // disabling widget should always disable input
        assertState($(DateFieldElement.class).id("DATEFIELD_DISABLED_ENABLED"),
                false, false);
        assertState($(DateFieldElement.class).id("DATEFIELD_DISABLED_DISABLED"),
                false, false);

    }

    private void assertState(AbstractDateFieldElement id, boolean widgetEnabled,
            boolean textInputEnabled) {
        assertDateFieldEnabled(id, widgetEnabled);
        assertTextInputEnabled(id, textInputEnabled);

    }

    private void assertDateFieldEnabled(AbstractDateFieldElement id,
            boolean assertEnabled) {
        boolean hasClass = hasCssClass(id, "v-disabled");
        boolean fieldEnabled = !hasClass;
        if (assertEnabled) {
            assertTrue("Field " + id.getAttribute("id") + " should be enabled",
                    fieldEnabled);
        } else {
            assertFalse(
                    "Field " + id.getAttribute("id") + " should be disabled",
                    fieldEnabled);
        }

    }

    private void assertTextInputEnabled(AbstractDateFieldElement id,
            boolean enabled) {
        String disabledAttr = id.findElement(By.xpath("./input"))
                .getAttribute("disabled");
        boolean textinputEnabled = (disabledAttr == null);

        if (enabled) {
            assertTrue(
                    "Field " + id.getAttribute("id")
                            + " text field should be enabled",
                    textinputEnabled);
        } else {
            assertFalse(
                    "Field " + id.getAttribute("id")
                            + " text field should be disabled",
                    textinputEnabled);
        }

    }
}
