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
package com.vaadin.tests.server.component.textfield;

import junit.framework.TestCase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test case for writing the value of the TextField to design
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

    public void testSynchronizeValue() {
        Element design = createDesign();
        AbstractTextField component = getComponent();
        component.setValue("test value");
        component.writeDesign(design, ctx);
        assertEquals("test value", design.attr("value"));
    }

    private AbstractTextField getComponent() {
        return new TextField();
    }

    private Element createDesign() {
        Attributes attr = new Attributes();
        return new Element(Tag.valueOf("v-text-field"), "", attr);
    }

}
