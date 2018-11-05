package com.vaadin.tests.components.radiobuttongroup;

import com.vaadin.tests.components.HasValueRequiredIndicator;
import com.vaadin.ui.RadioButtonGroup;

/**
 * The whole logic is inside HasValueRequiredIndicator. The code here just set
 * value for the component.
 *
 * @author Vaadin Ltd
 *
 */
public class RadioButtonGroupRequiredIndicator
        extends HasValueRequiredIndicator<RadioButtonGroup<String>> {

    @Override
    protected void initValue(RadioButtonGroup<String> component) {
        component.setItems("a", "b", "c");
    }

}
