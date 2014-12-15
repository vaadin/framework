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

import junit.framework.TestCase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.vaadin.ui.declarative.DesignContext;

/**
 * 
 * Test case for writing nested templates
 * 
 * @author Vaadin Ltd
 */
public class TestWriteNestedTemplates extends TestCase {

    private MyDesignRoot root;
    private Element design;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        root = new MyDesignRoot();
        design = createDesign();
        DesignContext designContext = new DesignContext();
        designContext.setRootComponent(root);
        root.writeDesign(design, designContext);
    }

    public void testChildRendered() {
        assertEquals("Root layout must have one child", 1, design.children()
                .size());
        assertEquals("com_vaadin_tests_design_nested-my-extended-child-design",
                design.child(0).tagName());
    }

    public void testRootCaptionWritten() {
        assertTrue("Root layout caption must be written",
                design.hasAttr("caption"));
        assertEquals("Root layout caption must be written", "root caption",
                design.attr("caption"));
    }

    public void testChildCaptionWritten() {
        assertTrue("Child design caption must be written", design.child(0)
                .hasAttr("caption"));
        assertEquals("Child design caption must be written", "child caption",
                design.child(0).attr("caption"));
    }

    // The default caption is read from child template
    public void testDefaultCaptionShouldNotBeWritten() {
        design = createDesign();
        root.childDesign.setCaption("Default caption for child design");
        DesignContext designContext = new DesignContext();
        designContext.setRootComponent(root);
        root.writeDesign(design, designContext);
        assertFalse("Default caption must not be written", design.child(0)
                .hasAttr("caption"));
    }

    public void testChildDesignChildrenNotWrittenInRootTemplate() {
        assertEquals(
                "Children of the child template must not be written to root template",
                0, design.child(0).children().size());
    }

    private Element createDesign() {
        return new Element(Tag.valueOf("v-vertical-layout"), "",
                new Attributes());
    }
}
