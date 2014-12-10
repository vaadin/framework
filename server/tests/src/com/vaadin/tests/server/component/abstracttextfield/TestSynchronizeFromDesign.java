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
package com.vaadin.tests.server.component.abstracttextfield;

import junit.framework.TestCase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test case for reading the attributes of the AbstractTextField from design
 */
public class TestSynchronizeFromDesign extends TestCase {

    private DesignContext ctx;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = new DesignContext();
    }

    public void testAttributes() {
        Element design = createDesign();
        AbstractTextField component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertEquals("this-is-null", component.getNullRepresentation());
        assertEquals(true, component.isNullSettingAllowed());
        assertEquals(5, component.getMaxLength());
        assertEquals(3, component.getColumns());
        assertEquals("input", component.getInputPrompt());
        assertEquals(TextChangeEventMode.EAGER,
                component.getTextChangeEventMode());
        assertEquals(100, component.getTextChangeTimeout());
    }

    private AbstractTextField getComponent() {
        return new TextField();
    }

    private Element createDesign() {
        Attributes attributes = new Attributes();
        attributes.put("null-representation", "this-is-null");
        attributes.put("null-setting-allowed", "true");
        attributes.put("maxlength", "5");
        attributes.put("columns", "3");
        attributes.put("input-prompt", "input");
        attributes.put("text-change-event-mode", "eager");
        attributes.put("text-change-timeout", "100");
        Element node = new Element(Tag.valueOf("v-text-field"), "", attributes);
        return node;
    }

}
