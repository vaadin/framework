package com.vaadin.tests.components.colorpicker;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test legal color values according to
 * http://www.w3schools.com/cssref/css_colors_legal.asp
 */
public class ColorPickerInputFormatsTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return ColorPickerTestUI.class;
    }

    @Test
    public void testRGBValue() throws Exception {
        openTestURL();

        setColorpickerValue("rgb(100,100,100)");

        assertEquals("#646464", getColorpickerValue());
    }

    @Test
    public void testRGBAValue() {
        openTestURL();

        setColorpickerValue("rgba(100,100,100, 0.5)");

        assertEquals("#646464", getColorpickerValue());
    }

    @Test
    public void testHSLValue() {
        openTestURL();

        setColorpickerValue("hsl(120,100%,50%)");

        assertEquals("#00ff00", getColorpickerValue());
    }

    @Test
    public void testHSLAValue() {
        openTestURL();

        setColorpickerValue("hsla(120,100%,50%, 0.3)");

        assertEquals("#00ff00", getColorpickerValue());
    }

    private void setColorpickerValue(String value) {

        // Open colorpicker
        getDriver().findElement(By.id("colorpicker1")).click();

        // Add RGB value
        WebElement field = getDriver()
                .findElement(By.className("v-colorpicker-preview-textfield"));

        // Select all text
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"));

        // Replace with rgb value
        field.sendKeys(value);

        // Submit
        field.sendKeys(Keys.RETURN);
    }

    private String getColorpickerValue() {
        WebElement field = getDriver()
                .findElement(By.className("v-colorpicker-preview-textfield"));
        return field.getAttribute("value");
    }
}
