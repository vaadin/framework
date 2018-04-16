package com.vaadin.tests.server.component.colorpicker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

public class ColorInputPatternMatchingTest {

    @Test
    public void inputValidHEX() {
        String hex1 = "#000000";
        String hex2 = "#ffffff";
        String hex3 = "#FF00ff";
        String hex4 = "#aa90e3";
        String hex5 = "#016953";
        String hex6 = "#bC64D0";
        String hex7 = "#F100FF";
        String hex8 = "#F0E9a5";
        String hex9 = "#990077";

        List<String> hexInput = new LinkedList<>(Arrays.asList(hex1, hex2, hex3,
                hex4, hex5, hex6, hex7, hex8, hex9));

        for (String testValue : hexInput) {
            assertTrue("Input " + testValue + " does not match hex pattern",
                    HEX_PATTERN.matcher(testValue).matches());
        }
    }

    @Test
    public void inputValidRGB() {
        String rgb1 = "rgb(0,0,0)";
        String rgb2 = "rgb(0, 0, 0 )";
        String rgb3 = "rgb(0,0,0)";
        String rgb4 = "rgb(0 0 0)";
        String rgb5 = "rgb(1 0 0 )";
        String rgb6 = "rgb(255,255,255)";
        String rgb7 = "rgb(255, 255, 255)";
        String rgb8 = "rgb( 255, 255, 255 )";
        String rgb9 = "rgb(163, 2, 210)";
        String rgb10 = "rGb(100, 0, 250)";
        String rgb11 = "RGB(200, 50, 0)";

        List<String> rgbInput = new LinkedList<>(Arrays.asList(rgb1, rgb2, rgb3,
                rgb4, rgb5, rgb6, rgb7, rgb8, rgb9, rgb10, rgb11));

        for (String testValue : rgbInput) {
            assertTrue("Input " + testValue + " does not match RGB pattern",
                    RGB_PATTERN.matcher(testValue).matches());
        }
    }

    @Test
    public void inputValidRGBA() {
        String rgba1 = "rgba(0,0,0,0)";
        String rgba2 = "rgba(0, 0, 0, 0 )";
        String rgba3 = "rgba(0,0,0, 0.1)";
        String rgba4 = "rgba(0 0 0 0.00)";
        String rgba5 = "rgba(1 0 0 0.50)";
        String rgba6 = "rgba(255,255,255,1.0)";
        String rgba7 = "rgba(255, 255, 255, 1.0)";
        String rgba8 = "rgba(255, 255, 255, 1.00)";
        String rgba9 = "rgba(255 255 255 1.00)";
        String rgba10 = "rgba(163, 2, 210, 0.24)";
        String rgba11 = "rgba(100, 0, 250, 0.8)";
        String rgba12 = "RGBA(200, 50, 0, 0.6)";

        List<String> rgbaInput = new LinkedList<>(
                Arrays.asList(rgba1, rgba2, rgba3, rgba4, rgba5, rgba6, rgba7,
                        rgba8, rgba9, rgba10, rgba11, rgba12));

        for (String testValue : rgbaInput) {
            assertTrue("Input " + testValue + " does not match RGBA pattern",
                    RGBA_PATTERN.matcher(testValue).matches());
        }
    }

    @Test
    public void inputValidHSL() {
        String hsl1 = "hsl(0,0,0)";
        String hsl2 = "hsl(0, 0, 0)";
        String hsl3 = "hsl(0,0%,0% )";
        String hsl4 = "hsl(0 0 0)";
        String hsl5 = "hsl(0 0% 0%)";
        String hsl6 = "hsl(360,100,100)";
        String hsl7 = "hsl(360, 100, 100)";
        String hsl8 = "hsl(360, 100%, 100%)";
        String hsl9 = "hsl(360 100% 100%)";
        String hsl10 = "hsl(20, 10, 10)";
        String hsl11 = "hsl(100, 0, 50)";
        String hsl12 = "hsl(200, 50, 0)";
        String hsl13 = "hsl(200, 50, 05)";
        String hsl14 = "HSL(200 , 50 , 05)";

        List<String> hslInput = new LinkedList<>(
                Arrays.asList(hsl1, hsl2, hsl3, hsl4, hsl5, hsl6, hsl7, hsl8,
                        hsl9, hsl10, hsl11, hsl12, hsl13, hsl14));

        for (String testValue : hslInput) {
            assertTrue("Input " + testValue + " does not match HSL pattern",
                    HSL_PATTERN.matcher(testValue).matches());
        }
    }

    @Test
    public void inputValidHSLA() {
        String hsla1 = "hsla(0,0,0,0)";
        String hsla2 = "hsla(0, 0, 0, 0)";
        String hsla3 = "hsla(0,0%,0%, 0.1)";
        String hsla4 = "hsla(0 0 0 0.00 )";
        String hsla5 = "hsla(0 0% 0% 0.50)";
        String hsla6 = "hsla(360,100,100,1.0)";
        String hsla7 = "hsla(360, 100, 100, 1.0)";
        String hsla8 = "hsla(360, 100%, 100%, 1.00)";
        String hsla9 = "hsla(360 100% 100% 1.00)";
        String hsla10 = "hsla(20, 10, 10, 0.24)";
        String hsla11 = "hsla(100, 0, 50, 0.8)";
        String hsla12 = "hsla(269, 50, 0, .6)";

        List<String> hslaInput = new LinkedList<>(
                Arrays.asList(hsla1, hsla2, hsla3, hsla4, hsla5, hsla6, hsla7,
                        hsla8, hsla9, hsla10, hsla11, hsla12));

        for (String testValue : hslaInput) {
            assertTrue("Input " + testValue + " does not match HSLA pattern",
                    HSLA_PATTERN.matcher(testValue).matches());
        }
    }

    @Test
    public void inputInvalidHEX() {
        String hex1 = "#0000000";
        String hex2 = "#ffgfff";
        String hex3 = "#FF10f";
        String hex4 = "#aa9";
        String hex5 = "#03";
        String hex6 = "#aab3c4c";
        String hex7 = "#6010";
        String hex8 = "#CCCC";
        String hex9 = "#9";
        String hex10 = "#10 10 10";
        String hex11 = "101010";
        String hex12 = "#10101q";

        List<String> hexInput = new LinkedList<>(Arrays.asList(hex1, hex2, hex3,
                hex4, hex5, hex6, hex7, hex8, hex9, hex10, hex11, hex12));

        for (String testValue : hexInput) {
            assertFalse("Input " + testValue + " should not match hex pattern",
                    HEX_PATTERN.matcher(testValue).matches());
        }
    }

    @Test
    public void inputInvalidRGB() {
        String rgb1 = "rgb(,0,0)";
        String rgb2 = "rgb(0, 0, 0, )";
        String rgb3 = "rgb(0.0,0,0)";
        String rgb4 = "rgb(0 0 -1)";
        String rgb5 = "rgb(1 00)";
        String rgb6 = "rgb(255,255,255.)";
        String rgb7 = "r gb(255, 256, 255)";
        String rgb8 = "rgb( 255, 255, 256 )";
        String rgb9 = "rgb(163, 2%, 210)";
        String rgb10 = "rGb(000)";
        String rgb11 = "rgb(255255255)";
        String rgb12 = "RGBA(255,255,255)";
        String rgb13 = "rgb 255 255 255)";
        String rgb14 = "255, 255, 0";
        String rgb15 = "hsl(10,0,0)";

        List<String> rgbInput = new LinkedList<>(
                Arrays.asList(rgb1, rgb2, rgb3, rgb4, rgb5, rgb6, rgb7, rgb8,
                        rgb9, rgb10, rgb11, rgb12, rgb13, rgb14, rgb15));

        for (String testValue : rgbInput) {
            assertFalse(
                    "Input " + testValue
                            + " should not match match RGB pattern",
                    RGB_PATTERN.matcher(testValue).matches());
        }
    }

    @Test
    public void inputInvalidRGBA() {
        String rgba1 = "rgba(256,0,0,0)";
        String rgba2 = "rgba(0, 256, 0, -0 )";
        String rgba3 = "rgba(0,0,10.0, 00)";
        String rgba4 = "rgba(0 0 0 2.00)";
        String rgba5 = "rgba(0 -99 0 0.50)";
        String rgba6 = "rgba(0,255%,255,1.0)";
        String rgba7 = "rgba(255, 255, 255, 1.05)";
        String rgba8 = "rgba(255, 255, 255, 1.50)";
        String rgba9 = "rgb a(255  255 0.005)";
        String rgba10 = "rgba(163, 256, 1000, 0.24)";
        String rgba11 = "rgba(100, 0.5, 250, 0.8)";
        String rgba12 = "rgba(, 50, 0, 0.6)";
        String rgba13 = "rgba(200, 50, 0, 10.6)";
        String rgba14 = "rgba 200, 50, 0, 1.";
        String rgba15 = "rgba(0,0,0,0.)";
        String rgba16 = "rgb(200, 50, 0)";

        List<String> rgbaInput = new LinkedList<>(Arrays.asList(rgba1, rgba2,
                rgba3, rgba4, rgba5, rgba6, rgba7, rgba8, rgba9, rgba10, rgba11,
                rgba12, rgba13, rgba14, rgba15, rgba16));

        for (String testValue : rgbaInput) {
            assertFalse("Input " + testValue + " should not match RGBA pattern",
                    RGBA_PATTERN.matcher(testValue).matches());
        }
    }

    @Test
    public void inputInvalidHSL() {
        String hsl1 = "hsl(361,0,0)";
        String hsl2 = "hsl(-0, 0, 0)";
        String hsl3 = "hsl (100%,0%,0% )";
        String hsl4 = "hsl(0 101 0)";
        String hsl5 = "hsl(0 0% -99%)";
        String hsl6 = "hsl(360,100,10 0)";
        String hsl7 = "hsl(360, 100, 101)";
        String hsl8 = "hsl(360, 110%, 100%)";
        String hsl9 = "hsl(3600 100% 100%)";
        String hsl10 = "hs l(420, 10, 10)";
        String hsl11 = "hsl(100, 0, 5,0)";
        String hsl12 = "hsla(200, 50, 0)";
        String hsl13 = "hsl(0,0,0";

        List<String> hslInput = new LinkedList<>(
                Arrays.asList(hsl1, hsl2, hsl3, hsl4, hsl5, hsl6, hsl7, hsl8,
                        hsl9, hsl10, hsl11, hsl12, hsl13));

        for (String testValue : hslInput) {
            assertFalse("Input " + testValue + " should not match HSL pattern",
                    HSL_PATTERN.matcher(testValue).matches());
        }
    }

    @Test
    public void inputInvalidHSLA() {
        String hsla1 = "hsla(361,0,0,0)";
        String hsla2 = "hsla(0.0, 0, 0, 0)";
        String hsla3 = "hsla(0,0%,0%, 1.1)";
        String hsla4 = "hsla(0 0 0 0.009 )";
        String hsla5 = "hsla(0 0% -100% 0.50)";
        String hsla6 = "hsla(360,1000,100,1.0)";
        String hsla7 = "hsla(0, 100, 100, 2.0)";
        String hsla8 = "hsla(360, 100%, 100%, 10.00)";
        String hsla9 = "hsl a(360 100% 100% 1.)";
        String hsla10 = "hsla(20, -10, 10, 0.24)";
        String hsla11 = "hsla(400, 0, 50, 0.8)";
        String hsla12 = "hsla(200, 50, 0, 0.996)";
        String hsla13 = "hsla 200, 50, 0, 0.9";
        String hsla14 = "hsla(0,0,0,0.)";

        List<String> hslaInput = new LinkedList<>(
                Arrays.asList(hsla1, hsla2, hsla3, hsla4, hsla5, hsla6, hsla7,
                        hsla8, hsla9, hsla10, hsla11, hsla12, hsla13, hsla14));

        for (String testValue : hslaInput) {
            assertFalse("Input " + testValue + " should not match HSLA pattern",
                    HSLA_PATTERN.matcher(testValue).matches());
        }
    }

    /**
     * @see com.vaadin.ui.components.colorpicker.ColorPickerPreview#HEX_PATTERN
     */
    private static final Pattern HEX_PATTERN = Pattern.compile(
            "(?i)^#\\s*(?<red>[\\da-f]{2})(?<green>[\\da-f]{2})(?<blue>[\\da-f]{2}"
                    + ")\\s*$");
    /**
     * @see com.vaadin.ui.components.colorpicker.ColorPickerPreview#RBB_PATTERN
     */
    private static final Pattern RGB_PATTERN = Pattern.compile(
            "(?i)^rgb\\(\\s*(?<red>[01]?\\d{1,2}|2[0-4]\\d|25[0-5])(?:\\s*[,+|\\"
                    + "s+]\\s*)(?<green>[01]?\\d\\d?|2[0-4]\\d|25[0-5])(?:\\s*[,"
                    + "+|\\s+]\\s*)(?<blue>[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\s*\\"
                    + ")$");
    /**
     * @see com.vaadin.ui.components.colorpicker.ColorPickerPreview#RGBA_PATTERN
     */
    private static final Pattern RGBA_PATTERN = Pattern.compile(
            "(?i)^rgba\\(\\s*(?<red>[01]?\\d{1,2}|2[0-4]\\d|25[0-5])(?:\\s*[,+|"
                    + "\\s+]\\s*)(?<green>[01]?\\d\\d?|2[0-4]\\d|25[0-5])(?:\\s"
                    + "*[,+|\\s+]\\s*)(?<blue>[01]?\\d\\d?|2[0-4]\\d|25[0-5])(?"
                    + ":\\s*[,+|\\s+]\\s*)(?<alpha>0(?:\\.\\d{1,2})?|0?(?:\\.\\"
                    + "d{1,2})|1(?:\\.0{1,2})?)\\s*\\)$");

    /**
     * @see com.vaadin.ui.components.colorpicker.ColorPickerPreview#HSL_PATTERN
     */
    private static final Pattern HSL_PATTERN = Pattern.compile(
            "(?i)hsl\\(\\s*(?<hue>[12]?\\d{1,2}|3[0-5]\\d|360)(?:\\s*[,+|\\s+]"
                    + "\\s*)(?<saturation>\\d{1,2}|100)(?:\\s*%?\\s*[,+|\\s+]\\"
                    + "s*)(?<light>\\d{1,2}|100)(?:\\s*%?\\s*)\\)$");

    /**
     * @see com.vaadin.ui.components.colorpicker.ColorPickerPreview#HSLA_PATTERN
     */
    private static final Pattern HSLA_PATTERN = Pattern.compile(
            "(?i)hsla\\(\\s*(?<hue>[12]?\\d{0,2}|3[0-5]\\d|360)(?:\\s*[,+|\\s+"
                    + "]\\s*)(?<saturation>\\d{1,2}|100)(?:\\s*%?\\s*[,+|\\s+]\\s*"
                    + ")(?<light>\\d{1,2}|100)(?:\\s*%?[,+|\\s+]\\s*)(?<alpha>"
                    + "0(?:\\.\\d{1,2})?|0?(?:\\.\\d{1,2})|1(?:\\.0{1,2})?)"
                    + "\\s*\\)$");
}
