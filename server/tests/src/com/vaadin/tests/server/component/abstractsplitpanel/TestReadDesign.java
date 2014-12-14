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

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.splitpanel.AbstractSplitPanelState.SplitterState;
import com.vaadin.ui.AbstractSplitPanel;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Tests synchronizing the attributes and children of horizontal and vertical
 * split panels from a design.
 * 
 * @author Vaadin Ltd
 */
public class TestReadDesign extends TestCase {
    DesignContext ctx;

    @Override
    protected void setUp() {
        ctx = new DesignContext();
    }

    public void testAttributes() throws Exception {
        // Create a design with non-default attributes values.
        Element design = createDesign(true, false, true, true);
        HorizontalSplitPanel sp = new HorizontalSplitPanel();
        sp.readDesign(design, ctx);
        // Check that the attributes are correctly parsed.
        assertEquals(20.5f, sp.getSplitPosition());
        assertEquals(Unit.PERCENTAGE, sp.getSplitPositionUnit());
        assertEquals(20f, sp.getMinSplitPosition());
        assertEquals(Unit.PERCENTAGE, sp.getMinSplitPositionUnit());
        assertEquals(50f, sp.getMaxSplitPosition());
        assertEquals(Unit.PIXELS, sp.getMaxSplitPositionUnit());
        assertEquals(true, sp.isLocked());
        checkReversed(sp, true);
        // check that the properties get the default values if the design
        // does not have attributes corresponding to those properties
        design = createDesign(true, true, true, true);
        sp.readDesign(design, ctx);
        HorizontalSplitPanel def = new HorizontalSplitPanel();
        assertEquals(def.getSplitPosition(), sp.getSplitPosition());
        assertEquals(def.getSplitPositionUnit(), sp.getSplitPositionUnit());
        assertEquals(def.getMinSplitPosition(), sp.getMinSplitPosition());
        assertEquals(def.getMinSplitPositionUnit(),
                sp.getMinSplitPositionUnit());
        assertEquals(def.getMaxSplitPosition(), sp.getMaxSplitPosition());
        assertEquals(def.getMaxSplitPositionUnit(),
                sp.getMaxSplitPositionUnit());
        assertEquals(def.isLocked(), sp.isLocked());
        checkReversed(sp, false);
    }

    public void testWithNoChildren() {
        Element design = createDesign(true, false, false, false);
        HorizontalSplitPanel sp = new HorizontalSplitPanel();
        sp.readDesign(design, ctx);
        assertEquals("Unexpected child count for the split panel.", 0,
                sp.getComponentCount());
    }

    public void testWithFirstChild() {
        Element design = createDesign(false, false, true, false);
        VerticalSplitPanel sp = new VerticalSplitPanel();
        sp.readDesign(design, ctx);
        assertEquals("Unexpected child count for the split panel.", 1,
                sp.getComponentCount());
        Object obj = sp.getFirstComponent();
        assertEquals("Wrong component in split panel.", Table.class,
                obj.getClass());
    }

    public void testWithSecondChild() {
        Element design = createDesign(true, false, false, true);
        HorizontalSplitPanel sp = new HorizontalSplitPanel();
        sp.readDesign(design, ctx);
        assertEquals("Unexpected child count for the split panel.", 1,
                sp.getComponentCount());
        Object obj = sp.getSecondComponent();
        assertEquals("Wrong component in split panel.", VerticalLayout.class,
                obj.getClass());
    }

    public void testWithBothChildren() {
        Element design = createDesign(false, false, true, true);
        VerticalSplitPanel sp = new VerticalSplitPanel();
        sp.readDesign(design, ctx);
        assertEquals("Unexpected child count for the split panel.", 2,
                sp.getComponentCount());
        Object first = sp.getFirstComponent();
        Object second = sp.getSecondComponent();
        assertEquals("Wrong first component in split panel.", Table.class,
                first.getClass());
        assertEquals("Wrong second component in split panel.",
                VerticalLayout.class, second.getClass());
    }

    /*
     * Creates an html tree node structure representing a split panel and its
     * contents. The parameters are used for controlling whether the split panel
     * is horizontal or vertical, whether attributes are set for the design and
     * whether the split panel should have the first and the second child
     * component.
     */
    private Element createDesign(boolean horizontal,
            boolean useDefaultAttributes, boolean hasFirstChild,
            boolean hasSecondChild) {
        Attributes attributes = new Attributes();
        if (!useDefaultAttributes) {
            attributes.put("split-position", "20.5%");
            // The unitless number should correspond to 20%
            attributes.put("min-split-position", "20");
            attributes.put("max-split-position", "50px");
            attributes.put("locked", "");
            attributes.put("reversed", "");
        }
        String tagName = horizontal ? "v-horizontal-split-panel"
                : "v-vertical-split-panel";
        Element element = new Element(Tag.valueOf(tagName), "", attributes);
        // Create the children
        if (hasFirstChild) {
            Element child = new Element(Tag.valueOf("v-table"), "");
            element.appendChild(child);
        }
        if (hasSecondChild) {
            Element child = new Element(Tag.valueOf("v-vertical-layout"), "");
            if (!hasFirstChild) {
                child.attr(":second", "");
            }
            element.appendChild(child);
        }
        return element;
    }

    /*
     * Checks the reversed property of a split panel.
     */
    private void checkReversed(AbstractSplitPanel sp, boolean expected)
            throws Exception {
        Method getter = AbstractSplitPanel.class
                .getDeclaredMethod("getSplitterState");
        getter.setAccessible(true);
        SplitterState state = (SplitterState) getter.invoke(sp);
        assertEquals("Wrong value for split panel property reversed.",
                expected, state.positionReversed);
    }
}