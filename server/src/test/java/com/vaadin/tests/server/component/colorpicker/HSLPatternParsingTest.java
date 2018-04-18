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
public class HSLPatternParsingTest {

    @Parameter(value = 0)
    public String input;

    @Parameter(value = 1)
    public Color expectedColor;

    @Parameter(value = 2)
    public boolean expectedMatches;

    @Parameters(name = "{index}: testHSLData({0}) = ({1},{2})")
    public static Collection<Object[]> hsldata() {
        Object[][] validValues = {
                { "hsl(0,0,0)", new Color(Color.HSLtoRGB(0, 0, 0)), true },
                { "hsl(0, 0, 0)", new Color(Color.HSLtoRGB(0, 0, 0)), true },
                { "hsl(0,0%,0% )", new Color(Color.HSLtoRGB(0, 0, 0)), true },
                { "hsl(0 0 0)", new Color(Color.HSLtoRGB(0, 0, 0)), true },
                { "hsl(0 0% 0%)", new Color(Color.HSLtoRGB(0, 0, 0)), true },
                { "hsl(360,100,100)", new Color(Color.HSLtoRGB(360, 100, 100)),
                        true },
                { "hsl(360, 100, 100)",
                        new Color(Color.HSLtoRGB(360, 100, 100)), true },
                { "hsl(360, 100%, 100%)",
                        new Color(Color.HSLtoRGB(360, 100, 100)), true },
                { "hsl(360 100% 100%)",
                        new Color(Color.HSLtoRGB(360, 100, 100)), true },
                { "hsl(20, 10, 10)", new Color(Color.HSLtoRGB(20, 10, 10)),
                        true },
                { "hsl(100, 0, 50)", new Color(Color.HSLtoRGB(100, 0, 50)),
                        true },
                { "hsl(200, 50, 0)", new Color(Color.HSLtoRGB(200, 50, 0)),
                        true },
                { "hsl(200, 50, 05)", new Color(Color.HSLtoRGB(200, 50, 5)),
                        true } };
        Object[][] invalidValues = { { "hsl(361,0,0)", null, false },
                { "hsl(-0, 0, 0)", null, false },
                { "hsl (100%,0%,0% )", null, false },
                { "hsl(0 101 0)", null, false },
                { "hsl(0 0% -99%)", null, false },
                { "hsl(360,100,10 0)", null, false },
                { "hsl(360, 100, 101)", null, false },
                { "hsl(360, 110%, 100%)", null, false },
                { "hsl(3600 100% 100%)", null, false },
                { "hs l(420, 10, 10)", null, false },
                { "hsl(100, 0, 5,0)", null, false },
                { "hsla(200, 50, 0)", null, false },
                { "hsl(0,0,0", null, false }, { "rgb\\(\\.*", null, false },
                { "hsl(\\.*)", null, false }, { "#\\d.*", null, false },
                { "", null, false } };
        ArrayList<Object[]> values = new ArrayList<>();
        Collections.addAll(values, validValues);
        Collections.addAll(values, invalidValues);

        return values;
    }

    @Test
    public void testHSLData() {
        Matcher m = ColorUtil.HSL_PATTERN.matcher(input);
        boolean matches = m.matches();
        if (expectedMatches) {
            Color c1 = ColorUtil.getHSLPatternColor(m);
            assertTrue(expectedColor.equals(c1));
        } else {
            assertTrue(!matches);
        }
    }

}
