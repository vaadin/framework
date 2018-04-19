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
public class HSLAPatternParsingTest {

    @Parameter(value = 0)
    public String input;

    @Parameter(value = 1)
    public int expectedHue;
    @Parameter(value = 2)
    public int expectedSaturation;
    @Parameter(value = 3)
    public int expectedLight;
    @Parameter(value = 4)
    public int expectedAlpha;

    @Parameter(value = 5)
    public boolean expectedMatches;

    @Parameters(name = "{index}: testHSLAData({0}) = ({1},{2},{3},{4},{5})")
    public static Collection<Object[]> hsladata() {
        Object[][] validValues = { { "hsla(0,0,0,0)", 0, 0, 0, 0, true },
                { "HSLA(0, 0, 0, 0)", 0, 0, 0, 0, true },
                { "hsla(0,0%,0%, 0.1)", 0, 0, 0, 25, true },
                { "hsla(0 0 0 0.00 )", 0, 0, 0, 0, true },
                { "hsla(0 0% 0% 0.50)", 0, 0, 0, 127, true },
                { "hsla(360,100,100,1.0)", 360, 100, 100, 255, true },
                { "hsla(360, 100, 100, 1.0)", 360, 100, 100, 255, true },
                { "hsla(360, 100%, 100%, 1.00)", 360, 100, 100, 255, true },
                { "hsla(360 100% 100% 1.00)", 360, 100, 100, 255, true },
                { "hsla(20, 10, 10, 0.24)", 20, 10, 10, 61, true },
                { "hsla(100, 0, 50, 0.8)", 100, 0, 50, 204, true },
                { "hsla(269, 50, 0, .6)", 269, 50, 0, 153, true }, };
        Object[][] invalidValues = { { "hsla(361,0,0,0)", 0, 0, 0, 0, false },
                { "hsla(0.0, 0, 0, 0)", 0, 0, 0, 0, false },
                { "hsla(0,0%,0%, 1.1)", 0, 0, 0, 0, false },
                { "hsla(0 0 0 0.009 )", 0, 0, 0, 0, false },
                { "hsla(0 0% -100% 0.50)", 0, 0, 0, 0, false },
                { "hsla(360,1000,100,1.0)", 0, 0, 0, 0, false },
                { "hsla(0, 100, 100, 2.0)", 0, 0, 0, 0, false },
                { "hsla(360, 100%, 100%, 10.00)", 0, 0, 0, 0, false },
                { "hsl a(360 100% 100% 1.)", 0, 0, 0, 0, false },
                { "hsla(20, -10, 10, 0.24)", 0, 0, 0, 0, false },
                { "hsla(400, 0, 50, 0.8)", 0, 0, 0, 0, false },
                { "hsla(200, 50, 0, 0.996)", 0, 0, 0, 0, false },
                { "hsla 200, 50, 0, 0.9", 0, 0, 0, 0, false },
                { "hsla(0,0,0,0.)", 0, 0, 0, 0, false } };

        ArrayList<Object[]> values = new ArrayList<>();
        Collections.addAll(values, validValues);
        Collections.addAll(values, invalidValues);

        return values;
    }

    @Test
    public void testHSLAData() {
        Matcher m = ColorUtil.HSLA_PATTERN.matcher(input);
        boolean matches = m.matches();
        if (expectedMatches) {
            Color expectedColor = new Color(Color.HSLtoRGB(expectedHue,
                    expectedSaturation, expectedLight));
            expectedColor.setAlpha(expectedAlpha);
            Color c1 = ColorUtil.getHSLAPatternColor(m);
            assertTrue(expectedColor.equals(c1));
        } else {
            assertTrue(!matches);
        }
    }

}
