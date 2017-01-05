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
package com.vaadin.tests.applicationservlet;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class ServiceInitListenersTest extends SingleBrowserTest {

    @Test
    public void testServiceInitListenerTriggered() {
        openTestURL();

        Assert.assertNotEquals(getLogRow(0), 0, extractCount(getLogRow(0)));
        Assert.assertNotEquals(getLogRow(1), 0, extractCount(getLogRow(1)));
    }

    private int extractCount(String logRow) {
        // Assuming row pattern is "label: 1"
        String substring = logRow.replaceAll("[^:]*:\\s*", "");
        return Integer.parseInt(substring);
    }

}
