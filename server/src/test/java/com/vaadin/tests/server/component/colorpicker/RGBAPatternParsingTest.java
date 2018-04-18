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
public class RGBAPatternParsingTest {

    @Parameter(value = 0)
    public String input;

    @Parameter(value = 1)
    public int expectedRed;
    @Parameter(value = 2)
    public int expectedGreen;
    @Parameter(value = 3)
    public int expectedBlue;
    @Parameter(value = 4)
    public int expectedAlpha;

    @Parameter(value = 5)
    public boolean expectedMatches;

    @Parameters(name = "{index}: testRGBAData({0}) = ({1},{2},{3},{4},{5})")
    public static Collection<Object[]> rgbdata() {
        Object[][] validValues = { { "rgba(0,0,0,0)", 0, 0, 0, 0, true },
                { "RGBA(0, 0, 0, 0 )", 0, 0, 0, 0, true },
                { "rgba(0 0 0 0.00)", 0, 0, 0, 0, true },
                { "rgba(1 1 1 1.00)", 1, 1, 1, 255, true },
                { "rgba(0 100 200 0.50)", 0, 100, 200, 127, true },
                { "rgba(255,255,255,1.0)", 255, 255, 255, 255, true },
                { "rgba(255, 255, 255, 1.0)", 255, 255, 255, 255, true },
                { "rgba(255 255 255 0)", 255, 255, 255, 0, true },
                { "rgba(1, 10, 100, 0.00)", 1, 10, 100, 0, true } };
        Object[][] invalidValues = { { "rgba(256,0,0,0)", 0, 0, 0, 0, false },
                { "rgba(0, 256, 0, -0 )", 0, 0, 0, 0, false },
                { "rgba(0,0,10.0, 00)", 0, 0, 0, 0, false },
                { "rgba(0 0 0 2.00)", 0, 0, 0, 0, false },
                { "rgba(0 -99 0 0.50)", 0, 0, 0, 0, false },
                { "rgba(0,255%,255,1.0)", 0, 0, 0, 0, false },
                { "rgba(255, 255, 255, 1.05)", 0, 0, 0, 0, false },
                { "rgba(255, 255, 255, 1.50)", 0, 0, 0, 0, false },
                { "rgb a(255 255 0.005)", 0, 0, 0, 0, false },
                { "rgba(163, 256, 1000, 0.24)", 0, 0, 0, 0, false },
                { "rgba(100, 0.5, 250, 0.8)", 0, 0, 0, 0, false },
                { "rgba(, 50, 0, 0.6)", 0, 0, 0, 0, false },
                { "rgba(200, 50, 0, 10.6)", 0, 0, 0, 0, false },
                { "rgba 200, 50, 0, 1.", 0, 0, 0, 0, false },
                { "rgba(0,0,0,0.)", 0, 0, 0, 0, false },
                { "rgb(200, 50, 0)", 0, 0, 0, 0, false },
                { "hsla,0(10,0,0)", 0, 0, 0, 0, false },
                { "rgba(\\s.*\\d[0-9])", 0, 0, 0, 0, false },
                { "rgba(\\.*,255,255, 0)", 0, 0, 0, 0, false },
                { "#\\d.*", 0, 0, 0, 0, false }, { "", 0, 0, 0, 0, false },
                { "rgba(\\d,\\d,\\d,0.0)", 0, 0, 0, 0, false },
                { "^rgba\\( \\.*)", 0, 0, 0, 0, false } };

        ArrayList<Object[]> values = new ArrayList<>();
        Collections.addAll(values, validValues);
        Collections.addAll(values, invalidValues);

        return values;
    }

    @Test
    public void testRGBAData() {
        Matcher m = ColorUtil.RGBA_PATTERN.matcher(input);
        boolean matches = m.matches();
        if (expectedMatches) {
            Color expectedColor = new Color(expectedRed, expectedGreen,
                    expectedBlue, expectedAlpha);
            Color c1 = ColorUtil.getRGBAPatternColor(m);

            assertTrue(expectedColor.equals(c1));
        } else {
            assertTrue(!matches);
        }
    }
}
