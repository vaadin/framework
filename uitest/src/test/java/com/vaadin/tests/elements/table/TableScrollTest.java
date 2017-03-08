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
package com.vaadin.tests.elements.table;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableScrollTest extends MultiBrowserTest {
    private static final int SCROLL_VALUE = 200;

    @Test
    public void testScrollLeft() {
        openTestURL();
        TableElement table = $(TableElement.class).get(0);
        table.scrollLeft(SCROLL_VALUE);
        Assert.assertEquals(SCROLL_VALUE, getScrollLeftValue(table));
    }

    @Test
    public void testScrollTop() {
        openTestURL();
        TableElement table = $(TableElement.class).get(0);
        table.scroll(SCROLL_VALUE);
        Assert.assertEquals(SCROLL_VALUE, getScrollTopValue(table));
    }

    // helper functions
    private int getScrollTopValue(WebElement elem) {
        JavascriptExecutor js = getCommandExecutor();
        String jsScript = "return arguments[0].getElementsByClassName(\"v-scrollable\")[0].scrollTop;";
        Long scrollTop = (Long) js.executeScript(jsScript, elem);
        return scrollTop.intValue();
    }

    private int getScrollLeftValue(WebElement elem) {
        JavascriptExecutor js = getCommandExecutor();
        String jsScript = "return arguments[0].getElementsByClassName(\"v-scrollable\")[0].scrollLeft;";
        Long scrollLeft = (Long) js.executeScript(jsScript, elem);
        return scrollLeft.intValue();
    }
}
