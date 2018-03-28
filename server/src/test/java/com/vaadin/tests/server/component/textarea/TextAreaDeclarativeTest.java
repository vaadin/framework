package com.vaadin.tests.server.component.textarea;

import java.io.IOException;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Test;

import com.vaadin.tests.server.component.abstracttextfield.AbstractTextFieldDeclarativeTest;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Tests declarative support for implementations of {@link TextArea}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class TextAreaDeclarativeTest
        extends AbstractTextFieldDeclarativeTest<TextArea> {

    @Override
    public void valueDeserialization()
            throws InstantiationException, IllegalAccessException {
        String value = "Hello World!";
        String design = String.format("<%s>%s</%s>", getComponentTag(), value,
                getComponentTag());

        TextArea component = new TextArea();
        component.setValue(value);

        testRead(design, component);
        testWrite(design, component);
    }

    @Override
    public void readOnlyValue()
            throws InstantiationException, IllegalAccessException {
        String value = "Hello World!";
        String design = String.format("<%s readonly>%s</%s>", getComponentTag(),
                value, getComponentTag());

        TextArea component = new TextArea();
        component.setValue(value);
        component.setReadOnly(true);

        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testHtmlEntities() throws IOException {
        String design = "<vaadin-text-area>&amp; Test</vaadin-text-area>";
        TextArea read = read(design);
        assertEquals("& Test", read.getValue());

        read.setValue("&amp; Test");

        DesignContext dc = new DesignContext();
        Element root = new Element(Tag.valueOf("vaadin-text-area"), "");
        read.writeDesign(root, dc);

        assertEquals("&amp;amp; Test", root.html());
    }

    @Test
    public void remainingAttriburesDeserialization() {
        int rows = 11;
        boolean wrap = false;
        String design = String.format("<%s rows='%s' word-wrap='%s'/>",
                getComponentTag(), rows, wrap);

        TextArea component = new TextArea();
        component.setRows(rows);
        component.setWordWrap(wrap);

        testRead(design, component);
        testWrite(design, component);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-text-area";
    }

    @Override
    protected Class<TextArea> getComponentClass() {
        return TextArea.class;
    }

}
