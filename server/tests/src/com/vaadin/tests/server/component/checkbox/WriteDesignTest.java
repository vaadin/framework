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
package com.vaadin.tests.server.component.checkbox;

import junit.framework.TestCase;

import org.jsoup.nodes.Element;
import org.junit.Test;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Tests generating html tree nodes corresponding to the contents of a Checkbox
 */
public class WriteDesignTest extends TestCase {

    private DesignContext ctx;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = new DesignContext();
    }

    @Test
    public void testChecked() {
        CheckBox box = new CheckBox();
        box.setValue(true);
        Element e = ctx.createElement(box);
        assertTrue("element must have checked attribute", e.hasAttr("checked"));
        assertTrue("the checked attribute must be true", e.attr("checked")
                .equals("true") || e.attr("checked").equals(""));
    }

    @Test
    public void testUnchecked() {
        CheckBox box = new CheckBox();
        box.setValue(false);
        Element e = ctx.createElement(box);
        assertFalse("the element must not have checked attribute",
                e.hasAttr("checked"));
    }
}