package com.vaadin.tests.components.twincolselect;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.TwinColSelect;

public class TwinColSelectCtrlA extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        List<String> data = IntStream.range(0, 6).mapToObj(i -> "Option " + i)
                .collect(Collectors.toList());

        TwinColSelect twinColSelect = new TwinColSelect<>(null, data);

        twinColSelect.setRows(6);
        twinColSelect.setLeftColumnCaption("Available options");
        twinColSelect.setRightColumnCaption("Selected options");

        addComponent(twinColSelect);
    }

}
