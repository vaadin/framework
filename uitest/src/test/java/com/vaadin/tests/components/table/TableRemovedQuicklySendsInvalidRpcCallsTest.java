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
package com.vaadin.tests.components.table;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableRemovedQuicklySendsInvalidRpcCallsTest extends
        MultiBrowserTest {

    private static final String BUTTON_ID = TableRemovedQuicklySendsInvalidRpcCalls.BUTTON_ID;
    private static final String FAILURE_CAPTION = TableRemovedQuicklySendsInvalidRpcCalls.FAILURE_CAPTION;
    private static final String SUCCESS_CAPTION = TableRemovedQuicklySendsInvalidRpcCalls.SUCCESS_CAPTION;

    @Test
    public void test() throws Exception {
        setDebug(true);
        openTestURL();

        assertFalse("Test started with the error present.", button().getText()
                .equals(FAILURE_CAPTION));
        assertFalse("Test jumped the gun.",
                button().getText().equals(SUCCESS_CAPTION));

        button().click();
        Thread.sleep(5000);

        assertFalse("Test failed after trying to trigger the error.", button()
                .getText().equals(FAILURE_CAPTION));
        assertTrue("Test didn't end up in correct success state.", button()
                .getText().equals(SUCCESS_CAPTION));
    }

    private WebElement button() {
        return vaadinElementById(BUTTON_ID);
    }
}
