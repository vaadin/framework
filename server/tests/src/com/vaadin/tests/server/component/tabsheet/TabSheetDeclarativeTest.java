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

import org.junit.Test;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.TextField;

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
    public void tabsNotShown() {
        String design = "<v-tab-sheet tabs-visible=\"false\">\n"
                + "  <tab caption=\"My Tab\" selected=\"true\">\n"
                + "    <v-label>My Content</v-label>\n" + "  </tab>\n"
                + "</v-tab-sheet>\n";
        TabSheet ts = new TabSheet();
        ts.setTabsVisible(false);
        Label l = new Label("My Content", ContentMode.HTML);
        Tab tab = ts.addTab(l);
        tab.setCaption("My Tab");
        ts.setSelectedTab(tab);
        testRead(design, ts);
        testWrite(design, ts);

    }
}