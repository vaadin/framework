package com.vaadin.tests.components.optiongroup;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.v7.ui.OptionGroup;

public class OptionGroupDisabled extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.setEnabled(false);
        optionGroup.setImmediate(true);
        optionGroup.setMultiSelect(true);
        optionGroup.addItem("test 1");
        optionGroup.addItem("test 2");
        optionGroup.addItem("test 3");

        addComponent(optionGroup);
    }

}
