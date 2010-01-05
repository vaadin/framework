package com.vaadin.tests.components.button;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Button.ClickEvent;

public class Buttons extends ComponentTestCase {

    Button button[] = new Button[20];

    @Override
    protected void setup() {
        super.setup();

        Button l;
        for (boolean nat : new boolean[] { false, true }) {
            l = createButton("This is an undefined wide button", nat);
            l.setWidth(null);
            addTestComponent(l);

            l = createButton(
                    "This is an undefined wide button with fixed 100px height",
                    nat);
            l.setWidth(null);
            l.setHeight("100px");
            addTestComponent(l);

            l = createButton(
                    "This is a 200px wide simple button with a much longer caption",
                    nat);
            l.setWidth("200px");
            addTestComponent(l);

            l = createButton("This is a 100% wide simple button "
                    + LoremIpsum.get(1500), nat);
            l.setWidth("100%");
            addTestComponent(l);

            l = createButton(
                    "This is a 100% wide button with fixed 65px height. "
                            + LoremIpsum.get(5000), nat);
            l.setWidth("100%");
            l.setHeight("65px");
            addTestComponent(l);
        }

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

    private Button createButton(String text, boolean nativeButton) {
        Button b;
        if (nativeButton) {
            b = new NativeButton(text);
        } else {
            b = new Button(text);
        }

        return b;
    }

    @Override
    protected String getDescription() {
        return "A generic test for Buttons in different configurations";
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
