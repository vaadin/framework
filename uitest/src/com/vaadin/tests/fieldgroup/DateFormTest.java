package com.vaadin.tests.fieldgroup;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.InlineDateFieldElement;
import com.vaadin.testbench.elements.PopupDateFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFormTest extends MultiBrowserTest {

    @Test
    public void testCorrectDateFormat() throws Exception {
        openTestURL();
        Assert.assertEquals("Unexpected DateField value,", "1/20/84",
                getDateFieldValue());
        Assert.assertEquals("Unexpected PopupDateField value,", "1/20/84",
                getPopupDateFieldValue());
        WebElement day20 = getInlineDateFieldCalendarPanel().findElement(
                By.vaadin("#day20"));
        Assert.assertTrue(
                "Unexpected InlineDateField state, 20th not selected.",
                hasCssClass(day20,
                        "v-inline-datefield-calendarpanel-day-selected"));
        Assert.assertEquals("Unexpected TextField contents,",
                "Jan 20, 1984 4:34:49 PM", $(TextFieldElement.class).first()
                        .getValue());
    }

    protected String getDateFieldValue() {
        return $(DateFieldElement.class).first().getValue();
    }

    protected String getPopupDateFieldValue() {
        return $(PopupDateFieldElement.class).first().getValue();
    }

    protected WebElement getInlineDateFieldCalendarPanel() {
        return $(InlineDateFieldElement.class).first().findElement(
                By.className("v-inline-datefield-calendarpanel"));
    }

}
