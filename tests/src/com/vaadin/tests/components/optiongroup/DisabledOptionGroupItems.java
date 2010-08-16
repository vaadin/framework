package com.vaadin.tests.components.optiongroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.OptionGroup;

public class DisabledOptionGroupItems extends ComponentTestCase<OptionGroup> {

    private static final List<String> cities = Arrays.asList(new String[] {
            "Berlin", "Brussels", "Helsinki", "Madrid", "Oslo", "Paris",
            "Stockholm" });

    private static final String NULL_SELECTION_ID = "Berlin";

    @Override
    protected void setup() {
        super.setup();

        OptionGroup og = createOptionGroup("");
        og.setItemEnabled("Helsinki", false);
        og.setItemEnabled("Paris", false);
        og.setValue(Arrays.asList("Helsinki"));
        og.setNullSelectionAllowed(true);
        og.setNullSelectionItemId(NULL_SELECTION_ID);
        addTestComponent(og);

        og = createOptionGroup("");
        og.setMultiSelect(true);
        og.setValue(Arrays.asList("Helsinki"));
        og.setNullSelectionAllowed(true);
        og.setItemEnabled("Helsinki", false);
        og.setItemEnabled("Paris", false);
        addTestComponent(og);

    }

    private OptionGroup createOptionGroup(String caption) {
        OptionGroup og = new OptionGroup(caption, cities);
        og.setImmediate(true);
        return og;
    }

    @Override
    protected List<Component> createActions() {
        ArrayList<Component> actions = new ArrayList<Component>();

        CheckBox enabled = new CheckBox("Enabled", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                setEnabled(event.getButton().booleanValue());
            }
        });
        enabled.setValue(true);
        enabled.setImmediate(true);
        actions.add(enabled);

        CheckBox readonly = new CheckBox("Readonly",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        setReadOnly(event.getButton().booleanValue());
                    }
                });
        readonly.setValue(false);
        readonly.setImmediate(true);
        actions.add(readonly);

        Button toggleDisabledItems = new Button("Toggle disabled items",
                new ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        for (OptionGroup og : getTestComponents()) {
                            for (Object itemId : og.getItemIds()) {
                                og.setItemEnabled(itemId,
                                        !og.isItemEnabled(itemId));
                            }
                        }
                    }
                });
        actions.add(toggleDisabledItems);

        Button toggleSelectionMode = new Button("Toggle selection mode",
                new ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        for (OptionGroup og : getTestComponents()) {
                            if (og.isMultiSelect()) {
                                og.setMultiSelect(false);
                                og.setNullSelectionItemId(NULL_SELECTION_ID);
                            } else {
                                og.setNullSelectionItemId(null);
                                og.setMultiSelect(true);
                            }

                        }
                    }
                });
        actions.add(toggleSelectionMode);

        return actions;
    }
}
