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
import com.vaadin.ui.components.colorpicker.ColorUtil;

@RunWith(value = Parameterized.class)
public class HSLPatternParsingTest {

    @Parameter(value = 0)
    public String input;

    @Parameter(value = 1)
    public int expectedRGB;

    @Parameter(value = 2)
    public boolean expectedMatches;

    @Parameters(name = "{index}: testHSLData({0}) = ({1},{2})")
    public static Collection<Object[]> hsldata() {
        Object[][] values = new Object[201][3];
        Random rnd = new Random();
        int i = 0;
        for (; i < values.length - INVALID_HSL_VALUES.length; i++) {
            StringBuilder sb = new StringBuilder("hsl(");

            int hue = rnd.nextInt(361);
            int saturation = rnd.nextInt(101);
            int light = rnd.nextInt(101);

            String[] hsls = { Integer.toString(hue),
                    Integer.toString(saturation), Integer.toString(light) };
            // delimiter for values
            String delimiter = rnd.nextInt(2) == 1 ? " "
                    : rnd.nextInt(2) == 1 ? "," : " ";
            sb.append(String.join(delimiter, hsls)).append(")");

            // get rgb value
            int rgb = Color.HSLtoRGB(hue, saturation, light);
            // add values to test data
            values[i] = new Object[] { sb.toString(), rgb, true };
        }
        // add invalid values to test data
        for (int j = 0; j < INVALID_HSL_VALUES.length; j++) {
            values[i++] = INVALID_HSL_VALUES[j];
        }
        return Arrays.asList(values);
    }

    @Test
    public void testHSLData() {
        Matcher m = ColorUtil.HSL_PATTERN.matcher(input);
        boolean matches = m.matches();
        if (expectedMatches) {
            Color c = new Color(expectedRGB);
            Color c1 = ColorUtil.getHSLPatternColor(m);
            assertTrue(c.equals(c1));
        } else {
            assertTrue(!matches);
        }
    }

    private static final Object[][] INVALID_HSL_VALUES = {
            { "hsl(361,0,0)", 0, false }, { "hsl(-0, 0, 0)", 0, false },
            { "hsl (100%,0%,0% )", 0, false }, { "hsl(0 101 0)", 0, false },
            { "hsl(0 0% -99%)", 0, false }, { "hsl(360,100,10 0)", 0, false },
            { "hsl(360, 100, 101)", 0, false },
            { "hsl(360, 110%, 100%)", 0, false },
            { "hsl(3600 100% 100%)", 0, false },
            { "hs l(420, 10, 10)", 0, false }, { "hsl(100, 0, 5,0)", 0, false },
            { "hsla(200, 50, 0)", 0, false }, { "hsl(0,0,0", 0, false },
            { "rgb\\(\\.*", 0, false }, { "hsl(\\.*)", 0, false },
            { "#\\d.*", 0, false }, { "", 0, false } };

}
