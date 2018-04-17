package com.vaadin.tests.server.component.colorpicker;

import static org.junit.Assert.assertTrue;

import java.text.DecimalFormat;
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
public class RGBAPatternParsingTest {
    private static final DecimalFormat df = new DecimalFormat("#0.0#");

    @Parameter(value = 0)
    public String input;

    @Parameter(value = 1)
    public int expectedRGB;

    @Parameter(value = 2)
    public int expectedAlpha;

    @Parameter(value = 3)
    public boolean expectedMatches;

    @Parameters(name = "{index}: testRGBAData({0}) = ({1},{2},{3})")
    public static Collection<Object[]> rgbdata() {
        Object[][] values = new Object[201][3];
        Random rnd = new Random();
        int i = 0;
        for (; i < values.length - INVALID_RGBA_VALUES.length; i++) {
            StringBuilder sb = new StringBuilder("rgba(");

            int red = rnd.nextInt(256);
            int green = rnd.nextInt(256);
            int blue = rnd.nextInt(256);
            double alpha = Double.parseDouble(df.format(rnd.nextDouble()));

            String[] rgba = { Integer.toString(red), Integer.toString(green),
                    Integer.toString(blue), df.format(alpha) };

            // delimiter for separating values
            String delimiter = rnd.nextInt(2) == 1 ? " "
                    : rnd.nextInt(2) == 1 ? "," : " ";

            sb.append(String.join(delimiter, rgba)).append(")");

            // add values to test data
            values[i] = new Object[] { sb.toString(),
                    new Color(red, green, blue).getRGB(), (int) (alpha * 255d),
                    true };
        }
        // add invalid values to test data
        for (int j = 0; j < INVALID_RGBA_VALUES.length; j++) {
            values[i++] = INVALID_RGBA_VALUES[j];
        }
        return Arrays.asList(values);
    }

    @Test
    public void testRGBData() {
        Matcher m = ColorUtil.RGBA_PATTERN.matcher(input);
        boolean matches = m.matches();
        if (expectedMatches) {
            Color c = new Color(expectedRGB);
            c.setAlpha(expectedAlpha);
            Color c1 = ColorUtil.getRGBAPatternColor(m);
            assertTrue(c.equals(c1));
        } else {
            assertTrue(!matches);
        }
    }

    private static final Object[][] INVALID_RGBA_VALUES = {
            { "rgba(256,0,0,0)", 0, 0, false },
            { "rgba(0, 256, 0, -0 )", 0, 0, false },
            { "rgba(0,0,10.0, 00)", 0, 0, false },
            { "rgba(0 0 0 2.00)", 0, 0, false },
            { "rgba(0 -99 0 0.50)", 0, 0, false },
            { "rgba(0,255%,255,1.0)", 0, 0, false },
            { "rgba(255, 255, 255, 1.05)", 0, 0, false },
            { "rgba(255, 255, 255, 1.50)", 0, 0, false },
            { "rgb a(255 255 0.005)", 0, 0, false },
            { "rgba(163, 256, 1000, 0.24)", 0, 0, false },
            { "rgba(100, 0.5, 250, 0.8)", 0, 0, false },
            { "rgba(, 50, 0, 0.6)", 0, 0, false },
            { "rgba(200, 50, 0, 10.6)", 0, 0, false },
            { "rgba 200, 50, 0, 1.", 0, 0, false },
            { "rgba(0,0,0,0.)", 0, 0, false },
            { "rgb(200, 50, 0)", 0, 0, false },
            { "hsla,0(10,0,0)", 0, 0, false },
            { "rgba(\\s.*\\d[0-9])", 0, 0, false },
            { "rgba(\\.*,255,255, 0)", 0, 0, false }, { "#\\d.*", 0, 0, false },
            { "", 0, 0, false }, { "rgba(\\d,\\d,\\d,0.0)", 0, 0, false },
            { "^rgba\\( \\.*)", 0, 0, false } };
}
