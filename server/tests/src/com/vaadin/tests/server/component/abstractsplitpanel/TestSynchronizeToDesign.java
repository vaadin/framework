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
package com.vaadin.tests.server.component.abstractsplitpanel;

import junit.framework.TestCase;

import org.jsoup.nodes.Element;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Tests synchronizing the properties and child components of split panels to a
 * design.
 * 
 * @author Vaadin Ltd
 */
public class TestSynchronizeToDesign extends TestCase {
    private DesignContext ctx;

    @Override
    public void setUp() {
        ctx = new DesignContext();
    }

    public void testHorizontalWithDefaultValues() {
        // no attributes or child elements should appear
        HorizontalSplitPanel sp = new HorizontalSplitPanel();
        Element e = ctx.createNode(sp);
        assertEquals("Wrong tag name.", "v-horizontal-split-panel",
                e.nodeName());
        assertEquals("The split panel should not have attributes.", 0, e
                .attributes().size());
        assertEquals("The split panel should not have children.", 0, e
                .children().size());
    }

    public void testVerticalWithAttributes() {
        // All defined attributes should be output in the tree node. No child
        // components are present in this test.
        VerticalSplitPanel sp = new VerticalSplitPanel();
        sp.setSplitPosition(27f, Unit.PIXELS, true);
        sp.setMinSplitPosition(5.5f, Unit.PERCENTAGE);
        sp.setMaxSplitPosition(95, Unit.PERCENTAGE);
        sp.setLocked(true);
        Element e = ctx.createNode(sp);
        assertEquals("Wrong tag name.", "v-vertical-split-panel", e.nodeName());
        assertEquals("Unexpected number of attributes.", 5, e.attributes()
                .size());
        assertEquals("Wrong split position.", "27px", e.attr("split-position"));
        assertEquals("Wrong minimum split position.", "5.5%",
                e.attr("min-split-position"));
        assertEquals("Wrong maximum split position.", "95%",
                e.attr("max-split-position"));
        assertTrue("Unexpected value for locked: " + e.attr("locked"),
                "true".equals(e.attr("locked")) || "".equals(e.attr("locked")));
        assertTrue(
                "Unexpected value for reversed: " + e.attr("reversed"),
                "true".equals(e.attr("reversed"))
                        || "".equals(e.attr("reversed")));
    }

    public void testHorizontalWithFirstChild() {
        // The split panel contains only the first child.
        HorizontalSplitPanel sp = new HorizontalSplitPanel();
        sp.setSplitPosition(25f);
        sp.setFirstComponent(new Button("First slot"));
        Element e = ctx.createNode(sp);
        assertEquals("Wrong split position.", "25%", e.attr("split-position"));
        assertEquals("Wrong number of child elements.", 1, e.children().size());
        Element eb = e.children().get(0);
        assertEquals("Wrong tag name of first child element.", "v-button",
                eb.nodeName());
        assertEquals("Wrong text in the button element.", "First slot",
                eb.html());
    }

    public void testVerticalWithSecondChild() {
        // The split panel contains only the second child.
        VerticalSplitPanel sp = new VerticalSplitPanel();
        sp.setMinSplitPosition(25f, Unit.PIXELS);
        sp.setSecondComponent(new Label("Second slot"));
        Element e = ctx.createNode(sp);
        assertEquals("Wrong minimum split position.", "25px",
                e.attr("min-split-position"));
        assertEquals("Wrong number of child elements.", 1, e.children().size());
        Element el = e.children().get(0);
        assertEquals("Wrong tag name of child element.", "v-label",
                el.nodeName());
        assertEquals("Wrong text in the label element.", "Second slot",
                el.html());
        assertTrue("Missing attribute :second in the label element.",
                el.hasAttr(":second"));
    }

    public void testVerticalWithBothChildren() {
        // The split panel has both child components.
        VerticalSplitPanel sp = new VerticalSplitPanel();
        sp.setFirstComponent(new Button("First slot"));
        sp.setSecondComponent(new Label("Second slot"));
        Element e = ctx.createNode(sp);
        assertEquals("Wrong number of child elements.", 2, e.children().size());
        Element eb = e.children().get(0);
        assertEquals("Wrong tag name of first child element.", "v-button",
                eb.nodeName());
        assertEquals("Wrong text in the button element.", "First slot",
                eb.html());
        Element el = e.children().get(1);
        assertEquals("Wrong tag name of second child element.", "v-label",
                el.nodeName());
        assertEquals("Wrong text in the label element.", "Second slot",
                el.html());
        assertFalse(
                "There should be no :second attribute when a split panel has both children.",
                el.hasAttr(":second"));
    }

    public void testReSynchronize() {
        // Test that old children and attributes are removed when an element is
        // synchronized to a new component.
        VerticalSplitPanel sp = new VerticalSplitPanel();
        sp.setMinSplitPosition(5.5f, Unit.PERCENTAGE);
        sp.setMaxSplitPosition(95, Unit.PERCENTAGE);
        sp.setFirstComponent(new Button("First slot"));
        sp.setSecondComponent(new Label("Second slot"));
        Element e = ctx.createNode(sp);
        sp = new VerticalSplitPanel();
        sp.synchronizeToDesign(e, ctx);
        assertTrue("There should be no attributes in the node.", e.attributes()
                .size() == 0);
        assertTrue("There should be no child elements.",
                e.children().size() == 0);
    }
}
