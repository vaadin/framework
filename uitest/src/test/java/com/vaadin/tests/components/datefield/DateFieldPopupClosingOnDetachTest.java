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

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.AbstractDateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldPopupClosingOnDetachTest extends MultiBrowserTest {

    @Test
    public void testDateFieldPopupClosingLongClick()
            throws InterruptedException, IOException {
        openTestURL();

        // Open the DateField popup.
        AbstractDateFieldElement df = $(AbstractDateFieldElement.class).first();
        df.findElement(By.tagName("button")).click();

        // Test UI will remove the DateField after 1 second.
        waitForElementNotPresent(By.className("v-datefield"));

        // The popup should be also removed now.
        assertElementNotPresent(By.className("v-datefield-popup"));
    }

}
