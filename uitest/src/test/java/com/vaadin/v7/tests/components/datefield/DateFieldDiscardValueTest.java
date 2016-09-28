package com.vaadin.v7.tests.components.datefield;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DateFieldDiscardValueTest extends SingleBrowserTest {

    @Test
    public void discardWhenDatasourceContentNonNullInvalidValue() {
        discardWorks(DateFieldDiscardValue.PROP_NONULL, "1", "123", "25/07/16");
    }

    @Test
    public void discardWhenDatasourceContentNonNullValidValue() {
        discardWorks(DateFieldDiscardValue.PROP_NONULL, "1", "24/07/16",
                "25/07/16");
    }

    @Test
    public void discardWhenDatasourceContentNullInvalidValue() {
        discardWorks(DateFieldDiscardValue.PROP_NULL_VALUE, "2", "123", "");
    }

    @Test
    public void discardWhenDatasourceContentNullValidValue() {
        discardWorks(DateFieldDiscardValue.PROP_NULL_VALUE, "2", "24/07/16",
                "");
    }

    @Test
    public void discardWhenDatasourceNull() {
        // If the data source is null, discard should do nothing.
        discardDoesntWork(DateFieldDiscardValue.PROP_NULL, "3", "123");
    }

    private void discardWorks(String caption, String id, String dateValue,
            String resultValue) {
        openTestURL();

        ButtonElement discardButton = $(ButtonElement.class)
                .caption("Discard " + id).first();
        DateFieldElement dateField = $(DateFieldElement.class).caption(caption)
                .first();
        dateField.setValue(dateValue);

        discardButton.click();

        assertEquals(resultValue, dateField.getValue());

        List<WebElement> elements = driver
                .findElements(By.className("v-errorindicator"));

        assertEquals(0, elements.size());
    }

    private void discardDoesntWork(String caption, String id,
            String dateValue) {
        openTestURL();

        ButtonElement discardButton = $(ButtonElement.class)
                .caption("Discard " + id).first();
        DateFieldElement dateField = $(DateFieldElement.class).caption(caption)
                .first();
        dateField.setValue(dateValue);

        discardButton.click();

        assertEquals(dateValue, dateField.getValue());

        List<WebElement> elements = driver
                .findElements(By.className("v-errorindicator"));

        assertEquals(1, elements.size());
    }

}