package com.vaadin.tests.components.label;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;

public class Labels extends ComponentTestCase {

    Label label[] = new Label[20];

    @Override
    protected void setup() {
        super.setup();

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
        l.setContentMode(Label.CONTENT_XHTML);
        addTestComponent(l);

        l = createLabel(
                "<div style='border: 1px solid blue'><h1>Hello</h1><p/><h2>I am a rich Label</h3></div>",
                "This is an XHTML label with fixed 200px width and rich content");
        l.setContentMode(Label.CONTENT_XHTML);
        l.setWidth("200px");
        addTestComponent(l);

    }

    private Component createActionLayout() {
        HorizontalLayout actionLayout = new HorizontalLayout();
        actionLayout.setSpacing(true);
        actionLayout.setMargin(true);
        for (Component c : createActions()) {
            actionLayout.addComponent(c);
        }
        addComponent(actionLayout);
        return actionLayout;
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

    @Override
    protected List<Component> createActions() {
        ArrayList<Component> actions = new ArrayList<Component>();

        CheckBox errorIndicators = new CheckBox("Error indicators",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        Button b = event.getButton();
                        boolean enabled = (Boolean) b.getValue();
                        setErrorIndicators(enabled);

                    }
                });

        CheckBox enabled = new CheckBox("Enabled", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                Button b = event.getButton();
                boolean enabled = (Boolean) b.getValue();
                setEnabled(enabled);
            }
        });

        CheckBox readonly = new CheckBox("Readonly",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        Button b = event.getButton();
                        boolean enabled = (Boolean) b.getValue();
                        setReadOnly(enabled);
                    }
                });

        errorIndicators.setValue(new Boolean(false));
        readonly.setValue(new Boolean(false));
        enabled.setValue(new Boolean(true));

        errorIndicators.setImmediate(true);
        readonly.setImmediate(true);
        enabled.setImmediate(true);

        actions.add(errorIndicators);
        actions.add(readonly);
        actions.add(enabled);

        return actions;
    }

}
