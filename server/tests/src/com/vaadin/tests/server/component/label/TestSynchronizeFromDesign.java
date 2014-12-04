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
package com.vaadin.tests.server.component.label;

import junit.framework.TestCase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Test;

import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.DesignContext;

/**
 * 
 * Test case for reading the contents of a Label from a design.
 * 
 */
public class TestSynchronizeFromDesign extends TestCase {

    private DesignContext ctx;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = new DesignContext();
    }

    @Test
    public void testWithContent() {
        createAndTestLabel("A label", null);
    }

    @Test
    public void testWithHtmlContent() {
        createAndTestLabel("<b>A label</b>", null);
    }

    @Test
    public void testWithContentAndCaption() {
        createAndTestLabel("A label", "This is a label");
    }

    @Test
    public void testWithCaption() {
        createAndTestLabel(null, "This is a label");
    }

    @Test
    public void testWithoutContentAndCaption() {
        createAndTestLabel(null, null);
    }

    /*
     * Test creating a Label. A Label can have both caption and content.
     */
    private void createAndTestLabel(String content, String caption) {
        Element e = createElement("v-label", content, caption);
        Label l = (Label) ctx.createChild(e);
        if (content != null) {
            assertEquals("The label has wrong text content.", content,
                    l.getValue());
        } else {
            assertTrue("The label has wrong text content.",
                    l.getValue() == null || "".equals(l.getValue()));
        }
        if (caption != null) {
            assertEquals("The label has wrong caption.", caption,
                    l.getCaption());
        } else {
            assertTrue("The label has wrong caption.", l.getCaption() == null
                    || "".equals(l.getCaption()));
        }
    }

    private Element createElement(String elementName, String content,
            String caption) {
        Attributes attributes = new Attributes();
        if (caption != null) {
            attributes.put("caption", caption);
        }
        Element node = new Element(Tag.valueOf(elementName), "", attributes);
        if (content != null) {
            node.html(content);
        }
        return node;
    }
}
