/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
public class NestedCustomLayoutsTest {

    private static final String PACKAGE_MAPPING = "com_vaadin_tests_design_nested_customlayouts:com.vaadin.tests.design.nested.customlayouts";

    @Test
    public void testNestedLayouts() throws IOException {
        VerticalLayout rootLayout = createRootLayout();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Design.write(rootLayout, out);
        Document doc = Jsoup.parse(out.toString(UTF_8.name()));

        assertEquals("package-mapping", doc.head().child(0).attr("name"));
        assertEquals(PACKAGE_MAPPING, doc.head().child(0).attr("content"));
        assertChildrenCount(doc);
    }

    private VerticalLayout createRootLayout() {
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

        return rootLayout;
    }

    private void assertChildrenCount(Document doc) {
        Element rootNode = doc.body().child(0);
        assertFalse("Children should not be empty",
                rootNode.children().isEmpty());

        for (Element child : rootNode.children()) {
            // make sure that the nested custom layouts do not render children
            assertTrue(child.children().isEmpty());
        }
    }
}
