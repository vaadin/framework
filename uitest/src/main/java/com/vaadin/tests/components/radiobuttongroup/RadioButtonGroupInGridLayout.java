package com.vaadin.tests.components.radiobuttongroup;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public class RadioButtonGroupInGridLayout extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        RadioButtonGroup<String> cbGroup = new RadioButtonGroup<>(null, DataProvider.ofItems("A", "B", "C"));
        cbGroup.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);

        GridLayout gridLayout = new GridLayout(2, 1);
        gridLayout.addComponent(cbGroup, 0, 0);
        gridLayout.setSpacing(true);

        gridLayout.addComponent(new TextField(), 1, 0);

        addComponent(gridLayout);
    }
}
