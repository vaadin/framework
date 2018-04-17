package com.vaadin.tests.server.component;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;

/**
 * Test cases for checking that reading a design with no elements in the html
 * body produces null as the root component.
 */
public class ReadEmptyDesignTest {
    InputStream is;

    @Before
    public void setUp() {
        String html = createDesign().toString();
        is = new ByteArrayInputStream(html.getBytes());
    }

    @Test
    public void testReadComponent() {
        Component root = Design.read(is);
        assertNull("The root component should be null.", root);
    }

    @Test
    public void testReadContext() {
        DesignContext ctx = Design.read(is, null);
        assertNotNull("The design context should not be null.", ctx);
        assertNull("The root component should be null.",
                ctx.getRootComponent());
    }

    @Test
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
