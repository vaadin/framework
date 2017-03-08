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

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UIInitBrowserDetailsTest extends MultiBrowserTest {

    @Test
    public void testBrowserDetails() throws Exception {
        openTestURL();
        /* location */
        compareRequestAndBrowserValue("v-loc", "location", "null");
        /* browser window width */
        compareRequestAndBrowserValue("v-cw", "browser window width", "-1");
        /* browser window height */
        compareRequestAndBrowserValue("v-ch", "browser window height", "-1");
        /* screen width */
        compareRequestAndBrowserValue("v-sw", "screen width", "-1");
        /* screen height */
        compareRequestAndBrowserValue("v-sh", "screen height", "-1");
        /* timezone offset */
        assertTextNotNull("timezone offset");
        /* raw timezone offset */
        assertTextNotNull("raw timezone offset");
        /* dst saving */
        assertTextNotNull("dst saving");
        /* dst in effect */
        assertTextNotNull("dst in effect");
        /* current date */
        assertTextNotNull("v-curdate");
        assertTextNotNull("current date");
    }

    private void compareRequestAndBrowserValue(String paramName,
            String browserName, String errorValue) {
        assertTextNotEquals(browserName, errorValue);
        Assert.assertEquals(
                String.format("Browser and request values differ in '%s',",
                        browserName),
                getLabelText(paramName), getLabelText(browserName));
    }

    private String getLabelText(String id) {
        return $(LabelElement.class).id(id).getText();
    }

    private void assertTextNotNull(String id) {
        assertTextNotEquals(id, "null");
    }

    private void assertTextNotEquals(String id, String notExpected) {
        String actual = getLabelText(id);
        Assert.assertNotEquals(String.format("Unexpected value for '%s'", id),
                notExpected, actual);
    }
}
