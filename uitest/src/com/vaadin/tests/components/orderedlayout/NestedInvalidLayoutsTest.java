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
package com.vaadin.tests.components.orderedlayout;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NativeButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class NestedInvalidLayoutsTest extends SingleBrowserTest {

    @Test
    public void ensureCorrectSizes() {
        openTestURL();

        // All Button components should have equal width
        List<Integer> widths = new ArrayList<Integer>();
        List<ButtonElement> all = $(ButtonElement.class).state(
                "primaryStyleName", "v-button").all();
        for (ButtonElement button : all) {
            widths.add(button.getSize().getWidth());
        }
        assertAllEqual(widths);

        // All NativeButton components should have equal height
        List<Integer> heights = new ArrayList<Integer>();
        for (NativeButtonElement button : $(NativeButtonElement.class).all()) {
            heights.add(button.getSize().getHeight());
        }
        assertAllEqual(heights);
    }

    private void assertAllEqual(List<Integer> values) {
        Integer first = values.get(0);
        for (Integer w : values) {
            Assert.assertEquals(first, w);
        }
    }
}
