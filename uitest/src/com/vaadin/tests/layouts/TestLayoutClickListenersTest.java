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
package com.vaadin.tests.layouts;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.AbsoluteLayoutElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CssLayoutElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests LayoutClickListener on different layouts.
 * 
 * @author Vaadin Ltd
 */
public class TestLayoutClickListenersTest extends MultiBrowserTest {

    @Before
    public void before() {
        openTestURL();
    }

    @Test
    public void clickInGridLayout() {
        GridLayoutElement layout = $(GridLayoutElement.class).first();

        // click on a label
        layout.$(LabelElement.class).first().click();
        assertLogText("GridLayout 1st child clicked",
                "1. GridLayout: left click on This is label 1");

        // click on a text field
        layout.$(TextFieldElement.class).get(1).click();
        assertLogText("GridLayout 5th child clicked",
                "2. GridLayout: left click on This is tf5");

        // click on the layout body (not any component inside the layout)
        layout.click(130, 41);
        assertLogText("GridLayout body clicked",
                "3. GridLayout: left click on <none>");
    }

    @Test
    public void clickInVerticalLayout() {
        VerticalLayoutElement layout = $(VerticalLayoutElement.class).get(4);

        // click on a text field
        layout.$(TextFieldElement.class).get(1).click();
        assertLogText("VerticalLayout 6th child clicked",
                "1. VerticalLayout: left click on This is tf6");

        // click on a label
        layout.$(LabelElement.class).get(3).click();
        assertLogText("VerticalLayout 4th child clicked",
                "2. VerticalLayout: left click on This is label 3");
    }

    @Test
    public void clickInAbsoluteLayout() {
        AbsoluteLayoutElement layout = $(AbsoluteLayoutElement.class).first();

        // click on a button that has its own ClickListener (should be ignored
        // by the LayoutClickListener)
        layout.$(ButtonElement.class).first().click();
        assertLogText("A button with a ClickListener clicked",
                "1. Button A button with its own click listener was clicked");

        // click on a text field's caption
        layout.$(TextFieldElement.class).first().click();
        assertLogText("AbsoluteLayout 1st child was clicked",
                "2. AbsoluteLayout: left click on This is its caption");
    }

    @Test
    public void clickInCSSLayout() {
        CssLayoutElement layout = $(CssLayoutElement.class).first();

        // click on a text field's caption
        layout.$(TextFieldElement.class).first().click();
        assertLogText("CSSLayout 1st child clicked",
                "1. CSSLayout: left click on This is its caption");

        // click on a button that has its own ClickListener (should be ignored
        // by the LayoutClickListener)
        layout.$(ButtonElement.class).first().click();
        assertLogText("Abutton with a ClickListener was clicked",
                "2. Button A button with its own click listener was clicked");
    }

    @Test
    public void dragInGridLayout() {
        GridLayoutElement layout = $(GridLayoutElement.class).first();

        // Drag inside the first label in this layout
        new Actions(getDriver())
                .moveToElement(layout.$(LabelElement.class).first(), 40, 8)
                .clickAndHold().moveByOffset(-20, 0).release().perform();
        assertLogText("Mouse dragged in GridLayout",
                "1. GridLayout: left click on This is label 1");

        // Drag from the third label to a text field in this layout
        new Actions(getDriver())
                .moveToElement(layout.$(LabelElement.class).get(2), 40, 8)
                .clickAndHold()
                .moveToElement(layout.$(TextFieldElement.class).get(3), 46, 33)
                .release().perform();
        assertLogText("Expected the drag to be ignored between elements",
                "1. GridLayout: left click on This is label 1");
    }

    @Test
    public void dragInVerticalLayout() {
        VerticalLayoutElement layout = $(VerticalLayoutElement.class).get(4);

        // Drag inside the first text field
        new Actions(getDriver())
                .moveToElement(layout.$(TextFieldElement.class).first(), 25, 9)
                .clickAndHold().moveByOffset(-20, 0).release().perform();
        assertLogText("Mouse dragged in VerticalLayout",
                "1. VerticalLayout: left click on This is tf5");

        // Drag from a caption to its text field
        new Actions(getDriver())
                .moveToElement(layout.$(TextFieldElement.class).get(4), 28, 11)
                .clickAndHold()
                .moveToElement(layout.$(TextFieldElement.class).get(4), 39, 30)
                .release().perform();
        assertLogText("Expected the drag to be ignored between elements",
                "1. VerticalLayout: left click on This is tf5");
    }

    @Test
    public void dragInAbsoluteLayout() {
        AbsoluteLayoutElement layout = $(AbsoluteLayoutElement.class).first();

        // Drag inside the first text field's caption
        new Actions(getDriver())
                .moveToElement(layout.$(TextFieldElement.class).first(), 21, 9)
                .clickAndHold().moveByOffset(-10, 0).release().perform();
        assertLogText("Mouse dragged in AbsoluteLayout",
                "1. AbsoluteLayout: left click on This is its caption");

        // Drag from a text field to another text field
        new Actions(getDriver())
                .moveToElement(layout.$(TextFieldElement.class).get(1), 54, 7)
                .clickAndHold()
                .moveToElement(layout.$(TextFieldElement.class).first(), 52, 10)
                .release().perform();
        assertLogText("Expected the drag to be ignored between elements",
                "1. AbsoluteLayout: left click on This is its caption");
    }

    @Test
    public void dragInCSSLayout() {
        CssLayoutElement layout = $(CssLayoutElement.class).first();

        // Drag inside the first text field's caption
        new Actions(getDriver())
                .moveToElement(layout.$(TextFieldElement.class).first(), 51, 7)
                .clickAndHold().moveByOffset(-20, 0).release().perform();
        assertLogText("Mouse dragged in CSSLayout",
                "1. CSSLayout: left click on This is its caption");

        // Drag from the first text field to the second text field
        new Actions(getDriver())
                .moveToElement(layout.$(TextFieldElement.class).first(), 51, 27)
                .clickAndHold()
                .moveToElement(layout.$(TextFieldElement.class).get(1), 51, 27)
                .release().perform();
        assertLogText("Expected the drag to be ignored between elements",
                "1. CSSLayout: left click on This is its caption");
    }

    private void assertLogText(String message, String expected) {
        String actual = $(LabelElement.class).first().getText();
        Assert.assertEquals(message, expected, actual);
    }
}
