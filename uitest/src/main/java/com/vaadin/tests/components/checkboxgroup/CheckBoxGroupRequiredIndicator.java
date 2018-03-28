package com.vaadin.tests.components.checkboxgroup;

import com.vaadin.tests.components.HasValueRequiredIndicator;
import com.vaadin.ui.CheckBoxGroup;

/**
 * The whole logic is inside HasValueRequiredIndicator. The code here just set
 * value for the component.
 *
 * @author Vaadin Ltd
 *
 */
public class CheckBoxGroupRequiredIndicator
        extends HasValueRequiredIndicator<CheckBoxGroup<String>> {

    @Override
    protected void initValue(CheckBoxGroup<String> component) {
        component.setCaption("a");
        component.setItems("a", "b", "c");
    }

}
