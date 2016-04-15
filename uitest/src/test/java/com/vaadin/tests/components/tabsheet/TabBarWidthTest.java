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
package com.vaadin.tests.components.tabsheet;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class TabBarWidthTest extends MultiBrowserTest {

    @Test
    public void testWidths() throws Exception {
        openTestURL();

        // Initial rendering.
        compareScreen("tab-bar-width-init");

        // Remove all widths.
        vaadinElementById("toggleWidths").click();
        compareScreen("tab-bar-width-undefined");

        // Restore all widths. This should restore the rendering to the same
        // point as the initial rendering.
        vaadinElementById("toggleWidths").click();
        compareScreen("tab-bar-width-restored");
    }

}
