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
public class HexPatternParsingTest {

    @Parameter(value = 0)
    public String input;

    @Parameter(value = 1)
    public int expectedRGB;

    @Parameter(value = 2)
    public boolean expectedMatches;

    @Parameters(name = "{index}: textValidHEX({0}) = ({1},{2})")
    public static Collection<Object[]> hexdata() {
        Object[][] values = new Object[201][3];
        Random rnd = new Random();
        int i = 0;
        for (; i < values.length - INVALID_HEX_VALUES.length; i++) {
            StringBuilder sb = new StringBuilder("#");

            int red = rnd.nextInt(256);
            int green = rnd.nextInt(256);
            int blue = rnd.nextInt(256);

            sb.append(Integer.toHexString(red).length() < 2
                    ? "0" + Integer.toHexString(red)
                    : Integer.toHexString(red));
            sb.append(Integer.toHexString(green).length() < 2
                    ? "0" + Integer.toHexString(green)
                    : Integer.toHexString(green));
            sb.append(Integer.toHexString(blue).length() < 2
                    ? "0" + Integer.toHexString(blue)
                    : Integer.toHexString(blue));
            values[i] = new Object[] { sb.toString(),
                    new Color(red, green, blue).getRGB(), true };
        }
        for (int j = 0; j < INVALID_HEX_VALUES.length; j++) {
            values[i++] = INVALID_HEX_VALUES[j];
        }
        return Arrays.asList(values);
    }

    @Test
    public void testValidHEX() {
        Matcher m = ColorUtil.HEX_PATTERN.matcher(input);
        boolean matches = m.matches();
        if (expectedMatches) {
            Color c = new Color(expectedRGB);
            Color c1 = ColorUtil.getHexPatternColor(m);
            assertTrue(c.equals(c1));
        } else {
            assertTrue(!matches);
        }
    }

    private static final Object[][] INVALID_HEX_VALUES = {
            { "#0000000", 0, false }, { "#ffgfff", 0, false },
            { "#FF10f", 0, false }, { "#aa9", 0, false }, { "#03", 0, false },
            { "#aab3c4c", 0, false }, { "#6010", 0, false },
            { "#CCCC", 0, false }, { "#9", 0, false },
            { "#10 10 10", 0, false }, { "101010", 0, false },
            { "#10101q", 0, false }, { "\\s%\\d[0-9]", 0, false },
            { "#\\d.*", 0, false }, { "rgb\\(\\.*", 0, false },
            { "#\\d\\d\\d", 0, false }, { "#\\d.*", 0, false },
            { "", 0, false }, { "hsl(25,25,25)", 0, false } };
}
