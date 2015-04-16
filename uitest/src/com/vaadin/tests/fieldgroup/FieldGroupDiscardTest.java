package com.vaadin.tests.fieldgroup;

import org.junit.Test;
import org.openqa.selenium.Keys;

public class FieldGroupDiscardTest extends BasicPersonFormTest {

    @Test
    public void testFieldGroupDiscard() throws Exception {
        openTestURL();
        assertDefaults();

        /* make some changes */
        getFirstNameField().sendKeys("John123", Keys.ENTER);
        getLastNameArea().sendKeys("Doe123", Keys.ENTER);
        getEmailField().sendKeys("john@doe.com123", Keys.ENTER);
        getAgeField().sendKeys("64123", Keys.ENTER);
        getGenderTable().getCell(2, 0);
        getDeceasedField().click();
        getDeceasedField().click();
        getDeceasedField().sendKeys("YAY!", Keys.ENTER);

        assertBeanValuesUnchanged();

        assertDiscardResetsFields();

        assertBeanValuesUnchanged();

        /* we should still be at the state we started from */
        assertDefaults();
    }
}
