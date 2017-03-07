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

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ThreadLocalInstancesTest extends MultiBrowserTest {
    @Test
    public void tb2test() throws Exception {
        openTestURL();
        $(ButtonElement.class).first().click();
        assertLogText("1. some app in class init", 15);
        assertLogText("2. null root in class init", 14);
        assertLogText("3. some app in app constructor", 13);
        assertLogText("4. null root in app constructor", 12);
        assertLogText("5. some app in app init", 11);
        assertLogText("6. null root in app init", 10);
        assertLogText("7. some app in root init", 9);
        assertLogText("8. this root in root init", 8);
        assertLogText("9. some app in root paint", 7);
        assertLogText("10. this root in root paint", 6);
        assertLogText("11. null app in background thread", 5);
        assertLogText("12. null root in background thread", 4);
        assertLogText("13. some app in resource handler", 3);
        assertLogText("14. this root in resource handler", 2);
        assertLogText("15. some app in button listener", 1);
        assertLogText("16. this root in button listener", 0);
    }

    private void assertLogText(String expected, int index) {
        Assert.assertEquals("Incorrect log text,", expected, getLogRow(index));
    }
}
