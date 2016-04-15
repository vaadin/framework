package com.vaadin.tests.fieldgroup;

import org.junit.Test;

public class CommitWithValidationOrConversionErrorTest extends
        BasicPersonFormTest {

    private static final String UPDATED_BEAN_VALUES = "Person [firstName=John, lastName=Doever, email=john@doe.com, age=123, sex=Male, address=Address [streetAddress=John street, postalCode=11223, city=John's town, country=USA], deceased=false, salary=null, salaryDouble=null, rent=null]";
    private static final String UPDATED_NAME_BEAN_VALUES = "Person [firstName=John, lastName=Doever, email=john@doe.com, age=64, sex=Male, address=Address [streetAddress=John street, postalCode=11223, city=John's town, country=USA], deceased=false, salary=null, salaryDouble=null, rent=null]";

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void testCommitInvalidName() {
        getLastNameArea().setValue("Doev");
        assertCommitFails();
        assertBeanValuesUnchanged();
    }

    @Test
    public void testCommitInvalidAge() {
        // default name invalid, must be fixed or doesn't test the correct error
        getLastNameArea().setValue("Doever");

        getAgeField().setValue("64,2");
        assertCommitFails();
        assertBeanValuesUnchanged();
    }

    @Test
    public void testFixValidationError() {
        getLastNameArea().setValue("Doev");
        assertCommitFails();
        assertBeanValuesUnchanged();

        getLastNameArea().setValue("Doever");
        assertCommitSuccessful();
        showBeanValues();
        assertLogText(UPDATED_NAME_BEAN_VALUES);
    }

    @Test
    public void testFixConversionError() {
        // default name invalid, must be fixed as well
        getLastNameArea().setValue("Doever");

        getAgeField().setValue("64,2");

        assertCommitFails();
        assertBeanValuesUnchanged();

        getAgeField().setValue("123");
        assertCommitSuccessful();

        showBeanValues();
        assertLogText(UPDATED_BEAN_VALUES);
    }

    @Test
    public void testDiscardAfterSuccessfulCommit() {
        getLastNameArea().setValue("Doever");
        getAgeField().setValue("123");
        assertCommitSuccessful();

        discardChanges();
        assertLogText("Discarded changes");
        showBeanValues();
        assertLogText(UPDATED_BEAN_VALUES);
    }
}
