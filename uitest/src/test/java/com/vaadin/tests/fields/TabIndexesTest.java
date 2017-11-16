package com.vaadin.tests.fields;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.AbstractComponentElement;
import com.vaadin.testbench.elements.AccordionElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.InlineDateFieldElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.RichTextAreaElement;
import com.vaadin.testbench.elements.SliderElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.testbench.elements.TwinColSelectElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TabIndexesTest extends SingleBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void testTabIndexesSetToZero() {
        // clicked by default
        assertLogText("1. Setting tab indexes to 0");
        for (WebElement element : getFocusElements()) {
            assertTabIndex("0", element);
        }
    }

    @Test
    public void testTabIndexesSetToOne() {
        setTabIndexesTo("1");
        for (WebElement element : getFocusElements()) {
            assertTabIndex("1", element);
        }
    }

    @Test
    public void testTabIndexesSetToOneThroughN() {
        setTabIndexesTo("1..N");
        int counter = 0;
        for (WebElement element : getFocusElements()) {
            ++counter;
            assertTabIndex(String.valueOf(counter), element);
        }
    }

    @Test
    public void testTabIndexesSetToNThroughOne() {
        setTabIndexesTo("N..1");
        List<WebElement> fieldElements = getFocusElements();
        int counter = fieldElements.size();
        for (WebElement element : fieldElements) {
            assertTabIndex(String.valueOf(counter), element);
            --counter;
        }
    }

    private void setTabIndexesTo(String expected) {
        String caption = String.format("Set %stab indexes to %s",
                (expected.contains("N") ? "" : "all "), expected);
        $(ButtonElement.class).caption(caption).first().click();
        assertLogText("2. Setting tab indexes to " + expected);
    }

    private void assertLogText(String expected) {
        assertEquals("Unexpected log contents,", expected, getLogRow(0));
    }

    private void assertTabIndex(String expected, WebElement element) {
        assertEquals(
                "Unexpected tab index for element "
                        + element.getAttribute("outerHTML"),
                expected, element.getAttribute("tabIndex"));
    }

    private List<WebElement> getFocusElements() {
        List<WebElement> focusElements = new ArrayList<>();

        focusElements.add($(ComboBoxElement.class).first().getInputField());
        focusElements
                .add($(NativeSelectElement.class).first().getSelectElement());
        focusElements
                .add($(ListSelectElement.class).first().getSelectElement());
        focusElements.add($(TextFieldElement.class).first());
        focusElements.add($(DateFieldElement.class).first().getInputElement());
        focusElements
                .add($(InlineDateFieldElement.class).first().getFocusElement());
        focusElements.add($(TreeGridElement.class).first());
        focusElements
                .add($(TwinColSelectElement.class).first().getOptionsElement());
        focusElements.add($(PasswordFieldElement.class).first());
        focusElements.add($(TextAreaElement.class).first());
        focusElements
                .add($(RichTextAreaElement.class).first().getEditorIframe());
        focusElements.add($(CheckBoxElement.class).first().getInputElement());
        focusElements.add($(SliderElement.class).first());
        focusElements.add($(MenuBarElement.class).first());
        TabSheetElement tabsheet = $(TabSheetElement.class).first();
        focusElements.add(tabsheet
                .findElement(By.className("v-tabsheet-tabitemcell-selected")));
        AccordionElement accordion = $(AccordionElement.class).first();
        focusElements.add(
                accordion.findElement(By.className("v-accordion-item-open")));

        List<AbstractComponentElement> components = $(
                VerticalLayoutElement.class).id(TabIndexes.FIELD_CONTAINER_ID)
                        .$(AbstractComponentElement.class).all();
        // the Labels within the TabSheet and Accordion are left out of the
        // index handling
        assertEquals(components.size(), focusElements.size() + 2);
        return focusElements;
    }

}
