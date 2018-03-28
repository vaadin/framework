package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;

public class ExpandChangeReattach extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setHeight(null);

        Table table = new Table("Table", TestUtils.getISO3166Container());
        verticalLayout.addComponent(table);
        verticalLayout.addComponent(
                new Button("Toggle expand logic", event -> {
                    if (verticalLayout.getHeight() == -1) {
                        verticalLayout.setHeight("900px");
                    } else {
                        verticalLayout.setHeight(null);
                    }
                }));

        addComponent(verticalLayout);
    }

    @Override
    protected String getTestDescription() {
        return "Table should not forget its scroll position when it is temporarily detached from the DOM because an ordered layout changes expand modes.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(10489);
    }

}
