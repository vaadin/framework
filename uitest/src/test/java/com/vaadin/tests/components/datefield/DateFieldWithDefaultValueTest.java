package com.vaadin.tests.components.datefield;

import static com.vaadin.tests.components.datefield.DateFieldWithDefaultValue.DATEFIELD_HAS_DEFAULT;
import static com.vaadin.tests.components.datefield.DateFieldWithDefaultValue.DATEFIELD_REGULAR;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldWithDefaultValueTest extends MultiBrowserTest {

    @Test
    public void testDateFieldDefaultValue() {
        openTestURL();
        String datePickerId = DATEFIELD_HAS_DEFAULT;
        getDateFieldElement(datePickerId).openPopup();
        WebElement monthSpanElement = getMonthSpanFromVisibleCalendarPanel();
        // Can't check for "October 2010", since IE11 translates October ->
        // lokakuu
        assert (monthSpanElement.getText().contains("2010"));
    }

    @Test
    public void testDateFieldWithNoDefault() {
        openTestURL();
        String datePickerId = DATEFIELD_REGULAR;
        getDateFieldElement(datePickerId).openPopup();
        WebElement monthSpanElement = getMonthSpanFromVisibleCalendarPanel();
        assert (!monthSpanElement.getText().contains("2010"));
    }

    private WebElement getMonthSpanFromVisibleCalendarPanel() {
        return getDriver()
                .findElements(
                        By.cssSelector(".v-datefield-calendarpanel-month"))
                .get(0);
    }

    private DateFieldElement getDateFieldElement(String id) {
        return $(DateFieldElement.class).id(id);
    }

}
