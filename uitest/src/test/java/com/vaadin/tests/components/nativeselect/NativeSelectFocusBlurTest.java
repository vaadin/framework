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
import org.openqa.selenium.Keys;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class NativeSelectFocusBlurTest extends MultiBrowserTest {

    @Test
    public void focusBlurEvents() {
        openTestURL();

        NativeSelectElement nativeSelect = $(NativeSelectElement.class).first();
        nativeSelect.click();

        // Focus event is fired
        Assert.assertTrue(logContainsText("1. Focus Event"));

        List<TestBenchElement> options = nativeSelect.getOptions();
        options.get(1).click();
        // No any new event
        Assert.assertFalse(logContainsText("2."));

        // click on log label => blur
        $(LabelElement.class).first().click();
        // blur event is fired
        Assert.assertTrue(logContainsText("2. Blur Event"));

        nativeSelect.click();
        // Focus event is fired
        Assert.assertTrue(logContainsText("3. Focus Event"));

        nativeSelect.sendKeys(Keys.ARROW_UP, Keys.ENTER);
        // No any new event
        Assert.assertFalse(logContainsText("4."));
    }
}
