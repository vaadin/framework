package com.vaadin.tests.components.checkbox;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CheckBoxNullValueTest extends MultiBrowserTest {
    @Test
    public void testValidation() throws Exception {
        openTestURL();
        CheckBoxElement checkbox = $(CheckBoxElement.class).first();
        CheckBoxElement requiredCheckbox = $(CheckBoxElement.class).caption(
                "A required checkbox").first();

        assertValid(checkbox, true);
        assertValid(requiredCheckbox, true);
        ButtonElement validate = $(ButtonElement.class).caption("Validate")
                .first();
        validate.click();

        assertValid(checkbox, true);
        assertValid(requiredCheckbox, false);

        click(checkbox);
        click(requiredCheckbox);
        validate.click();

        assertValid(checkbox, true);
        assertValid(requiredCheckbox, true);

        click(checkbox);
        click(requiredCheckbox);
        validate.click();
        assertValid(checkbox, true);
        assertValid(requiredCheckbox, false);

    }

    private void assertValid(CheckBoxElement checkbox, boolean valid) {
        boolean hasIndicator = false;
        List<WebElement> e = checkbox.findElements(By
                .className("v-errorindicator"));
        if (e.size() != 0) {
            hasIndicator = e.get(0).isDisplayed();
        }

        Assert.assertEquals("Checkbox state should be "
                + (valid ? "valid" : "invalid"), valid, !hasIndicator);

    }

}
