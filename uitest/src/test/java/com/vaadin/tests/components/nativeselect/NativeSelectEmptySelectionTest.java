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
package com.vaadin.tests.components.nativeselect;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class NativeSelectEmptySelectionTest extends MultiBrowserTest {

    @Test
    public void checkEmptySelection() {
        openTestURL();

        checkOptions("empty");

        // change the caption
        $(ButtonElement.class).first().click();
        checkOptions("updated");

        // disable empty caption
        $(ButtonElement.class).get(1).click();
        checkOptions(null);

        // enable back
        $(ButtonElement.class).get(2).click();
        checkOptions("updated");
    }

    private void checkOptions(String emptyCaption) {
        NativeSelectElement select = $(NativeSelectElement.class).first();
        Set<String> originalOptions = IntStream.range(1, 50)
                .mapToObj(index -> String.valueOf(index))
                .collect(Collectors.toSet());
        Set<String> options = select.getOptions().stream()
                .map(TestBenchElement::getText).collect(Collectors.toSet());
        if (emptyCaption == null) {
            Assert.assertEquals(49, options.size());
            Assert.assertTrue(options.containsAll(originalOptions));
        } else {
            options.contains(emptyCaption);
            Assert.assertEquals(50, options.size());
            Assert.assertTrue(options.containsAll(originalOptions));
        }
    }
}
