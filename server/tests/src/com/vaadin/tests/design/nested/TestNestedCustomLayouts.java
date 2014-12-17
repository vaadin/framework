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
package com.vaadin.tests.design.nested;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import com.vaadin.tests.design.nested.customlayouts.CustomAbsoluteLayout;
import com.vaadin.tests.design.nested.customlayouts.CustomAccordion;
import com.vaadin.tests.design.nested.customlayouts.CustomCssLayout;
import com.vaadin.tests.design.nested.customlayouts.CustomFormLayout;
import com.vaadin.tests.design.nested.customlayouts.CustomGridLayout;
import com.vaadin.tests.design.nested.customlayouts.CustomHorizontalLayout;
import com.vaadin.tests.design.nested.customlayouts.CustomHorizontalSplitPanel;
import com.vaadin.tests.design.nested.customlayouts.CustomPanel;
import com.vaadin.tests.design.nested.customlayouts.CustomTabSheet;
import com.vaadin.tests.design.nested.customlayouts.CustomVerticalLayout;
import com.vaadin.tests.design.nested.customlayouts.CustomVerticalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

/**
 * Test case for nested custom layouts. The children of the custom layouts must
 * not be rendered.
 * 
 * @author Vaadin Ltd
 */
public class TestNestedCustomLayouts extends TestCase {

    @Test
    public void testNestedLayouts() throws IOException {
        VerticalLayout rootLayout = new VerticalLayout();
        rootLayout.addComponent(new CustomAbsoluteLayout());
        rootLayout.addComponent(new CustomAccordion());
        rootLayout.addComponent(new CustomCssLayout());
        rootLayout.addComponent(new CustomFormLayout());
        rootLayout.addComponent(new CustomGridLayout());
        rootLayout.addComponent(new CustomHorizontalLayout());
        rootLayout.addComponent(new CustomHorizontalSplitPanel());
        rootLayout.addComponent(new CustomPanel());
        rootLayout.addComponent(new CustomTabSheet());
        rootLayout.addComponent(new CustomVerticalLayout());
        rootLayout.addComponent(new CustomVerticalSplitPanel());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Design.write(rootLayout, out);
        Document doc = Jsoup.parse(out.toString("UTF-8"));
        Element rootNode = doc.body().child(0);
        assertTrue("Root node must have children",
                rootNode.children().size() > 0);
        for (Element child : rootNode.children()) {
            // make sure that the nested custom layouts do not render children
            assertEquals("Child nodes must not have children", 0, child
                    .children().size());
        }
    }
}
