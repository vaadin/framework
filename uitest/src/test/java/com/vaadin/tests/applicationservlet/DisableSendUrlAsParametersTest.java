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
package com.vaadin.tests.applicationservlet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class DisableSendUrlAsParametersTest extends SingleBrowserTest {

    @Test
    public void testInitLocation() {
        openTestURL();

        String logRow = getLogRow(0);

        assertEquals(
                "1. Init location exception: Location is not available as the sendUrlsAsParameters parameter is configured as false",
                logRow);
    }
}
