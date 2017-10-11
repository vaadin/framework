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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTestPhantomJS2;

public class TimingInfoReportedTest extends SingleBrowserTestPhantomJS2 {

    @Test
    public void ensureTimingsAvailable() {
        openTestURL();
        assertEquals("2. Timings ok", getLogRow(0));
        $(ButtonElement.class).first().click();
        assertEquals("4. Timings ok", getLogRow(0));
    }
}
