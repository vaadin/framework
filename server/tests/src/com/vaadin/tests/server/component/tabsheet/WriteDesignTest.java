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
package com.vaadin.tests.server.component.tabsheet;

import junit.framework.TestCase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.TextField;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test case for writing TabSheet to design
 * 
 * @since
 * @author Vaadin Ltd
 */
public class WriteDesignTest extends TestCase {

    private TabSheet sheet;
    private Element design;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sheet = createTabSheet();
        design = createDesign();
        sheet.writeDesign(design, createDesignContext());
    }

    public void testOnlyOneTab() {
        assertEquals("There should be only one child", 1, design.children()
                .size());
    }

    public void testAttributes() {
        Element tabDesign = design.child(0);
        assertEquals("5", design.attr("tabindex"));
        assertEquals("test-caption", tabDesign.attr("caption"));
        assertEquals("false", tabDesign.attr("visible"));
        assertTrue(tabDesign.hasAttr("closable"));
        assertTrue(tabDesign.attr("closable").equals("true")
                || tabDesign.attr("closable").equals(""));
        assertEquals("false", tabDesign.attr("enabled"));
        assertEquals("http://www.vaadin.com/test.png", tabDesign.attr("icon"));
        assertEquals("OK", tabDesign.attr("icon-alt"));
        assertEquals("test-desc", tabDesign.attr("description"));
        assertEquals("test-style", tabDesign.attr("style-name"));
        assertEquals("test-id", tabDesign.attr("id"));
    }

    public void testContent() {
        Element tabDesign = design.child(0);
        Element content = tabDesign.child(0);
        assertEquals("Tab must have only one child", 1, tabDesign.children()
                .size());
        assertEquals("v-text-field", content.tagName());
    }

    private Element createDesign() {
        // make sure that the design node has old content that should be removed
        Element node = new Element(Tag.valueOf("v-tab-sheet"), "",
                new Attributes());
        node.appendChild(new Element(Tag.valueOf("tab"), "", new Attributes()));
        node.appendChild(new Element(Tag.valueOf("tab"), "", new Attributes()));
        node.appendChild(new Element(Tag.valueOf("tab"), "", new Attributes()));
        return node;
    }

    private DesignContext createDesignContext() {
        return new DesignContext();
    }

    private TabSheet createTabSheet() {
        TabSheet sheet = new TabSheet();
        sheet.setTabIndex(5);
        sheet.addTab(new TextField());
        Tab tab = sheet.getTab(0);
        tab.setCaption("test-caption");
        tab.setVisible(false);
        tab.setClosable(true);
        tab.setEnabled(false);
        tab.setIcon(new ExternalResource("http://www.vaadin.com/test.png"));
        tab.setIconAlternateText("OK");
        tab.setDescription("test-desc");
        tab.setStyleName("test-style");
        tab.setId("test-id");
        return sheet;
    }

}
