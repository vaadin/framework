package com.vaadin.tests.server.component.textarea;

import java.io.IOException;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Tests declarative support for implementations of {@link TextArea}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class TextAreaDeclarativeTest extends DeclarativeTestBase<TextArea> {

    @Test
    public void testTextArea() {
        String design = "<vaadin-text-area rows=6 wordwrap=false>Hello World!</vaadin-text-area>";
        TextArea ta = new TextArea();
        ta.setRows(6);
        ta.setWordwrap(false);
        ta.setValue("Hello World!");
        testRead(design, ta);
        testWrite(design, ta);
    }

    @Test
    public void testHtmlEntities() throws IOException {
        String design = "<vaadin-text-area>&amp; Test</vaadin-text-area>";
        TextArea read = read(design);
        Assert.assertEquals("& Test", read.getValue());

        read.setValue("&amp; Test");

        DesignContext dc = new DesignContext();
        Element root = new Element(Tag.valueOf("vaadin-text-area"), "");
        read.writeDesign(root, dc);

        Assert.assertEquals("&amp;amp; Test", root.html());
    }

    @Test
    public void testReadOnlyValue() {
        String design = "<vaadin-text-area readonly rows=6 wordwrap=false>Hello World!</vaadin-text-area>";
        TextArea ta = new TextArea();
        ta.setRows(6);
        ta.setWordwrap(false);
        ta.setValue("Hello World!");
        ta.setReadOnly(true);
        testRead(design, ta);
        testWrite(design, ta);
    }
}
