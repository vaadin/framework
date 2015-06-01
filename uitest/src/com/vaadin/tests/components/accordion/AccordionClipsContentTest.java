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
package com.vaadin.tests.components.accordion;

import org.junit.Test;

import com.vaadin.testbench.elements.NativeButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class AccordionClipsContentTest extends MultiBrowserTest {
    @Override
    protected Class<?> getUIClass() {
        return AccordionTest.class;
    }

    @Test
    public void testAccordionClipsContent() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Component container features",
                "Add component", "NativeButton", "auto x auto");

        $(NativeButtonElement.class).first().click();

        // Give the button time to pop back up in IE8.
        // If this sleep causes issues, next best thing is to click outside the
        // button to remove focus - needs new screenshots for all browsers.
        Thread.sleep(10);

        compareScreen("button-clicked");
    }
}
