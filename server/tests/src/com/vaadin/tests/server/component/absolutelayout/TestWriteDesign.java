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

import junit.framework.TestCase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test case for writing AbsoluteLayout to design
 * 
 * @since
 * @author Vaadin Ltd
 */
public class TestWriteDesign extends TestCase {

    public void testSynchronizeEmptyLayout() {
        AbsoluteLayout layout = createTestLayout();
        layout.removeAllComponents();
        Element design = createDesign();
        layout.writeDesign(design, createDesignContext());
        assertEquals(0, design.childNodes().size());
        assertEquals("changed-caption", design.attr("caption"));
    }

    public void testSynchronizeLayoutWithChildren() {
        AbsoluteLayout layout = createTestLayout();
        Element design = createDesign();
        layout.writeDesign(design, createDesignContext());
        assertEquals(2, design.childNodes().size());
        assertEquals("v-label", ((Element) design.childNode(0)).tagName());
        assertEquals("v-label", ((Element) design.childNode(1)).tagName());
    }

    public void testSynchronizePosition() {
        AbsoluteLayout layout = createTestLayout();
        Element design = createDesign();
        layout.writeDesign(design, createDesignContext());
        Attributes attributes = design.childNode(0).attributes();
        assertEquals("50px", attributes.get(":top"));
        assertEquals("50%", attributes.get(":left"));
        assertEquals("2", attributes.get(":z-index"));
        attributes = design.childNode(1).attributes();
        assertEquals("50px", attributes.get(":bottom"));
        assertEquals("50%", attributes.get(":right"));
    }

    private AbsoluteLayout createTestLayout() {
        AbsoluteLayout layout = new AbsoluteLayout();
        layout.setCaption("changed-caption");
        layout.addComponent(new Label("test-label"),
                "top:50px;left:50%;z-index:2");
        layout.addComponent(new Label("test-label-2"),
                "bottom:50px;right:50%;z-index:3");
        return layout;
    }

    private Element createDesign() {
        // make sure that the design node has old content that should be removed
        Attributes rootAttributes = new Attributes();
        rootAttributes.put("caption", "test-layout");
        Element node = new Element(Tag.valueOf("v-absolute-layout"), "",
                rootAttributes);
        Attributes firstChildAttributes = new Attributes();
        firstChildAttributes.put("caption", "test-label");
        Element firstChild = new Element(Tag.valueOf("v-label"), "",
                firstChildAttributes);
        node.appendChild(firstChild);

        Attributes secondChildAttributes = new Attributes();
        secondChildAttributes.put("caption", "test-button");
        Element secondChild = new Element(Tag.valueOf("v-button"), "",
                secondChildAttributes);
        node.appendChild(secondChild);
        return node;
    }

    private DesignContext createDesignContext() {
        return new DesignContext();
    }
}
