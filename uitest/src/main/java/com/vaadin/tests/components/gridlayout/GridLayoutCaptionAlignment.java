package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.v7.ui.TextField;

@SuppressWarnings("deprecation")
public class GridLayoutCaptionAlignment extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(createLayout(Alignment.BOTTOM_CENTER));
        addComponent(createLayout(Alignment.BOTTOM_LEFT));
        addComponent(createLayout(Alignment.BOTTOM_RIGHT));
        addComponent(createLayout(Alignment.MIDDLE_CENTER));
        addComponent(createLayout(Alignment.MIDDLE_LEFT));
        addComponent(createLayout(Alignment.MIDDLE_RIGHT));
        addComponent(createLayout(Alignment.TOP_CENTER));
        addComponent(createLayout(Alignment.TOP_LEFT));
        addComponent(createLayout(Alignment.TOP_RIGHT));
    }

    private GridLayout createLayout(Alignment align) {
        TextField field = new TextField("Some caption");

        GridLayout layout = new GridLayout(3, 3);
        layout.setSizeFull();
        layout.addComponent(field);
        layout.setComponentAlignment(field, align);
        return layout;
    }

    @Override
    protected Integer getTicketNumber() {
        return 17619;
    }

    @Override
    protected String getTestDescription() {
        return "Test alignment of component captions inside GridLayout – "
                + "all captions should be aligned directly above the TextField components.";
    }

}
