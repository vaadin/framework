package com.vaadin.tests.elements;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.AbstractComponentElement;
import com.vaadin.testbench.elements.AbstractLayoutElement;
import com.vaadin.testbench.elements.AccordionElement;
import com.vaadin.testbench.elements.BrowserFrameElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.testbench.elements.ColorPickerElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.FlashElement;
import com.vaadin.testbench.elements.FormLayoutElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.testbench.elements.HorizontalLayoutElement;
import com.vaadin.testbench.elements.ImageElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.TwinColSelectElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 *
 * Test class which have test methods for all components added in the testUI
 * class. Open TestURL is called only once before tests. Parent class should
 * override protected Class<?> getUIClass() to specify which testUI should be
 * used
 */

public abstract class ElementComponentGetCaptionBaseTest
        extends MultiBrowserTest {
    AbstractLayoutElement mainLayout;

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void getComboboxCaptionTest() {
        ComboBoxElement elem = mainLayout.$(ComboBoxElement.class).get(0);
        testCaption(elem, 0);
    }

    @Test
    public void getButtonCaptionTest() {
        ButtonElement elem = mainLayout.$(ButtonElement.class).get(0);
        testCaption(elem, 1);
    }

    @Test
    public void getGridCaptionTest() {
        GridElement elem = mainLayout.$(GridElement.class).get(0);
        testCaption(elem, 2);
    }

    @Test
    public void getCheckBoxGroupCaptionTest() {
        CheckBoxGroupElement elem = mainLayout.$(CheckBoxGroupElement.class)
                .get(0);
        testCaption(elem, 3);
    }

    @Test
    public void getRadioButtonGroupCaptionTest() {
        RadioButtonGroupElement elem = mainLayout
                .$(RadioButtonGroupElement.class).get(0);
        testCaption(elem, 4);
    }

    @Test
    public void getTwinColSelectCaptionTest() {
        TwinColSelectElement elem = mainLayout.$(TwinColSelectElement.class)
                .get(0);
        testCaption(elem, 5);
    }

    @Test
    public void getListSelectCaptionTest() {
        ListSelectElement elem = mainLayout.$(ListSelectElement.class).get(0);
        testCaption(elem, 6);
    }

    @Test
    public void getColorPickerCaptionTest() {
        ColorPickerElement elem = mainLayout.$(ColorPickerElement.class).get(0);
        testCaption(elem, 7);
    }

    @Test
    public void getAccordionCaptionTest() {
        AccordionElement elem = mainLayout.$(AccordionElement.class).get(0);
        testCaption(elem, 8);
    }

    @Test
    public void getImageCaptionTest() {
        ImageElement elem = mainLayout.$(ImageElement.class).get(0);
        testCaption(elem, 9);
    }

    @Test
    public void getFlashCaptionTest() {
        FlashElement elem = mainLayout.$(FlashElement.class).get(0);
        testCaption(elem, 10);
    }

    @Test
    public void getBrowserFrameCaptionTest() {
        BrowserFrameElement elem = mainLayout.$(BrowserFrameElement.class)
                .get(0);
        testCaption(elem, 11);
    }

    @Test
    public void getCheckBoxCaptionTest() {
        CheckBoxElement elem = mainLayout.$(CheckBoxElement.class).get(0);
        testCaption(elem, 12);
    }

    @Test
    public void getTextFieldCaptionTest() {
        TextFieldElement elem = mainLayout.$(TextFieldElement.class).get(0);
        testCaption(elem, 13);
    }

    @Test
    public void getTextAreaCaptionTest() {
        TextAreaElement elem = mainLayout.$(TextAreaElement.class).get(0);
        testCaption(elem, 14);
    }

    @Test
    public void getDateFieldCaptionTest() {
        DateFieldElement elem = mainLayout.$(DateFieldElement.class).get(0);
        testCaption(elem, 15);
    }

    @Test
    public void getVerticalLayoutCaptionTest() {
        VerticalLayoutElement elem = mainLayout.$(VerticalLayoutElement.class)
                .get(0);
        testCaption(elem, 16);
    }

    @Test
    public void getHorizontalLayoutCaptionTest() {
        HorizontalLayoutElement elem = mainLayout
                .$(HorizontalLayoutElement.class).get(0);
        testCaption(elem, 17);
    }

    @Test
    public void getFormLayoutCaptionTest() {
        FormLayoutElement elem = mainLayout.$(FormLayoutElement.class).get(0);
        testCaption(elem, 18);
    }

    @Test
    public void getGridLayoutCaptionTest() {
        GridLayoutElement elem = mainLayout.$(GridLayoutElement.class).get(0);
        testCaption(elem, 19);
    }

    private void testCaption(AbstractComponentElement elem, int caption_index) {
        String actual = elem.getCaption();
        String expected = ElementComponentGetCaptionBase.DEFAULT_CAPTIONS[caption_index];
        assertTrue("Error with class:" + elem.getAttribute("class"),
                expected.equals(actual));
    }
}
