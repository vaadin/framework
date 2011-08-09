package com.vaadin.tests.components.optiongroup;

import com.vaadin.tests.components.select.AbstractSelectTestCase;
import com.vaadin.ui.OptionGroup;

public class OptionGroups extends AbstractSelectTestCase<OptionGroup> {

    @Override
    protected Class<OptionGroup> getTestClass() {
        return OptionGroup.class;
    }

    @Override
    protected void createActions() {
        super.createActions();

        createDisabledItemsMultiToggle("Disabled items");
        createBooleanAction("HTML content allowed", CATEGORY_STATE, false,
                new Command<OptionGroup, Boolean>() {
                    public void execute(OptionGroup og, Boolean value,
                            Object data) {
                        og.setHtmlContentAllowed(value.booleanValue());
                    }
                });
    }

    private void createDisabledItemsMultiToggle(String category) {
        for (Object id : getComponent().getItemIds()) {
            createBooleanAction(id.toString() + " - enabled", category, true,
                    enabledItemCommand, id);
        }
    }

    private Command<OptionGroup, Boolean> enabledItemCommand = new Command<OptionGroup, Boolean>() {

        public void execute(OptionGroup c, Boolean value, Object data) {
            c.setItemEnabled(data, value);

        }
    };

}
