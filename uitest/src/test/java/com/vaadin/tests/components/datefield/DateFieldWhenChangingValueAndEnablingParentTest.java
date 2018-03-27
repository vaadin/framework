package com.vaadin.tests.components.datefield;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.PopupDateFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DateFieldWhenChangingValueAndEnablingParentTest
        extends SingleBrowserTest {

    @Test
    public void ensureCorrectStateAfterEnabling() {
        openTestURL();
        $(CheckBoxElement.class).first().click();

        assertState($(DateFieldElement.class).id("DATEFIELD_ENABLED"), true,
                true);
        assertState($(DateFieldElement.class).id("DATEFIELD_DISABLED"), false,
                false);

        assertState(
                $(PopupDateFieldElement.class).id("DATEFIELD_ENABLED_ENABLED"),
                true, true);
        assertState(
                $(PopupDateFieldElement.class).id("DATEFIELD_ENABLED_DISABLED"),
                true, false);

        // disabling widget should always disable input
        assertState(
                $(PopupDateFieldElement.class).id("DATEFIELD_DISABLED_ENABLED"),
                false, false);
        assertState($(PopupDateFieldElement.class)
                .id("DATEFIELD_DISABLED_DISABLED"), false, false);

    }

    /**
     * @since
     * @param id
     * @param widgetEnabled
     * @param textInputEnabled
     */
    private void assertState(DateFieldElement id, boolean widgetEnabled,
            boolean textInputEnabled) {
        assertDateFieldEnabled(id, widgetEnabled);
        assertTextInputEnabled(id, textInputEnabled);

    }

    private void assertDateFieldEnabled(DateFieldElement id,
            boolean assertEnabled) {
        boolean hasClass = hasCssClass(id, "v-disabled");
        boolean fieldEnabled = !hasClass;
        if (assertEnabled) {
            Assert.assertTrue(
                    "Field " + id.getAttribute("id") + " should be enabled",
                    fieldEnabled);
        } else {
            Assert.assertFalse(
                    "Field " + id.getAttribute("id") + " should be disabled",
                    fieldEnabled);
        }

    }

    private void assertTextInputEnabled(DateFieldElement id, boolean enabled) {
        String disabledAttr = id.findElement(By.xpath("./input"))
                .getAttribute("disabled");
        boolean textinputEnabled = (disabledAttr == null);

        if (enabled) {
            Assert.assertTrue(
                    "Field " + id.getAttribute("id")
                            + " text field should be enabled",
                    textinputEnabled);
        } else {
            Assert.assertFalse(
                    "Field " + id.getAttribute("id")
                            + " text field should be disabled",
                    textinputEnabled);
        }

    }
}
