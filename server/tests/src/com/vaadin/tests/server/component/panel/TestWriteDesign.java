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

/**
 * Test case for writing the attributes and the child element of a Panel to a
 * design.
 * 
 * @author Vaadin Ltd
 */
public class TestWriteDesign extends TestCase {
    Element panelElement;

    @Override
    public void setUp() {
        // create a component hierarchy
        Panel panel = new Panel("A panel");
        panel.setId("panelId");
        panel.setHeight("250px");
        panel.setScrollTop(50);
        panel.setTabIndex(4);
        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setWidth("500px");
        panel.setContent(vLayout);
        // synchronize to design
        DesignContext ctx = new DesignContext();
        panelElement = new Element(Tag.valueOf("div"), "");
        panel.writeDesign(panelElement, ctx);
    }

    public void testAttributes() {
        // should have caption, id, height, scroll top and tab index
        assertEquals(5, panelElement.attributes().size());
        // check the values of the attributes
        assertEquals("A panel", panelElement.attr("caption"));
        assertEquals("panelId", panelElement.attr("id"));
        assertEquals("250px", panelElement.attr("height"));
        assertEquals("50", panelElement.attr("scroll-top"));
        assertEquals("4", panelElement.attr("tabindex"));
    }

    public void testChild() {
        // the panel element should have exactly one child, a v-vertical-layout
        assertEquals(1, panelElement.childNodes().size());
        Element vLayoutElement = panelElement.child(0);
        assertEquals("v-vertical-layout", vLayoutElement.nodeName());
        assertEquals("500px", vLayoutElement.attr("width"));
    }
}
