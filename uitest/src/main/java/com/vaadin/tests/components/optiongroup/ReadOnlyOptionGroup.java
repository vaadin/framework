package com.vaadin.tests.components.optiongroup;

import java.util.Collections;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.v7.ui.OptionGroup;

/**
 * Test UI for unset read-only flag of Option group with new items allowed.
 *
 * @author Vaadin Ltd
 */
public class ReadOnlyOptionGroup extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final OptionGroup optionGroup = new OptionGroup("test field",
                Collections.singletonList("Option"));
        optionGroup.setNewItemsAllowed(true);

        final CheckBox readOnlyCheckbox = new CheckBox("read-only");
        readOnlyCheckbox.addValueChangeListener(
                event -> optionGroup.setReadOnly(readOnlyCheckbox.getValue()));
        readOnlyCheckbox.setValue(Boolean.TRUE);

        addComponent(optionGroup);
        addComponent(readOnlyCheckbox);
    }

    @Override
    protected String getTestDescription() {
        return "Unset read-only state for Option group should not throw an exception";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11772;
    }

}
