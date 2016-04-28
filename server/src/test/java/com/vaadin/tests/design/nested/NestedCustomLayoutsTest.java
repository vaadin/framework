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

import com.vaadin.tests.design.nested.customlayouts.*;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Test case for nested custom layouts. The children of the custom layouts must
 * not be rendered.
 * 
 * @author Vaadin Ltd
 */
public class NestedCustomLayoutsTest {

    private static String PACKAGE_MAPPING = "com_vaadin_tests_design_nested_customlayouts:com.vaadin.tests.design.nested.customlayouts";

    @Test
    public void testNestedLayouts() throws IOException {
        VerticalLayout rootLayout = createRootLayout();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Design.write(rootLayout, out);
        Document doc = Jsoup.parse(out.toString("UTF-8"));

        assertThat(doc.head().child(0).attr("name"), is("package-mapping"));
        assertThat(doc.head().child(0).attr("content"), is(PACKAGE_MAPPING));
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
        assertThat(rootNode.children().size(), greaterThan(0));

        for (Element child : rootNode.children()) {
            // make sure that the nested custom layouts do not render children
            assertThat(child.children().size(), is(0));
        }
    }
}
