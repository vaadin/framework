package com.vaadin.tests.components.label;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;

public class LabelModes extends ComponentTestCase<Label> {

    Label label[] = new Label[20];

    @Override
    protected void setup() {
        super.setup();

        Label l;
        l = createLabel("This is an undefined wide label with default content mode");
        l.setWidth(null);
        addTestComponent(l);

        l = createLabel("This label                       contains\nnewlines and spaces\nbut is in\ndefault content mode");
        l.setWidth(null);
        addTestComponent(l);

        l = createLabel("This label                       contains\nnewlines and spaces\nand is in\npreformatted mode");
        l.setContentMode(Label.CONTENT_PREFORMATTED);
        l.setWidth(null);
        addTestComponent(l);

        l = createLabel("This label                       contains\nnewlines and spaces\nand is in\nhtml mode");
        l.setContentMode(Label.CONTENT_XHTML);
        l.setWidth(null);
        addTestComponent(l);

        l = createLabel("This label                       contains\nnewlines and spaces\nand is in\nraw mode");
        l.setContentMode(Label.CONTENT_RAW);
        l.setWidth(null);
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

        errorIndicators.setValue(Boolean.FALSE);
        readonly.setValue(Boolean.FALSE);
        enabled.setValue(Boolean.TRUE);

        errorIndicators.setImmediate(true);
        readonly.setImmediate(true);
        enabled.setImmediate(true);

        actions.add(errorIndicators);
        actions.add(readonly);
        actions.add(enabled);

        return actions;
    }

}
