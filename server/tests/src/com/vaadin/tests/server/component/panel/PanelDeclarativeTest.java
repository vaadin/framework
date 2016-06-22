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
package com.vaadin.tests.server.component.panel;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.DesignException;

/**
 * Tests declarative support for Panel.
 * 
 * @author Vaadin Ltd
 */
public class PanelDeclarativeTest extends DeclarativeTestBase<Panel> {

    @Test
    public void testFeatures() {
        String design = "<vaadin-panel id=panelId caption=\"A panel\" tabindex=2 scroll-left=10 "
                + "scroll-top=20 width=200px height=150px> "
                + "<vaadin-vertical-layout width=300px height=400px /> "
                + "</vaadin-panel>";
        Panel p = new Panel();
        p.setId("panelId");
        p.setCaption("A panel");
        p.setTabIndex(2);
        p.setScrollLeft(10);
        p.setScrollTop(20);
        p.setWidth("200px");
        p.setHeight("150px");
        VerticalLayout vl = new VerticalLayout();
        vl.setWidth("300px");
        vl.setHeight("400px");
        p.setContent(vl);
        testRead(design, p);
        testWrite(design, p);
    }

    @Test(expected = DesignException.class)
    public void testWithMoreThanOneChild() {
        // Check that attempting to have two components in a panel causes a
        // DesignException.
        String design = "<vaadin-panel> <vaadin-vertical-layout/> <vaadin-horizontal-layout/> </vaadin-panel>";
        testRead(design, null);
    }
}
