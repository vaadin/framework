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

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Test;

import com.vaadin.server.ExternalResource;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.TextField;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Tests declarative support for TabSheet.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class TabSheetDeclarativeTest extends DeclarativeTestBase<TabSheet> {

    @Test
    public void testFeatures() {
        String design = "<v-tab-sheet tabindex=5><tab caption=test-caption "
                + "visible=false closable=true enabled=false icon=http://www.vaadin.com/test.png"
                + " icon-alt=OK description=test-desc style-name=test-style "
                + "id=test-id><v-text-field/></tab></v-tab-sheet>";
        TabSheet ts = new TabSheet();
        ts.setTabIndex(5);
        TextField tf = new TextField();
        Tab tab = ts.addTab(tf);
        tab.setCaption("test-caption");
        tab.setVisible(false);
        tab.setClosable(true);
        tab.setEnabled(false);
        tab.setIcon(new ExternalResource("http://www.vaadin.com/test.png"));
        tab.setIconAlternateText("OK");
        tab.setDescription("test-desc");
        tab.setStyleName("test-style");
        tab.setId("test-id");
        ts.setSelectedTab(tf);
        testRead(design, ts);
        testWrite(design, ts);
    }

    @Test
    public void testSelected() {
        String design = "<v-tab-sheet><tab selected=true><v-text-field/></tab></v-tab-sheet>";
        TabSheet ts = new TabSheet();
        TextField tf = new TextField();
        ts.addTab(tf);
        ts.setSelectedTab(tf);
        testRead(design, ts);
        testWrite(design, ts);
    }

    @Test
    public void testWriteRemovesOldContent() {
        // create old content that should be removed when writing
        Element design = new Element(Tag.valueOf("v-tab-sheet"), "",
                new Attributes());
        design.appendChild(new Element(Tag.valueOf("tab"), "", new Attributes()));
        design.appendChild(new Element(Tag.valueOf("tab"), "", new Attributes()));
        design.appendChild(new Element(Tag.valueOf("tab"), "", new Attributes()));
        // create a new TabSheet with one tab
        TabSheet ts = new TabSheet();
        ts.setTabIndex(5);
        ts.addTab(new TextField());
        Tab tab = ts.getTab(0);
        tab.setVisible(false);
        tab.setClosable(true);
        tab.setEnabled(false);
        // write the design and check written contents
        ts.writeDesign(design, new DesignContext());
        assertEquals("There should be only one child", 1, design.children()
                .size());
        assertEquals("v-text-field", design.child(0).child(0).tagName());
        assertEquals("5", design.attr("tabindex"));
        Element tabDesign = design.child(0);
        assertEquals("false", tabDesign.attr("visible"));
    }
}