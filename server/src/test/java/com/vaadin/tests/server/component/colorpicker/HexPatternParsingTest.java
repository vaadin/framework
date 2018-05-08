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
public class HexPatternParsingTest {

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

    @Parameters(name = "{index}: textValidHEX({0}) = ({1},{2},{3},{4})")
    public static Collection<Object[]> hexdata() {
        Object[][] validValues = { { "#000000", 0, 0, 0, true },
                { "#ffffff", 255, 255, 255, true },
                { "#FF00ff", 255, 0, 255, true },
                { "#aa90e3", 170, 144, 227, true },
                { "#016953", 1, 105, 83, true },
                { "#bC64D0", 188, 100, 208, true },
                { "#F100FF", 241, 0, 255, true },
                { "#F0E9a5", 240, 233, 165, true },
                { "#990077", 153, 0, 119, true } };
        Object[][] invalidValues = { { "#0000000", 0, 0, 0, false },
                { "#ffgfff", 0, 0, 0, false }, { "#FF10f", 0, 0, 0, false },
                { "#aa9", 0, 0, 0, false }, { "#03", 0, 0, 0, false },
                { "#aab3c4c", 0, 0, 0, false }, { "#6010", 0, 0, 0, false },
                { "#CCCC", 0, 0, 0, false }, { "#9", 0, 0, 0, false },
                { "#10 10 10", 0, 0, 0, false }, { "101010", 0, 0, 0, false },
                { "#10101q", 0, 0, 0, false },
                { "\\s%\\d[0-9]", 0, 0, 0, false },
                { "#\\d.*", 0, 0, 0, false }, { "rgb\\(\\.*", 0, 0, 0, false },
                { "#\\d\\d\\d", 0, 0, 0, false }, { "#\\d.*", 0, 0, 0, false },
                { "", 0, 0, 0, false }, { "hsl(25,25,25)", 0, 0, 0, false } };
        ArrayList<Object[]> values = new ArrayList<>();
        Collections.addAll(values, validValues);
        Collections.addAll(values, invalidValues);

        return values;
    }

    @Test
    public void testValidHEX() {
        Matcher m = ColorUtil.HEX_PATTERN.matcher(input);
        boolean matches = m.matches();
        if (expectedMatches) {
            Color c = new Color(expectedRed, expectedGreen, expectedBlue);
            Color c1 = ColorUtil.getHexPatternColor(m);
            assertTrue(c.equals(c1));
        } else {
            assertTrue(!matches);
        }
    }

}
