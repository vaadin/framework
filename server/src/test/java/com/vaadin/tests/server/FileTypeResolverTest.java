package com.vaadin.tests.server;

import java.io.File;

import junit.framework.TestCase;

import com.vaadin.util.FileTypeResolver;

public class FileTypeResolverTest extends TestCase {

    private static final String FLASH_MIME_TYPE = "application/x-shockwave-flash";
    private static final String TEXT_MIME_TYPE = "text/plain";
    private static final String HTML_MIME_TYPE = "text/html";

    public void testMimeTypes() {
        File plainFlash = new File("MyFlash.swf");
        File plainText = new File("/a/b/MyFlash.txt");
        File plainHtml = new File("c:\\MyFlash.html");

        // Flash
        assertEquals(
                FileTypeResolver.getMIMEType(plainFlash.getAbsolutePath()),
                FLASH_MIME_TYPE);
        assertEquals(
                FileTypeResolver.getMIMEType(plainFlash.getAbsolutePath()
                        + "?param1=value1"), FLASH_MIME_TYPE);
        assertEquals(
                FileTypeResolver.getMIMEType(plainFlash.getAbsolutePath()
                        + "?param1=value1&param2=value2"), FLASH_MIME_TYPE);

        // Plain text
        assertEquals(FileTypeResolver.getMIMEType(plainText.getAbsolutePath()),
                TEXT_MIME_TYPE);
        assertEquals(
                FileTypeResolver.getMIMEType(plainText.getAbsolutePath()
                        + "?param1=value1"), TEXT_MIME_TYPE);
        assertEquals(
                FileTypeResolver.getMIMEType(plainText.getAbsolutePath()
                        + "?param1=value1&param2=value2"), TEXT_MIME_TYPE);

        // Plain text
        assertEquals(FileTypeResolver.getMIMEType(plainHtml.getAbsolutePath()),
                HTML_MIME_TYPE);
        assertEquals(
                FileTypeResolver.getMIMEType(plainHtml.getAbsolutePath()
                        + "?param1=value1"), HTML_MIME_TYPE);
        assertEquals(
                FileTypeResolver.getMIMEType(plainHtml.getAbsolutePath()
                        + "?param1=value1&param2=value2"), HTML_MIME_TYPE);

        // Filename missing
        assertEquals(FileTypeResolver.DEFAULT_MIME_TYPE,
                FileTypeResolver.getMIMEType(""));
        assertEquals(FileTypeResolver.DEFAULT_MIME_TYPE,
                FileTypeResolver.getMIMEType("?param1"));

    }

    public void testExtensionCase() {
        assertEquals("image/jpeg", FileTypeResolver.getMIMEType("abc.jpg"));
        assertEquals("image/jpeg", FileTypeResolver.getMIMEType("abc.jPg"));
        assertEquals("image/jpeg", FileTypeResolver.getMIMEType("abc.JPG"));
        assertEquals("image/jpeg", FileTypeResolver.getMIMEType("abc.JPEG"));
        assertEquals("image/jpeg", FileTypeResolver.getMIMEType("abc.Jpeg"));
        assertEquals("image/jpeg", FileTypeResolver.getMIMEType("abc.JPE"));
    }

    public void testCustomMimeType() {
        assertEquals(FileTypeResolver.DEFAULT_MIME_TYPE,
                FileTypeResolver.getMIMEType("vaadin.foo"));

        FileTypeResolver.addExtension("foo", "Vaadin Foo/Bar");
        FileTypeResolver.addExtension("FOO2", "Vaadin Foo/Bar2");
        assertEquals("Vaadin Foo/Bar",
                FileTypeResolver.getMIMEType("vaadin.foo"));
        assertEquals("Vaadin Foo/Bar2",
                FileTypeResolver.getMIMEType("vaadin.Foo2"));
    }
}
