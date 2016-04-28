package com.vaadin.tests.components.optiongroup;

import java.util.Arrays;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Component;
import com.vaadin.ui.OptionGroup;

public class DisabledOptionGroupItems extends ComponentTestCase<OptionGroup> {

    private static final List<String> cities = Arrays.asList(new String[] {
            "Berlin", "Brussels", "Helsinki", "Madrid", "Oslo", "Paris",
            "Stockholm" });

    private static final String NULL_SELECTION_ID = "Berlin";

    @Override
    protected Class<OptionGroup> getTestClass() {
        return OptionGroup.class;
    }

    @Override
    protected void initializeComponents() {

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

    @Override
    protected void createCustomActions(List<Component> actions) {
        actions.add(createInvertDisabledItemsAction());
        actions.add(createToggleSelectionModeAction());

    }

    private Component createToggleSelectionModeAction() {
        return createButtonAction("Toggle selection mode",
                new Command<OptionGroup, Boolean>() {

                    @Override
                    public void execute(OptionGroup og, Boolean value,
                            Object data) {
                        if (og.isMultiSelect()) {
                            og.setMultiSelect(false);
                            og.setNullSelectionItemId(NULL_SELECTION_ID);
                        } else {
                            og.setNullSelectionItemId(null);
                            og.setMultiSelect(true);
                        }
                    }
                });
    }

    private Component createInvertDisabledItemsAction() {
        return createButtonAction("Invert disabled items",
                new Command<OptionGroup, Boolean>() {

                    @Override
                    public void execute(OptionGroup c, Boolean value,
                            Object data) {
                        for (Object itemId : c.getItemIds()) {
                            c.setItemEnabled(itemId, !c.isItemEnabled(itemId));
                        }
                    }
                });
    }

    private OptionGroup createOptionGroup(String caption) {
        OptionGroup og = new OptionGroup(caption, cities);
        og.setImmediate(true);
        return og;
    }

    @Override
    protected String getDescription() {
        return "Test case for disabled items in an OptionGroup";
    }
}
