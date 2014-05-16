/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.server.component.colorpicker;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.shared.ui.colorpicker.Color;

public class ColorConversions {

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
