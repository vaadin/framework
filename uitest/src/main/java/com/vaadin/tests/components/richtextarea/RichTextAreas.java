package com.vaadin.tests.components.richtextarea;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.RichTextArea;

public class RichTextAreas extends ComponentTestCase<RichTextArea> {

    @Override
    protected Class<RichTextArea> getTestClass() {
        return RichTextArea.class;
    }

    @Override
    protected void initializeComponents() {
        RichTextArea rta;

        rta = createRichTextArea("TextField 100% wide, 100px high");
        rta.setWidth("100%");
        rta.setHeight("100px");
        addTestComponent(rta);

        rta = createRichTextArea("TextField auto width, auto height");
        addTestComponent(rta);

        rta = createRichTextArea(null, "500px wide, 120px high textfield");
        rta.setWidth("500px");
        rta.setHeight("120px");
        addTestComponent(rta);

    }

    private RichTextArea createRichTextArea(String caption, String value) {
        return new RichTextArea(caption, value);
    }

    private RichTextArea createRichTextArea(String caption) {
        return new RichTextArea(caption);
    }

}
