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

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UIAccessExceptionHandlingTest extends MultiBrowserTest {

    @Test
    public void testExceptionHandlingOnUIAccess() throws Exception {
        openTestURL();
        $(ButtonElement.class).first().click();
        assertLogTexts(
                "1. Exception caught on get: java.util.concurrent.ExecutionException",
                "0. Exception caught on execution with ConnectorErrorEvent : java.util.concurrent.ExecutionException");

        $(ButtonElement.class).get(1).click();
        assertLogTexts(
                "1. Exception caught on get: java.util.concurrent.ExecutionException",
                "0. Exception caught on execution with ErrorEvent : java.util.concurrent.ExecutionException");

        $(ButtonElement.class).get(2).click();
        assertLogTexts(
                "1. Exception caught on get: java.util.concurrent.ExecutionException",
                "0. Exception caught on execution with ConnectorErrorEvent : java.util.concurrent.ExecutionException");
    }

    private void assertLogTexts(String first, String second) {
        assertLogText(0, first);
        assertLogText(1, second);
    }

    private void assertLogText(int index, String expected) {
        Assert.assertEquals("Unexpected log contents,", expected,
                getLogRow(index));
    }
}
