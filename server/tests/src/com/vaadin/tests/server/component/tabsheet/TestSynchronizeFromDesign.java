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
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.TextField;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test case from reading TabSheet from design
 * 
 * @since
 * @author Vaadin Ltd
 */
public class TestSynchronizeFromDesign extends TestCase {

    private TabSheet sheet;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sheet = createTabSheet();
    }

    public void testChildCount() {
        assertEquals(1, sheet.getComponentCount());
    }

    public void testTabIndex() {
        assertEquals(5, sheet.getTabIndex());
    }

    public void testTabAttributes() {
        Tab tab = sheet.getTab(0);
        assertEquals("test-caption", tab.getCaption());
        assertEquals(false, tab.isVisible());
        assertEquals(false, tab.isClosable());
        assertEquals(false, tab.isEnabled());
        assertEquals("http://www.vaadin.com/test.png",
                ((ExternalResource) tab.getIcon()).getURL());
        assertEquals("OK", tab.getIconAlternateText());
        assertEquals("test-desc", tab.getDescription());
        assertEquals("test-style", tab.getStyleName());
        assertEquals("test-id", tab.getId());
    }

    public void testSelectedComponent() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.synchronizeFromDesign(createFirstTabSelectedDesign(),
                new DesignContext());
        assertEquals(tabSheet.getTab(0).getComponent(),
                tabSheet.getSelectedTab());
    }

    public void testTabContent() {
        assertTrue("The child for the tabsheet should be textfield", sheet
                .getTab(0).getComponent() instanceof TextField);
    }

    private TabSheet createTabSheet() {
        TabSheet tabSheet = new TabSheet();
        // add some tabs that should be cleared on sync
        tabSheet.addComponent(new Label("tab1"));
        tabSheet.addComponent(new Label("tab2"));
        DesignContext ctx = new DesignContext();
        Element design = createDesign();
        tabSheet.synchronizeFromDesign(design, ctx);
        return tabSheet;
    }

    private Element createDesign() {
        // create root design
        Attributes rootAttributes = new Attributes();
        rootAttributes.put("tabindex", "5");
        Element node = new Element(Tag.valueOf("v-tab-sheet"), "",
                rootAttributes);
        // create tab design
        Attributes tabAttributes = new Attributes();
        tabAttributes.put("caption", "test-caption");
        tabAttributes.put("visible", "false");
        tabAttributes.put("closable", "false");
        tabAttributes.put("enabled", "false");
        tabAttributes.put("icon", "http://www.vaadin.com/test.png");
        tabAttributes.put("icon-alt", "OK");
        tabAttributes.put("description", "test-desc");
        tabAttributes.put("style-name", "test-style");
        tabAttributes.put("id", "test-id");
        Element tab = new Element(Tag.valueOf("tab"), "", tabAttributes);
        // add child component to tab
        tab.appendChild(new Element(Tag.valueOf("v-text-field"), "",
                new Attributes()));
        // add tab to root design
        node.appendChild(tab);
        return node;
    }

    private Element createFirstTabSelectedDesign() {
        // create root design
        Attributes rootAttributes = new Attributes();
        Element node = new Element(Tag.valueOf("v-tab-sheet"), "",
                rootAttributes);
        // create tab design
        Attributes tabAttributes = new Attributes();
        tabAttributes.put("selected", "");
        tabAttributes.put("caption", "test-caption");
        Element tab = new Element(Tag.valueOf("tab"), "", tabAttributes);
        // add child component to tab
        tab.appendChild(new Element(Tag.valueOf("v-text-field"), "",
                new Attributes()));
        // add tab to root design
        node.appendChild(tab);
        return node;

    }
}
