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
package com.vaadin.tests.elements.splitpanel;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.HorizontalSplitPanelElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.VerticalSplitPanelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * This class contains tests for checking that the methods getFirstComponent()
 * and getSecondComponent() of AbstractSplitPanelElement return the correct
 * components also when the split panel only has a second component. See #14073
 * and #14075.
 *
 */
public class SplitPanelComponentsTest extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void getSecondInHorizontalSplitPanel() throws Exception {
        HorizontalSplitPanelElement sp = $(HorizontalSplitPanelElement.class)
                .first();
        LabelElement label = sp.getSecondComponent(LabelElement.class);
        String labelText = label.getText();
        assertTrue("The second component of the split panel should be a label"
                + " containing the text 'Label 1.2'. Actual text: " + labelText,
                "Label 1.2".equals(labelText));
    }

    @Test
    public void getSecondInVerticalSplitPanel() throws Exception {
        VerticalSplitPanelElement sp = $(VerticalSplitPanelElement.class)
                .first();
        LabelElement label = sp.getSecondComponent(LabelElement.class);
        String labelText = label.getText();
        assertTrue("The second component of the split panel should be a label"
                + " containing the text 'Label 2.2'. Actual text: " + labelText,
                "Label 2.2".equals(labelText));
    }

    @Test
    public void getFirstInHorizontalSplitPanel() throws Exception {
        HorizontalSplitPanelElement sp = $(HorizontalSplitPanelElement.class)
                .first();
        LabelElement label = null;
        // There is no first component, so allow an exception.
        try {
            label = sp.getFirstComponent(LabelElement.class);
        } catch (Exception e) {
        }
        String labelText = label == null ? "" : label.getText();
        assertTrue(
                "The split panel should not have a first component. Found a label with"
                        + " text " + labelText,
                label == null);
    }

    @Test
    public void getFirstInVerticalSplitPanel() throws Exception {
        VerticalSplitPanelElement sp = $(VerticalSplitPanelElement.class)
                .first();
        LabelElement label = null;
        // There is no first component, so allow an exception.
        try {
            label = sp.getFirstComponent(LabelElement.class);
        } catch (Exception e) {
        }
        String labelText = label == null ? "" : label.getText();
        assertTrue(
                "The split panel should not have a first component. Found a label with"
                        + " text " + labelText,
                label == null);
    }

    @Test
    public void getElementsInSplitPanelWithBothComponents() throws Exception {
        // This test is for regression checking - getFirst and getSecond
        // should work also when the split panel has both components.
        HorizontalSplitPanelElement sp = $(HorizontalSplitPanelElement.class)
                .get(1);
        LabelElement label1 = sp.getFirstComponent(LabelElement.class);
        String label1Text = label1.getText();
        assertTrue("The first component of the split panel should be a label"
                + " containing the text 'Label 3.1'. Actual text: "
                + label1Text, "Label 3.1".equals(label1Text));
        LabelElement label2 = sp.getSecondComponent(LabelElement.class);
        String label2Text = label2.getText();
        assertTrue("The second component of the split panel should be a label"
                + " containing the text 'Label 3.2'. Actual text: "
                + label2Text, "Label 3.2".equals(label2Text));
    }

    @Test
    public void getFirstInSplitPanelWithFirstComponent() throws Exception {
        // This test is for regression checking - getFirst should also work
        // in a tab sheet with only the second component.
        VerticalSplitPanelElement sp = $(VerticalSplitPanelElement.class)
                .get(1);
        ButtonElement button = sp.getFirstComponent(ButtonElement.class);
        String buttonText = button.getText();
        assertTrue(
                "The first component of the split panel should be a button labeled \"Button\"."
                        + " Actual label: " + buttonText,
                "Button".equals(buttonText));
    }

    @Test
    public void getSecondInSplitPanelWithFirstComponent() throws Exception {
        // This test is for regression checking - getSecond should not return a
        // non-null element in a tab sheet with only the second component.
        VerticalSplitPanelElement sp = $(VerticalSplitPanelElement.class)
                .get(1);
        ButtonElement button = null;
        // There is no second component, so allow an exception.
        try {
            button = sp.getSecondComponent(ButtonElement.class);
        } catch (Exception e) {
        }
        String buttonText = button == null ? "" : button.getText();
        assertTrue(
                "The split panel should not have a second component. Found a button with"
                        + " text " + buttonText,
                button == null);
    }
}