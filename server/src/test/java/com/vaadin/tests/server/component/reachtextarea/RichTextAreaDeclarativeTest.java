package com.vaadin.tests.server.component.reachtextarea;

import java.util.Locale;

import org.junit.Test;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.tests.server.component.abstractfield.AbstractFieldDeclarativeTest;
import com.vaadin.ui.RichTextArea;

public class RichTextAreaDeclarativeTest
        extends AbstractFieldDeclarativeTest<RichTextArea, String> {

    @Override
    public void valueDeserialization()
            throws InstantiationException, IllegalAccessException {
        String value = "<b>Header</b> \n<br>Some text";
        String design = String.format("<%s>\n      %s\n      </%s>",
                getComponentTag(), value, getComponentTag());

        RichTextArea component = new RichTextArea();
        component.setValue(value);

        testRead(design, component);
        testWrite(design, component);
    }

    @Override
    public void readOnlyValue()
            throws InstantiationException, IllegalAccessException {
        String value = "<b>Header</b> \n<br>Some text";
        String design = String.format("<%s readonly>\n      %s\n      </%s>",
                getComponentTag(), value, getComponentTag());

        RichTextArea component = new RichTextArea();
        component.setValue(value);
        component.setReadOnly(true);

        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void remainingAttributeDeserialization() {
        ValueChangeMode mode = ValueChangeMode.TIMEOUT;
        int timeout = 67;
        String design = String.format(
                "<%s value-change-mode='%s' value-change-timeout='%d'/>",
                getComponentTag(), mode.name().toLowerCase(Locale.ROOT),
                timeout);

        RichTextArea component = new RichTextArea();
        component.setValueChangeMode(mode);
        component.setValueChangeTimeout(timeout);

        testRead(design, component);
        testWrite(design, component);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-rich-text-area";
    }

    @Override
    protected Class<RichTextArea> getComponentClass() {
        return RichTextArea.class;
    }

}
