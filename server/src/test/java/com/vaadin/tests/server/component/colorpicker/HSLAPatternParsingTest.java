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
public class HSLAPatternParsingTest {

    private static final DecimalFormat df = new DecimalFormat("#0.0#");

    @Parameter(value = 0)
    public String input;

    @Parameter(value = 1)
    public int expectedRGB;

    @Parameter(value = 2)
    public int expectedAlpha;

    @Parameter(value = 3)
    public boolean expectedMatches;

    @Parameters(name = "{index}: testHSLAData({0}) = ({1},{2},{3})")
    public static Collection<Object[]> hsladata() {
        Object[][] values = new Object[201][4];
        Random rnd = new Random();
        int i = 0;
        for (; i < values.length - INVALID_HSLA_VALUES.length; i++) {
            StringBuilder sb = new StringBuilder("hsla(");

            int hue = rnd.nextInt(361);
            int saturation = rnd.nextInt(101);
            int light = rnd.nextInt(101);
            double alpha = Double.parseDouble(df.format(rnd.nextDouble()));

            String[] hsla = { Integer.toString(hue),
                    Integer.toString(saturation), Integer.toString(light),
                    df.format(alpha) };

            // delimiter for values
            String delimiter = rnd.nextInt(2) == 1 ? " "
                    : rnd.nextInt(2) == 1 ? "," : " ";
            sb.append(String.join(delimiter, hsla)).append(")");

            // get rgb value
            int rgb = Color.HSLtoRGB(hue, saturation, light);
            // add values to test data
            values[i] = new Object[] { sb.toString(), rgb, (int) (alpha * 255d),
                    true };
        }
        // add invalid values to test data
        for (int j = 0; j < INVALID_HSLA_VALUES.length; j++) {
            values[i++] = INVALID_HSLA_VALUES[j];
        }
        return Arrays.asList(values);
    }

    @Test
    public void testHSLAData() {
        Matcher m = ColorUtil.HSLA_PATTERN.matcher(input);
        boolean matches = m.matches();
        if (expectedMatches) {
            Color c = new Color(expectedRGB);
            c.setAlpha(expectedAlpha);
            Color c1 = ColorUtil.getHSLAPatternColor(m);
            assertTrue(c.equals(c1));
        } else {
            assertTrue(!matches);
        }
    }

    private static final Object[][] INVALID_HSLA_VALUES = {
            { "hsla(361,0,0,0)", 0, 0, false },
            { "hsla(0.0, 0, 0, 0)", 0, 0, false },
            { "hsla(0,0%,0%, 1.1)", 0, 0, false },
            { "hsla(0 0 0 0.009 )", 0, 0, false },
            { "hsla(0 0% -100% 0.50)", 0, 0, false },
            { "hsla(360,1000,100,1.0)", 0, 0, false },
            { "hsla(0, 100, 100, 2.0)", 0, 0, false },
            { "hsla(360, 100%, 100%, 10.00)", 0, 0, false },
            { "hsl a(360 100% 100% 1.)", 0, 0, false },
            { "hsla(20, -10, 10, 0.24)", 0, 0, false },
            { "hsla(400, 0, 50, 0.8)", 0, 0, false },
            { "hsla(200, 50, 0, 0.996)", 0, 0, false },
            { "hsla 200, 50, 0, 0.9", 0, 0, false },
            { "hsla(0,0,0,0.)", 0, 0, false } };

}
