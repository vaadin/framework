package com.vaadin.tests.server;

import junit.framework.TestCase;

import com.vaadin.server.ClassResource;
import com.vaadin.ui.Embedded;

public class TestMimeTypes extends TestCase {

    public void testEmbeddedPDF() {
        Embedded e = new Embedded("A pdf", new ClassResource("file.pddf"));
        assertEquals("Invalid mimetype", "application/octet-stream",
                e.getMimeType());
        e = new Embedded("A pdf", new ClassResource("file.pdf"));
        assertEquals("Invalid mimetype", "application/pdf", e.getMimeType());
    }
}
