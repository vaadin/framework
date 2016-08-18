package com.vaadin.tests.fieldgroup;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.v7.tests.elements.LegacyDateFieldElement;
import com.vaadin.v7.tests.elements.LegacyInlineDateFieldElement;
import com.vaadin.v7.tests.elements.LegacyPopupDateFieldElement;
import com.vaadin.v7.tests.elements.LegacyTextFieldElement;

public class DateFormTest extends MultiBrowserTest {

    @Test
    public void testCorrectDateFormat() throws Exception {
        openTestURL();
        Assert.assertEquals("Unexpected DateField value,", "1/20/84",
                getDateFieldValue());
        Assert.assertEquals("Unexpected PopupDateField value,", "1/20/84",
                getPopupDateFieldValue());
        WebElement day20 = getInlineDateFieldCalendarPanel()
                .findElement(By.vaadin("#day20"));
        Assert.assertTrue(
                "Unexpected InlineDateField state, 20th not selected.",
                hasCssClass(day20,
                        "v-inline-datefield-calendarpanel-day-selected"));
        Assert.assertEquals("Unexpected TextField contents,",
                "Jan 20, 1984 4:34:49 PM",
                $(LegacyTextFieldElement.class).first().getValue());
    }

    protected String getDateFieldValue() {
        return $(LegacyDateFieldElement.class).first().getValue();
    }

    protected String getPopupDateFieldValue() {
        return $(LegacyPopupDateFieldElement.class).first().getValue();
    }

    protected WebElement getInlineDateFieldCalendarPanel() {
        return $(LegacyInlineDateFieldElement.class).first()
                .findElement(By.className("v-inline-datefield-calendarpanel"));
    }

}
