package com.vaadin.tests.server.component.colorpicker;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.shared.ui.colorpicker.Color;

public class ColorConversionsTest {

    @Test
    public void convertHSL2RGB() {

        int rgb = Color.HSLtoRGB(100, 50, 50);
        Color c = new Color(rgb);
        assertEquals(106, c.getRed());
        assertEquals(191, c.getGreen());
        assertEquals(64, c.getBlue());
        assertEquals("#6abf40", c.getCSS());

        rgb = Color.HSLtoRGB(0, 50, 50);
        c = new Color(rgb);
        assertEquals(191, c.getRed());
        assertEquals(64, c.getGreen());
        assertEquals(64, c.getBlue());
        assertEquals("#bf4040", c.getCSS());

        rgb = Color.HSLtoRGB(50, 0, 50);
        c = new Color(rgb);
        assertEquals(128, c.getRed());
        assertEquals(128, c.getGreen());
        assertEquals(128, c.getBlue());
        assertEquals("#808080", c.getCSS());

        rgb = Color.HSLtoRGB(50, 100, 0);
        c = new Color(rgb);
        assertEquals(0, c.getRed(), 0);
        assertEquals(0, c.getGreen(), 0);
        assertEquals(0, c.getBlue(), 0);
        assertEquals("#000000", c.getCSS());
    }
}
