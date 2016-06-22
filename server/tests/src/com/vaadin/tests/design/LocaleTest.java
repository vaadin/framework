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
package com.vaadin.tests.design;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Tests the handling of the locale property in parsing and html generation.
 * 
 * @author Vaadin Ltd
 */
public class LocaleTest {
    DesignContext ctx;

    @Before
    public void setUp() {
        ctx = new DesignContext();
    }

    /*
     * Checks that when the html corresponding to a component hierarchy is
     * constructed, the result only contains locale attributes for a component
     * if its locale differs from that of its parent.
     */
    @Test
    public void testHtmlGeneration() {
        // create a component hierarchy
        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setLocale(Locale.US);
        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.setLocale(Locale.ITALY);
        vLayout.addComponent(hLayout);
        Button b1 = new Button();
        b1.setLocale(Locale.ITALY);
        Button b2 = new Button();
        b2.setLocale(Locale.US);
        hLayout.addComponent(b1);
        hLayout.addComponent(b2);
        HorizontalLayout hlayout2 = new HorizontalLayout();
        hlayout2.setLocale(Locale.US);
        vLayout.addComponent(hlayout2);
        Label l = new Label();
        l.setLocale(Locale.US);
        hlayout2.addComponent(l);
        Label l2 = new Label();
        l2.setLocale(Locale.CANADA);
        hlayout2.addComponent(l2);
        ctx.setRootComponent(vLayout);
        // create the html tree corresponding to the component hierarchy
        Document doc = componentToDoc(ctx);
        // check the created html
        Element body = doc.body();
        Element evLayout = body.child(0);
        assertEquals("Wrong locale information.", "en_US",
                evLayout.attr("locale"));
        Element ehLayout = evLayout.child(0);
        assertEquals("Wrong locale information.", "it_IT",
                ehLayout.attr("locale"));
        Element eb1 = ehLayout.child(0);
        assertTrue(
                "The element should not have a locale specification, found locale "
                        + eb1.attr("locale"), "".equals(eb1.attr("locale")));
        Element eb2 = ehLayout.child(1);
        assertEquals("Wrong locale information.", "en_US", eb2.attr("locale"));
        Element ehLayout2 = evLayout.child(1);
        assertTrue(
                "The element should not have a locale specification, found locale "
                        + ehLayout2.attr("locale"),
                "".equals(ehLayout2.attr("locale")));
        Element el1 = ehLayout2.child(0);
        assertTrue(
                "The element should not have a locale specification, found locale "
                        + el1.attr("locale"), "".equals(el1.attr("locale")));
        Element el2 = ehLayout2.child(1);
        assertEquals("Wrong locale information.", "en_CA", el2.attr("locale"));
    }

    private Document componentToDoc(DesignContext dc) {
        // Create the html tree skeleton.
        Document doc = new Document("");
        DocumentType docType = new DocumentType("html", "", "", "");
        doc.appendChild(docType);
        Element html = doc.createElement("html");
        doc.appendChild(html);
        html.appendChild(doc.createElement("head"));
        Element body = doc.createElement("body");
        html.appendChild(body);
        dc.writePackageMappings(doc);

        // Append the design under <body> in the html tree. createNode
        // creates the entire component hierarchy rooted at the
        // given root node.
        Component root = dc.getRootComponent();
        Node rootNode = dc.createElement(root);
        body.appendChild(rootNode);
        return doc;

    }

    /*
     * Checks that the locale of a component is set when the html element
     * corresponding to the component specifies a locale.
     */
    @Test
    public void testParsing() {
        // create an html document
        Document doc = new Document("");
        DocumentType docType = new DocumentType("html", "", "", "");
        doc.appendChild(docType);
        Element html = doc.createElement("html");
        doc.appendChild(html);
        html.appendChild(doc.createElement("head"));
        Element body = doc.createElement("body");
        html.appendChild(body);
        Element evLayout = doc.createElement("vaadin-vertical-layout");
        evLayout.attr("locale", "en_US");
        body.appendChild(evLayout);
        Element ehLayout = doc.createElement("vaadin-horizontal-layout");
        evLayout.appendChild(ehLayout);
        Element eb1 = doc.createElement("vaadin-button");
        eb1.attr("locale", "en_US");
        ehLayout.appendChild(eb1);
        Element eb2 = doc.createElement("vaadin-button");
        eb2.attr("locale", "en_GB");
        ehLayout.appendChild(eb2);
        Element eb3 = doc.createElement("vaadin-button");
        ehLayout.appendChild(eb3);

        // parse the created document and check the constructed component
        // hierarchy
        String string = doc.html();
        VerticalLayout vLayout = (VerticalLayout) Design
                .read(new ByteArrayInputStream(string.getBytes()));
        assertEquals("Wrong locale.", new Locale("en", "US"),
                vLayout.getLocale());
        HorizontalLayout hLayout = (HorizontalLayout) vLayout.getComponent(0);
        assertEquals("The element should have the same locale as its parent.",
                vLayout.getLocale(), hLayout.getLocale());
        Button b1 = (Button) hLayout.getComponent(0);
        assertEquals("Wrong locale.", new Locale("en", "US"), b1.getLocale());
        Button b2 = (Button) hLayout.getComponent(1);
        assertEquals("Wrong locale.", new Locale("en", "GB"), b2.getLocale());
        Button b3 = (Button) hLayout.getComponent(2);
        assertEquals(
                "The component should have the same locale as its parent.",
                hLayout.getLocale(), b3.getLocale());
    }
}