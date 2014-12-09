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
package com.vaadin.tests.server.component.csslayout;

import junit.framework.TestCase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DesignSynchronizable;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test case for reading CssLayout from design
 * 
 * @author Vaadin Ltd
 */
public class TestSynchronizeFromDesign extends TestCase {

    public void testChildCount() {
        CssLayout root = createLayout();
        assertEquals(2, root.getComponentCount());
    }

    public void testAttributes() {
        CssLayout root = createLayout();
        assertEquals("test-layout", root.getCaption());
        assertEquals("test-label", root.getComponent(0).getCaption());
        assertEquals("test-button", root.getComponent(1).getCaption());
    }

    private CssLayout createLayout() {
        DesignContext ctx = new DesignContext();
        Element design = createDesign();
        DesignSynchronizable child = ctx.createChild(design);
        return (CssLayout) child;
    }

    private Element createDesign() {

        Attributes rootAttributes = new Attributes();
        rootAttributes.put("caption", "test-layout");
        Element node = new Element(Tag.valueOf("v-css-layout"), "",
                rootAttributes);

        Attributes firstChildAttributes = new Attributes();
        firstChildAttributes.put("caption", "test-label");
        Element firstChild = new Element(Tag.valueOf("v-label"), "",
                firstChildAttributes);
        node.appendChild(firstChild);

        Attributes secondChildAttributes = new Attributes();
        Element secondChild = new Element(Tag.valueOf("v-button"), "",
                secondChildAttributes);
        secondChild.html("test-button");
        node.appendChild(secondChild);
        return node;
    }
}
