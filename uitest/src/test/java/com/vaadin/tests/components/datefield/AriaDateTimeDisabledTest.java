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
package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class AriaDateTimeDisabledTest extends MultiBrowserTest {

    @Test
    public void verifyAriaDisabledAttributes() {
        openTestURL();

        // Expect aria-disabled="false" on the enabled DateField.
        String ariaDisabled = driver
                .findElement(By
                        .vaadin("/VVerticalLayout[0]/VPopupTimeCalendar[1]#popupButton"))
                .getAttribute("aria-disabled");
        assertEquals("false", ariaDisabled);

        // Expect aria-disabled="true" on the disabled DateField.
        ariaDisabled = driver.findElement(By.cssSelector(".v-disabled button"))
                .getAttribute("aria-disabled");
        assertEquals("true", ariaDisabled);
    }

}
