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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class NativeSelectCaptionGenerationTest extends SingleBrowserTest {

    private static final String[] value = new String[] { "Foo", "Bar", "Baz",
            "Spam", "Eggs" };

    @Test
    public void testDefaultDeclarativeCaptions() {
        openTestURL();
        List<TestBenchElement> options = $(NativeSelectElement.class).first()
                .getOptions();
        for (int i = 0; i < options.size(); ++i) {
            Assert.assertEquals("Captions don't match.", value[i],
                    options.get(i).getText());
        }
    }

    @Test
    public void testToStringCaptions() {
        openTestURL();
        $(ButtonElement.class).caption("toString").first().click();
        List<TestBenchElement> options = $(NativeSelectElement.class).first()
                .getOptions();
        for (int i = 0; i < options.size(); ++i) {
            Assert.assertEquals("Captions don't match.", "Option " + (i + 1),
                    options.get(i).getText());
        }
    }

    @Test
    public void testNumberOnlyCaptions() {
        openTestURL();
        $(ButtonElement.class).caption("Only number").first().click();
        List<TestBenchElement> options = $(NativeSelectElement.class).first()
                .getOptions();
        for (int i = 0; i < options.size(); ++i) {
            Assert.assertEquals("Captions don't match.", "" + (i + 1),
                    options.get(i).getText());
        }
    }

    @Test
    public void testChangeGeneratorToStringAndBackToDeclarative() {
        openTestURL();
        $(ButtonElement.class).caption("toString").first().click();
        $(ButtonElement.class).caption("Declarative").first().click();
        List<TestBenchElement> options = $(NativeSelectElement.class).first()
                .getOptions();
        for (int i = 0; i < options.size(); ++i) {
            Assert.assertEquals("Captions don't match.", value[i],
                    options.get(i).getText());
        }
    }
}
