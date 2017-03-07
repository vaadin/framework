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
package com.vaadin.tests.application;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class RefreshStatePreserveTest extends MultiBrowserTest {
    private static String UI_ID_TEXT = "UI id: 0";

    @Test
    public void testPreserveState() throws Exception {
        openTestURL();
        assertCorrectState();
        // URL needs to be different or some browsers don't count it as history
        openTestURL("debug");
        assertCorrectState();
        executeScript("history.back()");
        assertCorrectState();
    }

    private void assertCorrectState() {
        waitForElementPresent(By.className("v-label"));
        LabelElement uiIdLabel = $(LabelElement.class).get(7);
        Assert.assertEquals("Incorrect UI id,", UI_ID_TEXT,
                uiIdLabel.getText());
    }
}
