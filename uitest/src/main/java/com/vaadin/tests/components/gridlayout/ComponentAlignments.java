package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.VerticalLayout;

/**
 * Test UI for TOP_CENTER and TOP_RIGHT alignments in VerticalLayout.
 *
 * @author Vaadin Ltd
 */
public class ComponentAlignments extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        CheckBox topcenter = new CheckBox("Top Center");
        topcenter.setSizeUndefined();
        VerticalLayout verticalLayout1 = new VerticalLayout(topcenter);
        verticalLayout1.setHeight("40px");
        verticalLayout1.setWidth("140px");
        verticalLayout1.setComponentAlignment(topcenter, Alignment.TOP_CENTER);
        addComponent(verticalLayout1);

        CheckBox topright = new CheckBox("Top Right");
        topright.setSizeUndefined();
        VerticalLayout verticalLayout2 = new VerticalLayout(topright);
        verticalLayout2.setHeight("40px");
        verticalLayout2.setWidth("140px");
        verticalLayout2.setComponentAlignment(topright, Alignment.TOP_RIGHT);
        addComponent(verticalLayout2);

    }

    @Override
    protected Integer getTicketNumber() {
        return 14137;
    }

    @Override
    protected String getTestDescription() {
        return "TOP_CENTER and TOP_RIGHT alignments should work in VerticalLayout";
    }
}
