package com.vaadin.tests.components.select;

import java.util.Arrays;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.ListSelect;

public class SelectWithIntegers extends AbstractReindeerTestUI {
    private final List<Integer> years = Arrays.asList(2014, 2015, 2016);

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(createSelect("Default", null));
        addComponent(createSelect("ID_TOSTRING", ItemCaptionMode.ID_TOSTRING));
    }

    private AbstractSelect createSelect(String caption, ItemCaptionMode mode) {
        ListSelect listSelect = new ListSelect(caption, years);
        listSelect.setRows(years.size());
        listSelect.setNullSelectionAllowed(false);
        if (mode != null) {
            listSelect.setItemCaptionMode(mode);
        }
        return listSelect;
    }

}
