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
package com.vaadin.tests.serialization;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class EncodeResultDisplayTest extends SingleBrowserTest {
    @Test
    public void testEncodeResults() {
        openTestURL();

        int logRow = 0;

        Assert.assertEquals("Void: null", getLogRow(logRow++));
        Assert.assertEquals("SimpleTestBean: {\"value\":5}",
                getLogRow(logRow++));
        Assert.assertEquals("List: [\"Three\",\"Four\"]", getLogRow(logRow++));
        Assert.assertEquals("String[]: [\"One\",\"Two\"]", getLogRow(logRow++));
        Assert.assertEquals("Double: 2.2", getLogRow(logRow++));
        // PhantomJS likes to add a couple of extra decimals
        Assert.assertTrue(getLogRow(logRow++).startsWith("Float: 1.1"));
        Assert.assertEquals("Long: 2147483648", getLogRow(logRow++));
        Assert.assertEquals("Integer: 3", getLogRow(logRow++));
        Assert.assertEquals("Byte: 1", getLogRow(logRow++));
        Assert.assertEquals("Character: \"v\"", getLogRow(logRow++));
        Assert.assertEquals("String: \"My string\"", getLogRow(logRow++));
    }
}
