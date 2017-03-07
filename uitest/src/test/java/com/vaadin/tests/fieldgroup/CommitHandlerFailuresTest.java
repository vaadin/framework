/*
 * Copyright 2000-2016 Vaadin Ltd.
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
