package com.vaadin.tests.server.component.colorpicker;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.regex.Matcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.components.colorpicker.ColorUtils;

@RunWith(value = Parameterized.class)
public class RGBPatternParsingTest {

    @Parameter(value = 0)
    public String input;

    @Parameter(value = 1)
    public int expectedRGB;

    @Parameter(value = 2)
    public boolean expectedMatches;

    @Parameters(name = "{index}: testRGBData({0}) = ({1},{2})")
    public static Collection<Object[]> rgbdata() {
        Object[][] values = new Object[201][3];
        Random rnd = new Random();
        int i = 0;
        for (; i < values.length - INVALID_RGB_VALUES.length; i++) {
            StringBuilder sb = new StringBuilder("rgb(");

            int red = rnd.nextInt(256);
            int green = rnd.nextInt(256);
            int blue = rnd.nextInt(256);
            String[] rgb = { Integer.toString(red), Integer.toString(green),
                    Integer.toString(blue) };
            // delimiter for values
            String delimiter = rnd.nextInt(2) == 1 ? " "
                    : rnd.nextInt(2) == 1 ? "," : " ";
            sb.append(String.join(delimiter, rgb)).append(")");

            // add values to test data
            values[i] = new Object[] { sb.toString(),
                    new Color(red, green, blue).getRGB(), true };
        }
        for (int j = 0; j < INVALID_RGB_VALUES.length; j++) {
            values[i++] = INVALID_RGB_VALUES[j];
        }
        return Arrays.asList(values);
    }

    @Test
    public void testRGBData() {
        Matcher m = ColorUtils.RGB_PATTERN.matcher(input);
        boolean matches = m.matches();
        if (expectedMatches) {
            Color c = new Color(expectedRGB);
            Color c1 = ColorUtils.getRGBPatternColor(m);
            assertTrue(c.equals(c1));
        } else {
            assertTrue(!matches);
        }
    }

    private static final Object[][] INVALID_RGB_VALUES = {
            { "rgb(,0,0)", 0, false }, { "rgb(0, 0, 0, )", 0, false },
            { "rgb(0.0,0,0)", 0, false }, { "rgb( 0 0 -1 )", 0, false },
            { "rgb(1 00)", 0, false }, { "rgb(255,255,255.)", 0, false },
            { "r gb(255, 256, 255)", 0, false },
            { "rgb( 255, 255, 256 )", 0, false },
            { "rgb(163, 2%, 210)", 0, false }, { "rGb(000)", 0, false },
            { "rgb(255255255)", 0, false }, { "rGBA(255,255,255)", 0, false },
            { "rgb 255 255 255)", 0, false }, { "255, 255, 0", 0, false },
            { "hsl(10,0,0)", 0, false }, { "\\s%\\d[0-9]", 0, false },
            { "rgb(\\.*,255,255)", 0, false }, { "#\\d.*", 0, false },
            { "", 0, false }, { "rgb(\\d,\\d,\\d)", 0, false },
            { "^rgb\\( \\.*)", 0, false } };
}
