package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Select;
import com.vaadin.ui.Button.ClickEvent;

public class Comboboxes extends ComponentTestCase<ComboBox> {

    private static final Object CAPTION = "caption";
    Select label[] = new Select[20];

    @Override
    protected void setup() {
        super.setup();

        ComboBox s;

        s = createSelect(null);
        s.setWidth(null);
        addTestComponent(s);

        s = createSelect("Undefined wide, empty select");
        s.setWidth(null);
        addTestComponent(s);

        s = createSelect("Undefined wide select with 5 items");
        s.setWidth(null);
        addItem(s, "The first item");
        addItem(s, "The second item");
        addItem(s, "The third item");
        addItem(s, "The fourth item");
        addItem(s, "The fifth item");
        addTestComponent(s);

        s = createSelect("Undefined wide select with 50 items");
        s.setWidth(null);
        populate(s, 50);
        addTestComponent(s);

        s = createSelect(null);
        s.setWidth("100px");
        addTestComponent(s);

        s = createSelect("100px wide, empty select");
        s.setWidth("100px");
        addTestComponent(s);

        s = createSelect("150px wide select with 5 items");
        s.setWidth("150px");
        addItem(s, "The first item");
        addItem(s, "The second item");
        addItem(s, "The third item");
        addItem(s, "The fourth item");
        addItem(s, "The fifth item");
        addTestComponent(s);

        s = createSelect("200px wide select with 50 items");
        s.setWidth("200px");
        populate(s, 50);
        addTestComponent(s);

    }

    private void populate(Select s, int nr) {
        String text = " an item ";

        String caption = "";
        for (int i = 0; i < nr; i++) {
            if (i % 2 == 0) {
                caption += text;
            } else {
                caption += i;
            }

            addItem(s, caption);
        }

    }

    private void addItem(Select s, String string) {
        Object id = s.addItem();
        s.getItem(id).getItemProperty(CAPTION).setValue(string);

    }

    private ComboBox createSelect(String caption) {
        ComboBox cb = new ComboBox();
        cb.setImmediate(true);
        cb.addContainerProperty(CAPTION, String.class, "");
        cb.setItemCaptionPropertyId(CAPTION);
        cb.setCaption(caption);

        return cb;
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
