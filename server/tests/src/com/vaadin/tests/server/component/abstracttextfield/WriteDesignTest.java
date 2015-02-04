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
 * Test case for writing the attributes of the AbstractTextField to design
 * 
 * @author Vaadin Ltd
 */
public class WriteDesignTest extends TestCase {

    private DesignContext ctx;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = new DesignContext();
    }

    public void testSynchronizetestAttributes() {
        Element design = createDesign();
        AbstractTextField component = getComponent();
        component.setNullRepresentation("this-is-null");
        component.setNullSettingAllowed(true);
        component.setMaxLength(5);
        component.setColumns(3);
        component.setInputPrompt("input");
        component.setTextChangeEventMode(TextChangeEventMode.EAGER);
        component.setTextChangeTimeout(100);
        component.writeDesign(design, ctx);
        assertEquals("this-is-null", design.attr("null-representation"));
        assertEquals("true", design.attr("null-setting-allowed"));
        assertEquals("5", design.attr("maxlength"));
        assertEquals("3", design.attr("columns"));
        assertEquals("input", design.attr("input-prompt"));
        assertEquals("eager", design.attr("text-change-event-mode"));
        assertEquals("100", design.attr("text-change-timeout"));
    }

    private AbstractTextField getComponent() {
        return new TextField();
    }

    private Element createDesign() {
        Attributes attr = new Attributes();
        return new Element(Tag.valueOf("v-text-field"), "", attr);
    }

}
