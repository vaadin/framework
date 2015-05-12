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
package com.vaadin.tests.components.ui;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.UIElement;
import com.vaadin.tests.components.button.ButtonClick;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public class VaadinFinderLocatorUISearchTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return ButtonClick.class;
    }

    @Test
    public void getUIElementTest() {
        openTestURL();
        UIElement ui = $(UIElement.class).first();
        Assert.assertNotNull("Couldn't find the UI Element on the page", ui);
    }
}
