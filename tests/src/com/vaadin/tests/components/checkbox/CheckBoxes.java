package com.vaadin.tests.components.checkbox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Button.ClickEvent;

public class CheckBoxes extends ComponentTestCase<CheckBox> {

    private ThemeResource SMALL_ICON = new ThemeResource(
            "../runo/icons/16/ok.png");
    private ThemeResource LARGE_ICON = new ThemeResource(
            "../runo/icons/64/document.png");

    @Override
    protected void setup() {
        super.setup();

        setTheme("tests-tickets");
        CheckBox cb;

        cb = createCheckBox("CheckBox with normal text");
        addTestComponent(cb);

        cb = createCheckBox("CheckBox with large text");
        cb.setStyleName("large");
        addTestComponent(cb);

        cb = createCheckBox("CheckBox with normal text and small icon",
                SMALL_ICON);
        addTestComponent(cb);
        cb = createCheckBox("CheckBox with large text and small icon",
                SMALL_ICON);
        cb.setStyleName("large");
        addTestComponent(cb);

        cb = createCheckBox("CheckBox with normal text and large icon",
                LARGE_ICON);
        addTestComponent(cb);
        cb = createCheckBox("CheckBox with large text and large icon",
                LARGE_ICON);
        cb.setStyleName("large");
        addTestComponent(cb);

    }

    private CheckBox createCheckBox(String caption, Resource icon) {
        CheckBox cb = createCheckBox(caption);
        cb.setIcon(icon);

        return cb;
    }

    private CheckBox createCheckBox(String caption) {
        return new CheckBox(caption);
    }

    @Override
    protected String getDescription() {
        return "A generic test for CheckBoxes in different configurations";
    }

    @Override
    protected Integer getTicketNumber() {
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

        errorIndicators.setValue(Boolean.FALSE);
        required.setValue(Boolean.FALSE);
        readonly.setValue(Boolean.FALSE);
        enabled.setValue(Boolean.TRUE);

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
