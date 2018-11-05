package com.vaadin.tests.components.optiongroup;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.ui.OptionGroup;

/**
 * Testcase for #10451
 *
 * @author Vaadin Ltd
 */
public class OptionGroupRetainFocusKeyboardValueChange
        extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final OptionGroup optiongroup = new OptionGroup();
        optiongroup.addItem(1);
        optiongroup.addItem(2);
        optiongroup.addItem(3);
        optiongroup.setItemCaption(1, "A");
        optiongroup.setItemCaption(2, "B");
        optiongroup.setItemCaption(3, "C");
        optiongroup.setImmediate(true);

        optiongroup.addValueChangeListener(event -> {
            if (optiongroup.isSelected(2)) {
                optiongroup.setItemCaption(1, "A+");
            } else if (optiongroup.isSelected(3)) {
                optiongroup.removeItem(2);
                optiongroup.addItem(2);
                optiongroup.setItemCaption(2, "B");
            }
        });

        addComponent(optiongroup);

        optiongroup.focus();
    }

    @Override
    protected String getTestDescription() {
        return "OptionGroup should retain focus after it's value being changed with keyboard";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10451;
    }
}
