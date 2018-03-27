/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class EncodeResultDisplayTest extends SingleBrowserTest {
    @Test
    public void testEncodeResults() {
        openTestURL();

        int logRow = 0;

        assertEquals("Void: null", getLogRow(logRow++));
        assertEquals("SimpleTestBean: {\"value\":5}", getLogRow(logRow++));
        assertEquals("List: [\"Three\",\"Four\"]", getLogRow(logRow++));
        assertEquals("String[]: [\"One\",\"Two\"]", getLogRow(logRow++));
        assertEquals("Double: 2.2", getLogRow(logRow++));
        // PhantomJS likes to add a couple of extra decimals
        assertTrue(getLogRow(logRow++).startsWith("Float: 1.1"));
        assertEquals("Long: 2147483648", getLogRow(logRow++));
        assertEquals("Integer: 3", getLogRow(logRow++));
        assertEquals("Byte: 1", getLogRow(logRow++));
        assertEquals("Character: \"v\"", getLogRow(logRow++));
        assertEquals("String: \"My string\"", getLogRow(logRow++));
    }
}
