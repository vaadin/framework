package com.vaadin.tests.components.label;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.Label;

public class Labels extends ComponentTestCase<Label> {

    @Override
    protected Class<Label> getTestClass() {
        return Label.class;
    }

    @Override
    protected void initializeComponents() {
        Label l;
        l = createLabel("This is an undefined\nwide\nlabel               which do not wrap. It should be clipped at the end of the screen"
                + LoremIpsum.get(1000));
        l.setWidth(null);
        addTestComponent(l);

        l = createLabel("This is a 200px wide simple label which\n\n\nwrap");
        l.setWidth("200px");
        addTestComponent(l);

        l = createLabel("This is a 100% wide simple label which should wrap. "
                + LoremIpsum.get(1500));
        l.setWidth("100%");
        addTestComponent(l);

        l = createLabel("This is a\n\n     100%\t\t\t   \twide simple with fixed 65px height. It should wrap. "
                + LoremIpsum.get(5000));
        l.setWidth("100%");
        l.setHeight("65px");
        addTestComponent(l);

        l = createLabel(
                "<div style='border: 1px solid red'><h1>Hello\n\n\n</h1><p/><h2>I am a rich Label</h3></div>",
                "This is an XHTML label with rich content");
        l.setContentMode(ContentMode.HTML);
        addTestComponent(l);

        l = createLabel(
                "<div style='border: 1px solid blue'><h1>Hello</h1><p/><h2>I am a rich Label</h3></div>",
                "This is an XHTML label with fixed 200px width and rich content");
        l.setContentMode(ContentMode.HTML);
        l.setWidth("200px");
        addTestComponent(l);

        l = createLabel("Some UTF8 characters: äöÄÖ≤≠∉Ġå2²");
        addTestComponent(l);

    }

    private Label createLabel(String text, String caption) {
        Label l = new Label(text);
        l.setCaption(caption);

        return l;
    }

    private Label createLabel(String text) {
        return createLabel(text, null);
    }

    @Override
    protected String getDescription() {
        return "A generic test for Labels in different configurations";
    }

}
