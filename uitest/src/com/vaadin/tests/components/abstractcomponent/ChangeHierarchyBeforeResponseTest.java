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
package com.vaadin.tests.components.abstractcomponent;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ChangeHierarchyBeforeResponseTest extends SingleBrowserTest {
    @Test
    public void testHierarchyChangeBeforeResponse() {
        openTestURL();

        ButtonElement button = $(ButtonElement.class).first();

        Assert.assertEquals(
                "Button caption should change by its own beforeClientResponse",
                "Add label to layout", button.getText());

        button.click();

        LabelElement label = $(LabelElement.class).all().get(1);

        Assert.assertEquals("Label should have been considered initial twice",
                "Initial count: 2", label.getText());
    }
}
