package com.vaadin.tests.components.datefield;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.customelements.AbstractDateFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldDayResolutionOffsetTest extends MultiBrowserTest {

    @Test
    public void dateValueDoesNotHaveOffset() throws InterruptedException {
        openTestURL();

        openDatePicker();
        select2ndOfSeptember();

        LabelElement dateValue = $(LabelElement.class).id("dateValue");
        assertThat(dateValue.getText(), is("09/02/2014 00:00:00"));
    }

    private void select2ndOfSeptember() {
        for (WebElement e : findElements(
                By.className("v-datefield-calendarpanel-day"))) {
            if (e.getText().equals("2")) {
                e.click();
                break;
            }
        }
    }

    private void openDatePicker() {
        AbstractDateFieldElement dateField = $(AbstractDateFieldElement.class)
                .first();

        dateField.findElement(By.tagName("button")).click();
    }

}
