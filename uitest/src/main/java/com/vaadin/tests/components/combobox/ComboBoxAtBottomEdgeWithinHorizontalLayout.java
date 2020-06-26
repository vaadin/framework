package com.vaadin.tests.components.combobox;

import java.util.Arrays;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

public class ComboBoxAtBottomEdgeWithinHorizontalLayout extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<Integer> comboBox = new ComboBox<>();
        comboBox.setItems(Arrays.asList(102, 205, 302, 402, 500));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponent(comboBox);

        getLayout().addComponent(horizontalLayout);
        getLayout().setComponentAlignment(horizontalLayout,
                Alignment.BOTTOM_RIGHT);
        getLayout().setSizeFull();
        getLayout().getParent().setSizeFull();
    }

    @Override
    protected Integer getTicketNumber() {
        return 11866;
    }

    @Override
    protected String getTestDescription() {
        return "ComboBox at bottom edge should open popup above "
                + "even when within HorizontalLayout.";
    }
}
