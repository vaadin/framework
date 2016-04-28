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
package com.vaadin.tests.applicationservlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UIProviderInitParameterTest extends MultiBrowserTest {

    @Override
    protected void openTestURL(String... parameters) {
        driver.get(getTestUrl());
    }

    @Override
    protected String getDeploymentPath() {
        return "/uiprovider";
    }

    @Test
    public void testDefault() {
        // Test that UI parameter is used by default
        openTestURL();

        List<LabelElement> labels = $(LabelElement.class).all();
        assertTrue("unexpected amount of labels", labels.size() > 2);

        LabelElement label = labels.get(labels.size() - 1);
        String message = "Tests whether the testing server is run with assertions enabled.";
        assertEquals("unexpected text found", message, label.getText());
    }

    @Test
    public void testExtended() {
        // Test that UIProvider parameter is more important than UI parameter
        driver.get(getTestUrl().replace("uiprovider", "uiprovider/test"));

        LabelElement label = $(LabelElement.class).first();
        String message = "Test for basic JavaScript component functionality.";
        assertEquals("unexpected text found", message, label.getText());
    }

}
