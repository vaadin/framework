package com.vaadin.tests.fieldgroup;

import org.junit.Test;
import org.openqa.selenium.Keys;

public class CommitHandlerFailuresTest extends BasicPersonFormTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void testDefaults() {
        assertDefaults();
        assertBeanValuesUnchanged();
    }

    @Test
    public void testUpdatingWithoutCommit() {
        updateFields();
        assertBeanValuesUnchanged();
    }

    @Test
    public void testPreCommitFails() {
        updateFields();

        getPreCommitFailsCheckBox().click();
        assertCommitFails();

        assertBeanValuesUnchanged();
    }

    @Test
    public void testPostCommitFails() {
        updateFields();

        getPostCommitFailsCheckBox().click();
        assertCommitFails();

        assertBeanValuesUnchanged();
    }

    @Test
    public void testDiscard() {
        updateFields();
        assertDiscardResetsFields();
        assertBeanValuesUnchanged();
    }

    private void updateFields() {
        getLastNameArea().sendKeys("Doeve", Keys.ENTER);
        getFirstNameField().sendKeys("Mike", Keys.ENTER);
        getEmailField().sendKeys("me@me.com", Keys.ENTER);
        getAgeField().sendKeys("12", Keys.ENTER);
        getGenderTable().getCell(2, 0).click();
    }
}
