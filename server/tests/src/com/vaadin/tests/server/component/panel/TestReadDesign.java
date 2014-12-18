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
package com.vaadin.tests.server.component.panel;

import junit.framework.TestCase;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;

/**
 * Test case for reading the attributes of a Panel from design.
 * 
 * @author Vaadin Ltd
 */
public class TestReadDesign extends TestCase {
    DesignContext ctx;

    @Override
    public void setUp() {
        ctx = new DesignContext();
    }

    public void testAttributes() {
        Element design = createDesign();
        Panel panel = new Panel();
        panel.readDesign(design, ctx);
        assertEquals("A panel", panel.getCaption());
        assertEquals(2, panel.getTabIndex());
        assertEquals(10, panel.getScrollLeft());
        assertEquals(20, panel.getScrollTop());
        assertEquals(200f, panel.getWidth());
        assertEquals(150f, panel.getHeight());
    }

    public void testChild() {
        Element design = createDesign();
        Panel panel = new Panel();
        panel.readDesign(design, ctx);
        VerticalLayout vLayout = (VerticalLayout) panel.getContent();
        assertEquals(300f, vLayout.getWidth());
        assertEquals(400f, vLayout.getHeight());
    }

    public void testWithMoreThanOneChild() {
        Element design = createDesign();
        // Add a new child to the panel element. An exception should be
        // thrown when parsing the design.
        Element newChild = new Element(Tag.valueOf("v-horizontal-layout"), "");
        design.appendChild(newChild);
        Panel panel = new Panel();
        try {
            panel.readDesign(design, ctx);
            fail("Parsing a design containing a Panel with more than one child component should have failed.");
        } catch (DesignException e) {
            // Nothing needs to be done, this is the expected case.
        }
    }

    /*
     * Creates an html document that can be parsed into a valid component
     * hierarchy.
     */
    private Element createDesign() {
        // Create a node defining a Panel
        Element panelElement = new Element(Tag.valueOf("v-panel"), "");
        panelElement.attr("caption", "A panel");
        panelElement.attr("tabindex", "2");
        panelElement.attr("scroll-left", "10");
        panelElement.attr("scroll-top", "20");
        panelElement.attr("width", "200px");
        panelElement.attr("height", "150px");
        // Add some content to the panel
        Element layoutElement = new Element(Tag.valueOf("v-vertical-layout"),
                "");
        layoutElement.attr("width", "300px");
        layoutElement.attr("height", "400px");
        panelElement.appendChild(layoutElement);
        return panelElement;
    }
}