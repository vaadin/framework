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
package com.vaadin.tests.components.nativebutton;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if coordinates returned by click event on NativeButtons look
 * good. (see #14022)
 * 
 * @author Vaadin Ltd
 */
public class NativeButtonClickTest extends MultiBrowserTest {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.tb3.MultiBrowserTest#getBrowsersToTest()
     */

    @Test
    public void testClickCoordinates() {
        openTestURL();

        clickFirstButton();
        String eventCoordinates = getFirstLabelValue();
        Assert.assertNotEquals("0,0", eventCoordinates);

        clickSecondButton();
        eventCoordinates = getSecondLabelValue();
        Assert.assertNotEquals("0,0", eventCoordinates);
    }

    private void clickFirstButton() {
        ButtonElement button = $(ButtonElement.class).first();
        button.click();
    }

    private void clickSecondButton() {
        ButtonElement button = $(ButtonElement.class).get(1);
        button.click();
    }

    private String getFirstLabelValue() {
        LabelElement label = $(LabelElement.class).get(1);
        return label.getText();
    }

    private String getSecondLabelValue() {
        LabelElement label = $(LabelElement.class).get(2);
        return label.getText();
    }
}
