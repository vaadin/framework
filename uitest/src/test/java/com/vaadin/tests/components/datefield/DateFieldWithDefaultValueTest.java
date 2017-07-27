package com.vaadin.tests.components.datefield;

import static com.vaadin.tests.components.datefield.DateFieldWithDefaultValue.DATEFIELD_HAS_DEFAULT;
import static com.vaadin.tests.components.datefield.DateFieldWithDefaultValue.DATEFIELD_REGULAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import javax.validation.constraints.AssertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.AbstractDateFieldElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.ui.UI;

public class DateFieldWithDefaultValueTest extends MultiBrowserTest {

    @Test
    public void testDateFieldDefaultValue() {
        openTestURL();
        String datePickerId = DATEFIELD_HAS_DEFAULT;
        clickDateDatePickerButton(datePickerId);
        WebElement monthSpanElement = getMonthSpanFromVisibleCalendarPanel();
        // Can't check for "October 2010", since IE11 translates October -> lokakuu
        assert(monthSpanElement.getText().contains("2010"));
    }

    @Test
    public void testDateFieldWithNoDefault() {
        openTestURL();
        String datePickerId = DATEFIELD_REGULAR;
        clickDateDatePickerButton(datePickerId);
        WebElement monthSpanElement = getMonthSpanFromVisibleCalendarPanel();
        assert(!monthSpanElement.getText().contains("2010"));
    }

    private WebElement getMonthSpanFromVisibleCalendarPanel() {
        return getDriver()
        .findElements(By.cssSelector(
                ".v-datefield-calendarpanel-month")).get(0);
        
    }

    private DateFieldElement getDateFieldElement(String id) {
        return $(DateFieldElement.class).id(id);
    }

    private WebElement getToggleButton(String id) {
        AbstractDateFieldElement dateField = getDateFieldElement(id);
        return dateField.findElement(By.tagName("button"));
    }

    private void clickDateDatePickerButton(String id) {
        getToggleButton(id).click();
    }

}
