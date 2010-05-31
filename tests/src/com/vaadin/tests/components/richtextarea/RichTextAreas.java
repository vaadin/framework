package com.vaadin.tests.components.richtextarea;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Button.ClickEvent;

public class RichTextAreas extends ComponentTestCase<RichTextArea> {

    @Override
    protected void setup() {
        super.setup();

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
        RichTextArea tf = new RichTextArea();
        tf.setCaption(caption);
        tf.setValue(value);

        return tf;
    }

    private RichTextArea createRichTextArea(String caption) {
        return createRichTextArea(caption, "");
    }

    @Override
    protected String getDescription() {
        return "A generic test for TextFields in different configurations";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
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

        CheckBox required = new CheckBox("Required",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        Button b = event.getButton();
                        boolean enabled = (Boolean) b.getValue();
                        setRequired(enabled);
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
        required.setValue(new Boolean(false));
        readonly.setValue(new Boolean(false));
        enabled.setValue(new Boolean(true));

        errorIndicators.setImmediate(true);
        required.setImmediate(true);
        readonly.setImmediate(true);
        enabled.setImmediate(true);

        actions.add(errorIndicators);
        actions.add(required);
        actions.add(readonly);
        actions.add(enabled);

        return actions;
    }

}
