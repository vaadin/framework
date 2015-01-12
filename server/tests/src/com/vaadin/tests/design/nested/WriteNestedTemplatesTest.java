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

import com.vaadin.ui.declarative.DesignContext;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * 
 * Test case for writing nested templates
 * 
 * @author Vaadin Ltd
 */
public class WriteNestedTemplatesTest {

    private MyDesignRoot root;
    private Element design;

    @Before
    public void setUp() {
        root = new MyDesignRoot();
        design = createDesign();
    }

    private Element createDesign() {
        Element design = new Element(Tag.valueOf("v-vertical-layout"), "",
                new Attributes());

        DesignContext designContext = new DesignContext();
        designContext.setRootComponent(root);
        root.writeDesign(design, designContext);

        return design;
    }

    @Test
    public void testChildRendered() {
        assertEquals("Root layout must have one child", 1, design.children()
                .size());
        assertEquals("com_vaadin_tests_design_nested-my-extended-child-design",
                design.child(0).tagName());
    }

    @Test
    public void rootCaptionIsWritten() {
        assertTrue(design.hasAttr("caption"));
        assertThat(design.attr("caption"), is("root caption"));
    }

    @Test
    public void childCaptionIsWritten() {
        assertTrue(design.child(0).hasAttr("caption"));
        assertThat(design.child(0).attr("caption"), is("child caption"));
    }

    // The default caption is read from child template
    @Test
    public void defaultCaptionIsNotOverwritten() {
        root.childDesign.setCaption("Default caption for child design");
        design = createDesign();

        assertFalse(design.child(0).hasAttr("caption"));
    }

    @Test
    public void childDesignChildrenIsNotWrittenInRootTemplate() {
        assertThat(design.child(0).children().size(), is(0));
    }
}
