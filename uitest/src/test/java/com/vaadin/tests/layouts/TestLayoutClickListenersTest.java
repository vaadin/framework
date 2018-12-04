package com.vaadin.tests.layouts;

import static org.junit.Assert.assertEquals;

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
        layout.click(getXOffset(layout, 130), getYOffset(layout, 41));
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
        LabelElement label = layout.$(LabelElement.class).first();
        new Actions(getDriver())
                .moveToElement(label, getXOffset(label, 40),
                        getYOffset(label, 8))
                .clickAndHold().moveByOffset(-20, 0).release().perform();
        assertLogText("Mouse dragged in GridLayout",
                "1. GridLayout: left click on This is label 1");

        // Drag from the third label to a text field in this layout
        label = layout.$(LabelElement.class).get(2);
        TextFieldElement textField = layout.$(TextFieldElement.class).get(3);
        new Actions(getDriver())
                .moveToElement(label, getXOffset(label, 40),
                        getYOffset(label, 8))
                .clickAndHold().moveToElement(textField,
                        getXOffset(textField, 46), getYOffset(textField, 33))
                .release().perform();
        assertLogText("Expected the drag to be ignored between elements",
                "1. GridLayout: left click on This is label 1");
    }

    @Test
    public void dragInVerticalLayout() {
        VerticalLayoutElement layout = $(VerticalLayoutElement.class).get(4);

        // Drag inside the first text field
        TextFieldElement textField = layout.$(TextFieldElement.class).first();
        new Actions(getDriver())
                .moveToElement(textField, getXOffset(textField, 25),
                        getYOffset(textField, 9))
                .clickAndHold().moveByOffset(-20, 0).release().perform();
        assertLogText("Mouse dragged in VerticalLayout",
                "1. VerticalLayout: left click on This is tf5");

        // Drag from a caption to its text field
        textField = layout.$(TextFieldElement.class).get(4);
        new Actions(getDriver())
                .moveToElement(textField, getXOffset(textField, 28),
                        getYOffset(textField, 11))
                .clickAndHold().moveToElement(textField,
                        getXOffset(textField, 39), getYOffset(textField, 30))
                .release().perform();
        assertLogText("Expected the drag to be ignored between elements",
                "1. VerticalLayout: left click on This is tf5");
    }

    @Test
    public void dragInAbsoluteLayout() {
        AbsoluteLayoutElement layout = $(AbsoluteLayoutElement.class).first();

        // Drag inside the first text field's caption
        TextFieldElement firstTextField = layout.$(TextFieldElement.class)
                .first();
        new Actions(getDriver())
                .moveToElement(firstTextField, getXOffset(firstTextField, 21),
                        getYOffset(firstTextField, 9))
                .clickAndHold().moveByOffset(-10, 0).release().perform();
        assertLogText("Mouse dragged in AbsoluteLayout",
                "1. AbsoluteLayout: left click on This is its caption");

        // Drag from a text field to another text field
        TextFieldElement otherTextField = layout.$(TextFieldElement.class)
                .get(1);
        new Actions(getDriver())
                .moveToElement(otherTextField, getXOffset(otherTextField, 54),
                        getYOffset(otherTextField, 7))
                .clickAndHold()
                .moveToElement(firstTextField, getXOffset(firstTextField, 52),
                        getYOffset(firstTextField, 10))
                .release().perform();
        assertLogText("Expected the drag to be ignored between elements",
                "1. AbsoluteLayout: left click on This is its caption");
    }

    @Test
    public void dragInCSSLayout() {
        CssLayoutElement layout = $(CssLayoutElement.class).first();

        // Drag inside the first text field's caption
        TextFieldElement firstTextField = layout.$(TextFieldElement.class)
                .first();
        new Actions(getDriver())
                .moveToElement(firstTextField, getXOffset(firstTextField, 51),
                        getYOffset(firstTextField, 7))
                .clickAndHold().moveByOffset(-20, 0).release().perform();
        assertLogText("Mouse dragged in CSSLayout",
                "1. CSSLayout: left click on This is its caption");

        // Drag from the first text field to the second text field
        TextFieldElement otherTextField = layout.$(TextFieldElement.class)
                .get(1);
        new Actions(getDriver())
                .moveToElement(firstTextField, getXOffset(firstTextField, 51),
                        getYOffset(firstTextField, 27))
                .clickAndHold()
                .moveToElement(otherTextField, getXOffset(otherTextField, 51),
                        getYOffset(otherTextField, 27))
                .release().perform();
        assertLogText("Expected the drag to be ignored between elements",
                "1. CSSLayout: left click on This is its caption");
    }

    private void assertLogText(String message, String expected) {
        String actual = $(LabelElement.class).first().getText();
        assertEquals(message, expected, actual);
    }
}
