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
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test case for writing CssLayout to design
 * 
 * @author Vaadin Ltd
 */
public class TestSynchronizeToDesign extends TestCase {

    public void testSynchronizeEmptyLayout() {
        CssLayout layout = new CssLayout();
        layout.setCaption("changed-caption");
        Element design = createDesign();
        layout.synchronizeToDesign(design, createDesignContext());
        assertEquals(0, design.childNodes().size());
        assertEquals("changed-caption", design.attr("caption"));
    }

    public void testSynchronizeLayoutWithChildren() {
        CssLayout layout = new CssLayout();
        layout.addComponent(new Label("test-label"));
        layout.getComponent(0).setCaption("test-caption");
        layout.addComponent(new Label("test-label-2"));
        Element design = createDesign();
        layout.synchronizeToDesign(design, createDesignContext());
        assertEquals(2, design.childNodes().size());
        assertEquals("v-label", ((Element) design.childNode(0)).tagName());
        assertEquals("test-caption", design.childNode(0).attr("caption"));
    }

    private Element createDesign() {
        // make sure that the design node has old content that should be removed
        Attributes rootAttributes = new Attributes();
        rootAttributes.put("caption", "test-layout");
        Element node = new Element(Tag.valueOf("v-vertical-layout"), "",
                rootAttributes);
        Attributes firstChildAttributes = new Attributes();
        firstChildAttributes.put("caption", "test-label");
        Element firstChild = new Element(Tag.valueOf("v-label"), "",
                firstChildAttributes);
        node.appendChild(firstChild);

        Element secondChild = new Element(Tag.valueOf("v-button"), "",
                new Attributes());
        secondChild.html("test-button");
        node.appendChild(secondChild);
        return node;
    }

    private DesignContext createDesignContext() {
        return new DesignContext();
    }
}
