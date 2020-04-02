package com.vaadin.tests.components.colorpicker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ColorPickerElement;
import com.vaadin.testbench.elements.ColorPickerPreviewElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test legal color values according to
 * http://www.w3schools.com/cssref/css_colors_legal.asp
 */
public class ValoColorPickerInputFormatsTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Ignoring Phantom JS
        return getBrowserCapabilities(Browser.IE11, Browser.FIREFOX,
                Browser.CHROME);
    }

    private ColorPickerPreviewElement previewElement;

    @Before
    public void setUp() {
        openTestURL();
        getPreviewElement();
    }

    private void getPreviewElement() {
        ColorPickerElement cpElement = $(ColorPickerElement.class)
                .id("colorpicker1");
        // Open ColorPicker
        cpElement.click();
        // Find preview element
        previewElement = $(ColorPickerPreviewElement.class).first();
    }

    @Override
    protected Class<?> getUIClass() {
        return ValoColorPickerTestUI.class;
    }

    @Test
    public void testRGBValue() throws Exception {
        setColorpickerValue("rgb(100 100 100)");

        assertEquals("#646464", previewElement.getColorFieldValue());
    }

    @Test
    public void testRGBAValue() {
        setColorpickerValue("rgba(100,100,100, 0.5)");

        assertEquals("#646464", previewElement.getColorFieldValue());
    }

    @Test
    public void testHSLValue() {
        setColorpickerValue("hsl(120,100%, 50%)");

        assertEquals("#00ff00", previewElement.getColorFieldValue());
    }

    @Test
    public void testHSLAValue() {
        setColorpickerValue("hsla(120, 0, 50%, 0.3)");

        assertEquals("#808080", previewElement.getColorFieldValue());
    }

    @Test
    public void testHexTextInputValidation() {
        // set valid hex value to ColorTextField
        setColorpickerValue("#AAbb33");
        assertFalse(previewElement.getColorFieldContainsErrors());
    }

    @Test
    public void testRGBTextInputValidation() {
        String rgbString = "rgb(255 10 0)";
        // set valid rgb value to ColorTextField
        setColorpickerValue(rgbString);
        assertFalse(previewElement.getColorFieldContainsErrors());
    }

    @Test
    public void testHSLTextInputValidation() {
        String hslString = "HSL(300, 60, 100)";
        setColorpickerValue(hslString);
        assertFalse(previewElement.getColorFieldContainsErrors());
    }

    @Test
    public void testHexTextInputValidationError() {
        // set invalid hex value to ColorTextField
        setColorpickerValue("#xyz");
        assertTrue(previewElement.getColorFieldContainsErrors());
    }

    @Test
    public void testRGBTextInputValidationError() {
        String rgbString = "rgb(300, 60, 90)";
        // set invalid rgb value to ColorTextField
        setColorpickerValue(rgbString);
        assertTrue(previewElement.getColorFieldContainsErrors());
    }

    @Test
    public void testRGBATextInputValidationError() {
        String rgbaString = "rgba(250, 0, 10, 6.0)";
        // set invalid rgba value to ColorTextField
        setColorpickerValue(rgbaString);
        assertTrue(previewElement.getColorFieldContainsErrors());
    }

    @Test
    public void testHSLTextInputValidationError() {
        String hslString = "hsl(370,60%,120%)";
        // set invalid hsl value to ColorTextField
        setColorpickerValue(hslString);
        assertTrue(previewElement.getColorFieldContainsErrors());
    }

    @Test
    public void testHSLATextInputValidationError() {
        String hslaString = "hsla(300, 50, 10, 1.1)";
        // set invalid hsla value to ColorTextField
        setColorpickerValue(hslaString);
        assertTrue(previewElement.getColorFieldContainsErrors());
    }

    @Test
    public void testFailedValidationResult() {
        // set invalid hex value to ColorTextField
        setColorpickerValue("#xyz");
        // verify there are errors
        assertTrue(previewElement.getColorFieldContainsErrors());
        // verify value has not been changed
        assertEquals("#xyz", previewElement.getColorFieldValue());
    }

    private void setColorpickerValue(String value) {
        WebElement field = previewElement.getColorTextField();

        // Select all text
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"));

        // Replace with new value
        field.sendKeys(value);

        // Submit
        field.sendKeys(Keys.RETURN);
    }
}
