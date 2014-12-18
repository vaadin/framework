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

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test case for writing the attributes of the AbstractField to design
 * 
 * @author Vaadin Ltd
 */
public class TestWriteDesign extends TestCase {

    private DesignContext ctx;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = new DesignContext();
    }

    public void testSynchronizeReadOnly() {
        Element design = createDesign();
        AbstractField component = getComponent();
        component.setReadOnly(true);
        component.writeDesign(design, ctx);
        // we only changed one of the attributes, others are at default values
        assertEquals(1, design.attributes().size());
        assertTrue("Design must contain readonly", design.hasAttr("readonly"));
        assertTrue("Readonly must be true", design.attr("readonly").equals("")
                || design.attr("readonly").equals("true"));
    }

    public void testSynchronizeModelReadOnly() {
        Element design = createDesign();
        AbstractField component = getComponent();
        ObjectProperty property = new ObjectProperty<String>("test");
        property.setReadOnly(true);
        component.setPropertyDataSource(property);
        component.writeDesign(design, ctx);
        // make sure that property readonly is not written to design
        assertFalse("Design must not contain readonly",
                design.hasAttr("readonly"));
    }

    private AbstractField getComponent() {
        return new TextField();
    }

    private Element createDesign() {
        Attributes attr = new Attributes();
        attr.put("should_be_removed", "foo");
        return new Element(Tag.valueOf("v-text-field"), "", attr);
    }
}
