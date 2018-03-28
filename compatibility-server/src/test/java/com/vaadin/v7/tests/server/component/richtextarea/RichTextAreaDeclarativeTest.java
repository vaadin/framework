package com.vaadin.v7.tests.server.component.richtextarea;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.ui.RichTextArea;

public class RichTextAreaDeclarativeTest
        extends DeclarativeTestBase<RichTextArea> {

    private String getBasicDesign() {
        return "<vaadin7-rich-text-area null-representation='' null-setting-allowed>\n"
                + "\n      <b>Header</b> <br/>Some text\n      "
                + "</vaadin7-rich-text-area>";
    }

    private RichTextArea getBasicExpected() {
        RichTextArea rta = new RichTextArea();
        rta.setNullRepresentation("");
        rta.setNullSettingAllowed(true);
        rta.setValue("<b>Header</b> \n<br>Some text");
        return rta;
    }

    @Test
    public void testBasicRead() {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testBasicWrite() {
        testWrite(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testReadEmpty() {
        testRead("<vaadin7-rich-text-area />", new RichTextArea());
    }

    @Test
    public void testWriteEmpty() {
        testWrite("<vaadin7-rich-text-area />", new RichTextArea());
    }

    @Test
    public void testReadOnlyValue() {
        String design = "<vaadin7-rich-text-area readonly style-name='v-richtextarea-readonly'>Hello World!</vaadin7-text-area>";
        RichTextArea ta = new RichTextArea();
        ta.setValue("Hello World!");
        ta.setReadOnly(true);

        testRead(design, ta);
        testWrite(design, ta);
    }
}
