package com.vaadin.tests.fieldgroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.DesiredCapabilities;

public class BooleanTextFieldTest extends BasicPersonFormTest {

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Test
    public void testSetBooleanTextField() throws Exception {
        openTestURL();

        getLastNameArea().clear();
        getLastNameArea().click();
        getLastNameArea().sendKeys("Dover");

        assertBeanValuesUnchanged();

        getDeceasedField().click();
        while (!getDeceasedField().getValue().isEmpty()) {
            getDeceasedField().sendKeys(Keys.BACK_SPACE);
        }
        getDeceasedField().sendKeys("false", Keys.ENTER);

        /* error indicator */
        assertEquals("Incorrect amount of error indicators on page.", 1,
                findElements(By.className("v-errorindicator")).size());
        assertEquals("false", getDeceasedField().getAttribute("value"));

        assertBeanValuesUnchanged();

        /* error message in tooltip */
        getDeceasedField().showTooltip();
        assertEquals("Could not convert value to Boolean",
                getTooltipErrorElement().getText());

        getDeceasedField().click();
        while (!getDeceasedField().getValue().isEmpty()) {
            getDeceasedField().sendKeys(Keys.BACK_SPACE);
        }
        getDeceasedField().sendKeys("YAY!", Keys.ENTER);

        /* no error indicator */
        assertFalse(isElementPresent(By.className("v-errorindicator")));

        assertCommitSuccessful();

        /* commit last name and new deceased status */
        showBeanValues();
        assertEquals(
                "4. Person [firstName=John, lastName=Dover, email=john@doe.com, age=64, sex=Male, address=Address [streetAddress=John street, postalCode=11223, city=John's town, country=USA], deceased=true, salary=null, salaryDouble=null, rent=null]",
                getLogRow(0));
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // this test also works on IEs, but Firefox has problems with tooltips
        return getBrowsersExcludingFirefox();
    }

}
