package com.vaadin.tests.server;

import junit.framework.TestCase;

import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.ui.Embedded;

public class TestMimeTypes extends TestCase {

    public void testEmbeddedPDF() {
        Application app = new Application() {

            @Override
            public void init() {
                // TODO Auto-generated method stub

            }
        };
        Embedded e = new Embedded("A pdf", new ClassResource("file.pddf", app));
        assertEquals("Invalid mimetype", "application/octet-stream",
                e.getMimeType());
        e = new Embedded("A pdf", new ClassResource("file.pdf", app));
        assertEquals("Invalid mimetype", "application/pdf", e.getMimeType());
    }
}
