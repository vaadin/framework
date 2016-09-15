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
package com.vaadin.tests.components.abstractfield;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.SingleBrowserTest;

public abstract class ConfigurableAbstractFieldTest extends SingleBrowserTest {

    private static final org.openqa.selenium.By REQUIRED_BY = By
            .className("v-required");
    private static final org.openqa.selenium.By ERROR_INDICATOR_BY = By
            .className("v-errorindicator");

    @Test
    public void requiredIndicator() {
        openTestURL();

        assertNoRequiredIndicator();
        selectMenuPath("Component", "State", "Required");
        assertRequiredIndicator();
        selectMenuPath("Component", "State", "Required");
        assertNoRequiredIndicator();
    }

    @Test
    public void errorIndicator() {
        openTestURL();

        assertNoErrorIndicator();
        selectMenuPath("Component", "State", "Error indicator");
        assertErrorIndicator();
        selectMenuPath("Component", "State", "Error indicator");
        assertNoErrorIndicator();
    }

    private void assertRequiredIndicator() {
        assertElementPresent(REQUIRED_BY);
    }

    private void assertNoRequiredIndicator() {
        assertElementNotPresent(REQUIRED_BY);
    }

    private void assertErrorIndicator() {
        assertElementPresent(ERROR_INDICATOR_BY);
    }

    private void assertNoErrorIndicator() {
        assertElementNotPresent(ERROR_INDICATOR_BY);
    }
}
