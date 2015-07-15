/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.fieldgroup;

import org.junit.Assert;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableRowElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.FixedNotificationElement;

public abstract class BasicPersonFormTest extends MultiBrowserTest {

    private static final String BEAN_VALUES = "Person [firstName=John, lastName=Doe, email=john@doe.com, age=64, sex=Male, address=Address [streetAddress=John street, postalCode=11223, city=John's town, country=USA], deceased=false, salary=null, salaryDouble=null, rent=null]";
    private int logCounter;

    @Override
    public void setup() throws Exception {
        super.setup();

        logCounter = 0;
    }

    @Override
    protected Class<?> getUIClass() {
        return BasicPersonForm.class;
    }

    protected TextFieldElement getFirstNameField() {
        return $(TextFieldElement.class).caption("First Name").first();
    }

    protected TextAreaElement getLastNameArea() {
        return $(TextAreaElement.class).caption("Last Name").first();
    }

    protected TextFieldElement getEmailField() {
        return $(TextFieldElement.class).caption("Email").first();
    }

    protected TextFieldElement getAgeField() {
        return $(TextFieldElement.class).caption("Age").first();
    }

    protected TableElement getGenderTable() {
        return $(TableElement.class).caption("Sex").first();
    }

    protected TextFieldElement getDeceasedField() {
        return $(TextFieldElement.class).caption("Deceased").first();
    }

    protected void showBeanValues() {
        $(ButtonElement.class).caption("Show bean values").first().click();
    }

    protected CheckBoxElement getPreCommitFailsCheckBox() {
        return $(CheckBoxElement.class).get(1);
    }

    protected void commitChanges() {
        $(ButtonElement.class).caption("Commit").first().click();
    }

    protected void closeNotification() {
        $(FixedNotificationElement.class).first().close();
    }

    protected CheckBoxElement getPostCommitFailsCheckBox() {
        return $(CheckBoxElement.class).get(0);
    }

    protected void discardChanges() {
        $(ButtonElement.class).caption("Discard").first().click();
    }

    protected void assertFirstNameValue(String expected) {
        assertFieldValue("First Name", expected, getFirstNameField());
    }

    protected void assertLastNameValue(String expected) {
        assertFieldValue("Last Name", expected, getLastNameArea());
    }

    protected void assertEmailValue(String expected) {
        assertFieldValue("Email", expected, getEmailField());
    }

    protected void assertAgeValue(String expected) {
        assertFieldValue("Age", expected, getAgeField());
    }

    protected void assertDeceasedValue(String expected) {
        assertFieldValue("Deceased", expected, getDeceasedField());
    }

    private void assertFieldValue(String caption, String expected,
            TestBenchElement field) {
        Assert.assertEquals(
                String.format("Unexpected value for field '%s',", caption),
                expected, field.getAttribute("value"));
    }

    protected void assertSelectedSex(Sex sex) {
        TableRowElement row = getGenderTable().getRow(getIndex(sex));
        Assert.assertTrue(
                String.format("Given sex (%s) isn't selected.",
                        sex.getStringRepresentation()),
                hasCssClass(row, "v-selected"));
    }

    private int getIndex(Sex sex) {
        switch (sex) {
        case MALE:
            return 0;
        case FEMALE:
            return 1;
        default:
            return 2;
        }
    }

    protected void assertBeanValuesUnchanged() {
        showBeanValues();
        assertLogText(BEAN_VALUES);
    }

    protected void assertCommitFails() {
        commitChanges();
        closeNotification();
        assertLogText("Commit failed: Commit failed");
    }

    protected void assertCommitSuccessful() {
        commitChanges();
        closeNotification();
        assertLogText("Commit succesful");
    }

    protected void assertDiscardResetsFields() {
        discardChanges();
        assertLogText("Discarded changes");
        assertDefaults();
    }

    protected void assertLogText(String expected) {
        ++logCounter;
        Assert.assertEquals("Unexpected log contents,", logCounter + ". "
                + expected, getLogRow(0));
    }

    protected void assertDefaults() {
        assertFirstNameValue("John");
        assertLastNameValue("Doe");
        assertEmailValue("john@doe.com");
        assertAgeValue("64");
        assertSelectedSex(Sex.MALE);
        assertDeceasedValue("NAAAAAH");
    }
}
