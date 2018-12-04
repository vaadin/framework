package com.vaadin.v7.tests.server.component.textarea;

import java.io.IOException;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.v7.ui.TextArea;

/**
 * Tests declarative support for implementations of {@link TextArea}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class TextAreaDeclarativeTest extends DeclarativeTestBase<TextArea> {

    @Test
    public void testTextArea() {
        String design = "<vaadin7-text-area rows=6 wordwrap=false>Hello World!</vaadin7-text-area>";
        TextArea ta = new TextArea();
        ta.setRows(6);
        ta.setWordwrap(false);
        ta.setValue("Hello World!");
        testRead(design, ta);
        testWrite(design, ta);
    }

    @Test
    public void testHtmlEntities() throws IOException {
        String design = "<vaadin7-text-area>&amp; Test</vaadin7-text-area>";
        TextArea read = read(design);
        assertEquals("& Test", read.getValue());

        read.setValue("&amp; Test");

        DesignContext dc = new DesignContext();
        Element root = new Element(Tag.valueOf("vaadin-text-area"), "");
        read.writeDesign(root, dc);

        assertEquals("&amp;amp; Test", root.html());
    }

    @Test
    public void testReadOnlyValue() {
        String design = "<vaadin7-text-area readonly rows=6 wordwrap=false>Hello World!</vaadin7-text-area>";
        TextArea ta = new TextArea();
        ta.setRows(6);
        ta.setWordwrap(false);
        ta.setValue("Hello World!");
        ta.setReadOnly(true);
        testRead(design, ta);
        testWrite(design, ta);
    }
}
