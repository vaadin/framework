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
package com.vaadin.tests.server.component.textarea;

import junit.framework.TestCase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test case for reading the value of the TextField from design
 * 
 * @author Vaadin Ltd
 */
public class TestSynchronizeFromDesign extends TestCase {
    private DesignContext ctx;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = new DesignContext();
    }

    public void testValue() {
        Element design = createDesign();
        AbstractTextField component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertEquals("test value", component.getValue());
    }

    private AbstractTextField getComponent() {
        return new TextArea();
    }

    private Element createDesign() {
        Attributes attributes = new Attributes();
        Element node = new Element(Tag.valueOf("v-text-area"), "", attributes);
        node.html("test value");
        return node;
    }
}
