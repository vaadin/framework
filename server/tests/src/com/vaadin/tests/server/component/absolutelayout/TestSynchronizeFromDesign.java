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
package com.vaadin.tests.server.component.absolutelayout;

import java.util.Iterator;

import junit.framework.TestCase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test case for reading AbsoluteLayout from design
 * 
 * @since
 * @author Vaadin Ltd
 */
public class TestSynchronizeFromDesign extends TestCase {

    private AbsoluteLayout root;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        root = createLayout();
    }

    public void testAttributes() {
        assertEquals("test-layout", root.getCaption());
        Iterator<Component> children = root.iterator();
        assertEquals("test-label", children.next().getCaption());
        assertEquals("test-button", children.next().getCaption());
    }

    public void testTopLeftPosition() {
        ComponentPosition position = root.getPosition(root.iterator().next());
        assertEquals(Sizeable.Unit.PIXELS, position.getTopUnits());
        assertEquals(100.0f, position.getTopValue());
        assertEquals(Sizeable.Unit.PERCENTAGE, position.getLeftUnits());
        assertEquals(50.0f, position.getLeftValue());
    }

    public void testBottomRightPosition() {
        Iterator<Component> children = root.iterator();
        children.next();
        ComponentPosition position = root.getPosition(children.next());
        assertEquals(Sizeable.Unit.PIXELS, position.getBottomUnits());
        assertEquals(100.0f, position.getBottomValue());
        assertEquals(Sizeable.Unit.PERCENTAGE, position.getRightUnits());
        assertEquals(50.0f, position.getRightValue());
    }

    public void testZIndex() {
        ComponentPosition position = root.getPosition(root.iterator().next());
        assertEquals(2, position.getZIndex());
    }

    private AbsoluteLayout createLayout() {
        DesignContext ctx = new DesignContext();
        Element design = createDesign();
        Component child = ctx.createChild(design);
        return (AbsoluteLayout) child;
    }

    private Element createDesign() {

        Attributes rootAttributes = new Attributes();
        rootAttributes.put("caption", "test-layout");
        Element node = new Element(Tag.valueOf("v-absolute-layout"), "",
                rootAttributes);

        Attributes firstChildAttributes = new Attributes();
        firstChildAttributes.put("caption", "test-label");
        firstChildAttributes.put(":top", "100px");
        firstChildAttributes.put(":left", "50%");
        firstChildAttributes.put(":z-index", "2");
        Element firstChild = new Element(Tag.valueOf("v-label"), "",
                firstChildAttributes);
        node.appendChild(firstChild);

        Attributes secondChildAttributes = new Attributes();
        secondChildAttributes.put(":bottom", "100px");
        secondChildAttributes.put(":right", "50%");
        Element secondChild = new Element(Tag.valueOf("v-button"), "",
                secondChildAttributes);
        secondChild.html("test-button");
        node.appendChild(secondChild);
        return node;
    }
}
