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
package com.vaadin.tests.components.javascriptcomponent;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.AbstractJavaScriptComponentElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Re-implementation of Javascript Resize Listener TB2 Test in TB4.
 */
@TestCategory("javascript")
public class JavaScriptResizeListenerTest extends MultiBrowserTest {

    @Test
    public void testResizeListener() throws InterruptedException {
        openTestURL();

        // Get handles to relevant elements in page
        AbstractJavaScriptComponentElement jsComponent = $(
                AbstractJavaScriptComponentElement.class).first();

        ButtonElement sizeToggleButton = $(ButtonElement.class).first();

        CheckBoxElement listenerToggleBox = $(CheckBoxElement.class).first();

        // Make sure we're initialized correctly
        assertEquals("Initial state", jsComponent.getText());

        // Try to use the toggle button - nothing should happen
        sizeToggleButton.click();
        Thread.sleep(1000);
        assertEquals("Initial state", jsComponent.getText());

        // Enable the JavaScript resize listener
        listenerToggleBox.click();

        // Vaadin doesn't do a server round-trip here, which means that
        // waitForVaadin will fail to Do The Right Thing. Instead, we'll have to
        // wait for a bit before resuming execution
        Thread.sleep(2500);

        // The listener should change the text to reflect current component size
        sizeToggleButton.click();
        Thread.sleep(1000);
        assertEquals("Current size is 100 x 100", jsComponent.getText());

        // Click button to change size
        sizeToggleButton.click();
        Thread.sleep(1000);
        assertEquals("Current size is 200 x 50", jsComponent.getText());

        // Click it again to revert to previous state
        sizeToggleButton.click();
        Thread.sleep(1000);
        assertEquals("Current size is 100 x 100", jsComponent.getText());

        // Disable the listener
        listenerToggleBox.click();

        // Again, we'll need to sleep for a bit due to waitForVaadin not doing
        // the right thing
        Thread.sleep(2500);
        assertEquals("Listener disabled", jsComponent.getText());

        // Check that nothing happens when clicking the button again
        sizeToggleButton.click();
        Thread.sleep(1000);
        assertEquals("Listener disabled", jsComponent.getText());

    }

}
