package com.vaadin.tests.server.component.colorpicker;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.components.colorpicker.ColorUtil;

@RunWith(value = Parameterized.class)
public class RGBPatternParsingTest {

    @Parameter(value = 0)
    public String input;

    @Parameter(value = 1)
    public int expectedRed;
    @Parameter(value = 2)
    public int expectedGreen;
    @Parameter(value = 3)
    public int expectedBlue;

    @Parameter(value = 4)
    public boolean expectedMatches;

    @Parameters(name = "{index}: testRGBData({0}) = ({1},{2},{3},{4})")
    public static Collection<Object[]> rgbdata() {
        Object[][] validValues = { { "rgb(0,0,0)", 0, 0, 0, true },
                { "rgb(0, 0, 0)", 0, 0, 0, true },
                { "rgb(0 0 0)", 0, 0, 0, true },
                { "rgb(1 1 1)", 1, 1, 1, true },
                { "rgb(0 100 255)", 0, 100, 255, true },
                { "rgb(255,255,255)", 255, 255, 255, true },
                { "RGB(255, 255, 255 )", 255, 255, 255, true },
                { "rgb(255 255 255)", 255, 255, 255, true },
                { "rgb(1, 10, 100)", 1, 10, 100, true } };
        Object[][] invalidValues = { { "rgb(,0,0)", 0, 0, 0, false },
                { "rgb(0, 0, 0, )", 0, 0, 0, false },
                { "rgb(0.0,0,0)", 0, 0, 0, false },
                { "rgb( 0 0 -1 )", 0, 0, 0, false },
                { "rgb(1 00)", 0, 0, 0, false },
                { "rgb(255,255,255.)", 0, 0, 0, false },
                { "r gb(255, 256, 255)", 0, 0, 0, false },
                { "rgb( 255, 255, 256 )", 0, 0, 0, false },
                { "rgb(163, 2%, 210)", 0, 0, 0, false },
                { "rGb(000)", 0, 0, 0, false },
                { "rgb(255255255)", 0, 0, 0, false },
                { "rGBA(255,255,255)", 0, 0, 0, false },
                { "rgb 255 255 255)", 0, 0, 0, false },
                { "255, 255, 0", 0, 0, 0, false },
                { "hsl(10,0,0)", 0, 0, 0, false },
                { "\\s%\\d[0-9]", 0, 0, 0, false },
                { "rgb(\\.*,255,255)", 0, 0, 0, false },
                { "#\\d.*", 0, 0, 0, false }, { "", 0, 0, 0, false },
                { "rgb(\\d,\\d,\\d)", 0, 0, 0, false },
                { "^rgb\\( \\.*)", 0, 0, 0, false } };

        ArrayList<Object[]> values = new ArrayList<>();
        Collections.addAll(values, validValues);
        Collections.addAll(values, invalidValues);

        return values;
    }

    @Test
    public void testRGBData() {
        Matcher m = ColorUtil.RGB_PATTERN.matcher(input);
        boolean matches = m.matches();
        if (expectedMatches) {
            Color expectedColor = new Color(expectedRed, expectedGreen,
                    expectedBlue);
            Color c1 = ColorUtil.getRGBPatternColor(m);
            assertTrue(expectedColor.equals(c1));
        } else {
            assertTrue(!matches);
        }
    }
}
