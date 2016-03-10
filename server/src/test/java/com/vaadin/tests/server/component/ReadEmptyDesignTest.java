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
package com.vaadin.tests.server.component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;

/**
 * Test cases for checking that reading a design with no elements in the html
 * body produces null as the root component.
 */
public class ReadEmptyDesignTest extends TestCase {
    InputStream is;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String html = createDesign().toString();
        is = new ByteArrayInputStream(html.getBytes());
    }

    public void testReadComponent() {
        Component root = Design.read(is);
        assertNull("The root component should be null.", root);
    }

    public void testReadContext() {
        DesignContext ctx = Design.read(is, null);
        assertNotNull("The design context should not be null.", ctx);
        assertNull("The root component should be null.", ctx.getRootComponent());
    }

    public void testReadContextWithRootParameter() {
        try {
            Component rootComponent = new VerticalLayout();
            DesignContext ctx = Design.read(is, rootComponent);
            fail("Reading a design with no elements should fail when a non-null root Component is specified.");
        } catch (DesignException e) {
            // This is the expected outcome, nothing to do.
        }
    }

    private Document createDesign() {
        Document doc = new Document("");
        DocumentType docType = new DocumentType("html", "", "", "");
        doc.appendChild(docType);
        Element html = doc.createElement("html");
        doc.appendChild(html);
        html.appendChild(doc.createElement("head"));
        html.appendChild(doc.createElement("body"));
        return doc;
    }
}