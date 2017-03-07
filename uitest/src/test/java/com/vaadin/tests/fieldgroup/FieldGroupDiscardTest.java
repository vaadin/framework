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
