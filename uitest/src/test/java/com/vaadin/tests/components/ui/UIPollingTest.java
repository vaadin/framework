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
package com.vaadin.tests.components.ui;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UIPollingTest extends MultiBrowserTest {

    @Test
    public void testPolling() throws Exception {
        openTestURL();
        getTextField().setValue("500");
        sleep(2000);
        /* Ensure polling has taken place */
        Assert.assertTrue("Page does not contain the given text",
                driver.getPageSource().contains("2. 1000ms has passed"));
        getTextField().setValue("-1");
        sleep(2000);
        /* Ensure polling has stopped */
        Assert.assertFalse("Page contains the given text",
                driver.getPageSource().contains("20. 10000ms has passed"));
    }

    public TextFieldElement getTextField() {
        return $(TextFieldElement.class).first();
    }
}
