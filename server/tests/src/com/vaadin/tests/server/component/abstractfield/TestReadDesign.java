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
package com.vaadin.tests.server.component.abstractfield;

import junit.framework.TestCase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.declarative.DesignContext;

/**
 * 
 * Test case for reading the attributes of the AbstractField from design
 * 
 * @author Vaadin Ltd
 */
public class TestReadDesign extends TestCase {

    private DesignContext ctx;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = new DesignContext();
    }

    public void testSynchronizeReadOnly() {
        Element design = createDesign("readonly", "");
        AbstractField component = getComponent();
        component.readDesign(design, ctx);
        assertEquals(true, component.isReadOnly());
        design = createDesign("readonly", "false");
        component.readDesign(design, ctx);
        assertEquals(false, component.isReadOnly());
    }

    public void testSynchronizeTabIndex() {
        Element design = createDesign("tabindex", "2");
        AbstractField component = getComponent();
        component.readDesign(design, ctx);
        assertEquals("Tab index must be 2", 2, component.getTabIndex());
    }

    private AbstractField getComponent() {
        return new TextField();
    }

    private Element createDesign(String key, String value) {
        Attributes attributes = new Attributes();
        attributes.put(key, value);
        Element node = new Element(Tag.valueOf("v-text-field"), "", attributes);
        return node;
    }
}
