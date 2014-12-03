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
package com.vaadin.tests.server.component.abstractorderedlayout;

import junit.framework.TestCase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.DesignSynchronizable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test case from reading AbstractOrdered layouts from design
 * 
 * @since
 * @author Vaadin Ltd
 */
public class TestSynchronizeFromDesign extends TestCase {

    public void testChildCount() {
        VerticalLayout root = createLayout(0f);
        assertEquals(2, root.getComponentCount());
    }

    public void testAttributes() {
        VerticalLayout root = createLayout(0f);
        assertEquals("test-layout", root.getCaption());
        assertEquals("test-label", root.getComponent(0).getCaption());
        assertEquals("test-button", root.getComponent(1).getCaption());
    }

    public void testExpandRatio() {
        VerticalLayout root = createLayout(1f);
        assertEquals(1f, root.getExpandRatio(root.getComponent(0)));
        assertEquals(1f, root.getExpandRatio(root.getComponent(1)));

        root = createLayout(0f);
        assertEquals(0f, root.getExpandRatio(root.getComponent(0)));
        assertEquals(0f, root.getExpandRatio(root.getComponent(1)));
    }

    public void testAlignment() {
        VerticalLayout root = createLayout(0f, ":top", ":left");
        assertEquals(Alignment.TOP_LEFT,
                root.getComponentAlignment(root.getComponent(0)));
        root = createLayout(0f, ":middle", ":center");
        assertEquals(Alignment.MIDDLE_CENTER,
                root.getComponentAlignment(root.getComponent(0)));
        root = createLayout(0f, ":bottom", ":right");
        assertEquals(Alignment.BOTTOM_RIGHT,
                root.getComponentAlignment(root.getComponent(0)));

    }

    private VerticalLayout createLayout(float expandRatio, String... alignments) {
        DesignContext ctx = new DesignContext();
        Element design = createDesign(expandRatio, alignments);
        DesignSynchronizable child = ctx.createChild(design);
        return (VerticalLayout) child;
    }

    private Element createDesign(float expandRatio, String... alignments) {

        Attributes rootAttributes = new Attributes();
        rootAttributes.put("caption", "test-layout");
        Element node = new Element(Tag.valueOf("v-vertical-layout"), "",
                rootAttributes);

        Attributes firstChildAttributes = new Attributes();
        firstChildAttributes.put("caption", "test-label");
        firstChildAttributes.put(":expand", String.valueOf(expandRatio));
        for (String alignment : alignments) {
            firstChildAttributes.put(alignment, "");
        }
        Element firstChild = new Element(Tag.valueOf("v-label"), "",
                firstChildAttributes);
        node.appendChild(firstChild);

        Attributes secondChildAttributes = new Attributes();
        secondChildAttributes.put("caption", "test-button");
        secondChildAttributes.put(":expand", String.valueOf(expandRatio));
        for (String alignment : alignments) {
            secondChildAttributes.put(alignment, "");
        }
        Element secondChild = new Element(Tag.valueOf("v-button"), "",
                secondChildAttributes);
        node.appendChild(secondChild);
        return node;
    }
}
