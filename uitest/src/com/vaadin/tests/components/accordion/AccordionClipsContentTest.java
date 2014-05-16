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

        /*
         * MenuBarElement doesn't have any API, so this part is ugly until
         * #13364 is fixed
         */

        // Component
        vaadinElement("PID_Smenu#item0").click();
        // Component container features
        clickAt("Root/VOverlay[0]/VMenuBar[0]#item3", 136, 8);
        // Add component
        clickAt("Root/VOverlay[1]/VMenuBar[0]#item0", 65, 4);
        // NativeButton
        clickAt("Root/VOverlay[2]/VMenuBar[0]#item1", 86, 2);
        // autoxauto
        vaadinElement("Root/VOverlay[3]/VMenuBar[0]#item0").click();

        $(NativeButtonElement.class).first().click();

        compareScreen("button-clicked");
    }

    private void clickAt(String vaadinLocator, int x, int y) {
        testBenchElement(vaadinElement(vaadinLocator)).click(x, y);
    }
}
