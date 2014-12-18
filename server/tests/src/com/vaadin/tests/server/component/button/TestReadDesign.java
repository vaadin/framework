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
package com.vaadin.tests.server.component.button;

import java.lang.reflect.Field;

import junit.framework.TestCase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Test;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.declarative.DesignContext;

/**
 * 
 * Test cases for reading the contents of a Button and a NativeButton from a
 * design.
 * 
 */
public class TestReadDesign extends TestCase {

    private DesignContext ctx;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = new DesignContext();
    }

    @Test
    public void testWithContent() {
        createAndTestButtons("Click", null);
    }

    @Test
    public void testWithHtmlCaption() {
        createAndTestButtons("<b>Click me</b>", null);
    }

    @Test
    public void testWithContentAndCaption() {
        createAndTestButtons("Click me", "caption");
    }

    @Test
    public void testWithCaption() {
        createAndTestButtons(null, "Click me");
    }

    @Test
    public void testAttributes() throws IllegalArgumentException,
            SecurityException, IllegalAccessException, NoSuchFieldException {
        Attributes attributes = new Attributes();
        attributes.put("tabindex", "3");
        attributes.put("plain-text", "");
        attributes.put("icon-alt", "OK");
        attributes.put("click-shortcut", "ctrl-shift-o");
        Button button = (Button) ctx
                .readDesign(createButtonWithAttributes(attributes));
        assertEquals(3, button.getTabIndex());
        assertEquals(false, button.isHtmlContentAllowed());
        assertEquals("OK", button.getIconAlternateText());
        Field field = Button.class.getDeclaredField("clickShortcut");
        field.setAccessible(true);
        ClickShortcut value = (ClickShortcut) field.get(button);
        assertEquals(KeyCode.O, value.getKeyCode());
        assertEquals(ModifierKey.CTRL, value.getModifiers()[0]);
        assertEquals(ModifierKey.SHIFT, value.getModifiers()[1]);
    }

    /*
     * Test both Button and NativeButton. Caption should always be ignored. If
     * content is null, the created button should have empty content.
     */
    private void createAndTestButtons(String content, String caption) {
        Element e1 = createElement("v-button", content, caption);
        Button b1 = (Button) ctx.readDesign(e1);
        Element e2 = createElement("v-native-button", content, caption);
        NativeButton b2 = (NativeButton) ctx.readDesign(e2);
        if (content != null) {
            assertEquals("The button has the wrong text content.", content,
                    b1.getCaption());
            assertEquals("The button has the wrong text content.", content,
                    b2.getCaption());
        } else {
            assertTrue("The button has the wrong content.",
                    b1.getCaption() == null || "".equals(b1.getCaption()));
            assertTrue("The button has the wrong content.",
                    b2.getCaption() == null || "".equals(b2.getCaption()));
        }
    }

    private Element createButtonWithAttributes(Attributes attributes) {
        return new Element(Tag.valueOf("v-button"), "", attributes);
    }

    private Element createElement(String elementName, String content,
            String caption) {
        Attributes attributes = new Attributes();
        if (caption != null) {
            attributes.put("caption", caption);
        }
        Element node = new Element(Tag.valueOf(elementName), "", attributes);
        if (content != null) {
            node.html(content);
        }
        return node;
    }
}