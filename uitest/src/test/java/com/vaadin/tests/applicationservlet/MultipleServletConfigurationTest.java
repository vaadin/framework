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
package com.vaadin.tests.applicationservlet;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MultipleServletConfigurationTest extends MultiBrowserTest {

    @Override
    protected void closeApplication() {
    }

    @Test
    public void testMultipleServletConfiguration() throws Exception {
        getDriver().get(getBaseURL() + "/embed1");
        assertLabelText("Verify that Button HTML rendering works");
        getDriver().get(getBaseURL() + "/embed2");
        assertLabelText(
                "Margins inside labels should not be allowed to collapse out of the label as it causes problems with layotus measuring the label.");
        getDriver().get(getBaseURL() + "/embed1");
        assertLabelText("Verify that Button HTML rendering works");
    }

    private void assertLabelText(String expected) {
        Assert.assertEquals("Unexpected label text,", expected,
                $(LabelElement.class).first().getText());
    }
}
