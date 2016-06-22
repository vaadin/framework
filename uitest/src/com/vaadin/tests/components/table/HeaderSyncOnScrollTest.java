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
package com.vaadin.tests.components.table;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests Table Footer ClickListener
 * 
 * @author Vaadin Ltd
 */
public class HeaderSyncOnScrollTest extends MultiBrowserTest {

    @Test
    public void testFooter() throws IOException {
        openTestURL();

        $(ButtonElement.class).first().click();

        compareScreen("100pct-no-scrollbar");

        $(ButtonElement.class).get(1).click();

        TableElement first = $(TableElement.class).first();
        first.scrollLeft(200);

        compareScreen("300px-scrolled-right");

        $(ButtonElement.class).get(2).click();

        compareScreen("100pct-no-scrollbar-second");
    }

}
