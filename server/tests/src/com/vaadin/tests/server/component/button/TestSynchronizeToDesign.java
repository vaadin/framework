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

import junit.framework.TestCase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Test;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Tests generating html tree nodes corresponding to the contents of a Button
 * and a NativeButton.
 */
public class TestSynchronizeToDesign extends TestCase {

    private DesignContext ctx;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = new DesignContext();
    }

    @Test
    public void testWithTextContent() {
        createAndTestButtons("Click me");
    }

    @Test
    public void testWithHtmlContent() {
        createAndTestButtons("<b>Click</b>");
    }

    @Test
    public void testAttributes() {
        Button button = new Button();
        button.setTabIndex(3);
        button.setIconAlternateText("OK");
        button.setClickShortcut(KeyCode.O, ModifierKey.CTRL, ModifierKey.SHIFT);
        Element e = new Element(Tag.valueOf("v-button"), "", new Attributes());
        button.synchronizeToDesign(e, ctx);
        assertEquals("3", e.attr("tabindex"));
        assertTrue("Button is plain text by default", e.hasAttr("plain-text"));
        assertEquals("OK", e.attr("icon-alt"));
        assertEquals("ctrl-shift-o", e.attr("click-shortcut"));
    }

    @Test
    public void testUpdateContentMode() {
        Button button = new Button("OK");
        Element e = new Element(Tag.valueOf("v-button"), "", new Attributes());
        button.synchronizeToDesign(e, ctx);
        assertTrue("Button is plain text by default", e.hasAttr("plain-text"));

        button.setHtmlContentAllowed(true);
        button.synchronizeToDesign(e, ctx);
        assertTrue("Button is updated to HTML", !e.hasAttr("plain-text"));

    }

    private void createAndTestButtons(String content) {
        Button b1 = new Button(content);
        // we need to set this on, since the plain-text attribute will appear
        // otherwise
        b1.setHtmlContentAllowed(true);
        Element e1 = ctx.createNode(b1);
        assertEquals("Wrong tag name for button.", "v-button", e1.tagName());
        assertEquals("Unexpected content in the v-button element.", content,
                e1.html());
        assertTrue("The v-button element should not have attributes.", e1
                .attributes().size() == 0);
        NativeButton b2 = new NativeButton(content);
        b2.setHtmlContentAllowed(true);
        Element e2 = ctx.createNode(b2);
        assertEquals("Wrong tag name for button.", "v-native-button",
                e2.tagName());
        assertEquals("Unexpected content in the v-button element.", content,
                e2.html());
        assertTrue("The v-button element should not have attributes.", e2
                .attributes().size() == 0);
    }
}