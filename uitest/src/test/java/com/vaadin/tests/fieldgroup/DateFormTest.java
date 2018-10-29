package com.vaadin.tests.fieldgroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.InlineDateFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFormTest extends MultiBrowserTest {

    private final SimpleDateFormat FORMAT = new SimpleDateFormat(
            "MMM dd, yyyy h:mm:ss a");

    @Test
    public void testCorrectDateFormat() throws Exception {
        openTestURL();
        assertEquals("Unexpected DateField value,", "1/20/84",
                getDateFieldValue());
        assertEquals("Unexpected PopupDateField value,", "1/21/84",
                getPopupDateFieldValue());
        WebElement day20 = getInlineDateFieldCalendarPanel()
                .findElement(By.vaadin("#day20"));
        assertTrue("Unexpected InlineDateField state, 20th not selected.",
                hasCssClass(day20,
                        "v-inline-datefield-calendarpanel-day-selected"));
        // Depends on the TZ offset on the server
        assertEquals("Unexpected TextField contents,",
                FORMAT.format(DateForm.DATE),
                $(TextFieldElement.class).first().getValue());
    }

    protected String getDateFieldValue() {
        return $(DateFieldElement.class).first().getValue();
    }

    protected String getPopupDateFieldValue() {
        return $(DateFieldElement.class).get(1).getValue();
    }

    protected WebElement getInlineDateFieldCalendarPanel() {
        return $(InlineDateFieldElement.class).first()
                .findElement(By.className("v-inline-datefield-calendarpanel"));
    }

}