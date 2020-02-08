package com.vaadin.tests.components.datefield;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DateFieldBinderCrossValidationTest extends SingleBrowserTest {

    private final static String EXPECTED_ERROR = "from field is Date is out of allowed range. To field is Date is out of allowed range";
    private final static String EXPECTED_NULL_ERROR = "from field is null. To field is null";

    @Test
    public void makeBothFieldInvalidThenValid() {
        openTestURL();

        DateFieldElement fromField = $(DateFieldElement.class).id("from-field");
        WebElement fromFieldText = fromField.findElement(By.tagName("input"));
        DateFieldElement toField = $(DateFieldElement.class).id("to-field");
        WebElement toFieldText = toField.findElement(By.tagName("input"));
        LabelElement label = $(LabelElement.class).id("status");

        fromFieldText.sendKeys("2019/01/01", Keys.ENTER);
        toFieldText.sendKeys("2018/02/02", Keys.ENTER);

        assertEquals("Error message should contain the information",
                EXPECTED_ERROR, label.getText());

        fromFieldText.clear();
        fromFieldText.sendKeys("2018/01/01", Keys.ENTER);
        assertEquals("Error message should be null", EXPECTED_NULL_ERROR,
                label.getText());
    }

    @Test
    public void dateFieldRangeYearDigitsIncrease() {
        openTestURL();

        DateFieldElement toField = $(DateFieldElement.class).id("to-field");
        // This will set the rangeEnd of the fromField
        WebElement toFieldText = toField.findElement(By.tagName("input"));
        toFieldText.sendKeys("9999/12/31", Keys.ENTER);

        DateFieldElement fromField = $(DateFieldElement.class).id("from-field");
        fromField.openPopup();
        waitForElementNotPresent(By.className("v-datefield-popup"));

        WebElement monthYearLabel = findElement(By.className("v-datefield-calendarpanel-month"));

        // The next month button should be disabled
        findElement(By.className("v-button-nextmonth")).click();
        // Test that year has not changed
        assertTrue("",monthYearLabel.getText().contains("9999"));

        // The next year button should be disabled
        findElement(By.className("v-button-nextyear")).click();
        // Test that year has not changed
        assertTrue("",monthYearLabel.getText().contains("9999"));
    }

    @Test
    public void dateFieldRangeYearBigNumbersPopupOpens() {
        openTestURL();

        DateFieldElement toField = $(DateFieldElement.class).id("to-field");
        // This will set the rangeEnd of the fromField
        WebElement toFieldText = toField.findElement(By.tagName("input"));
        toFieldText.sendKeys("10000/12/31", Keys.ENTER);
        DateFieldElement fromField = $(DateFieldElement.class).id("from-field");

        // Test that popup opens
        fromField.openPopup();
        waitForElementNotPresent(By.className("v-datefield-popup"));
        assertElementPresent(By.className("v-datefield-popup"));
    }
}
