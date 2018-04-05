package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ComboboxScrollableWindow extends AbstractReindeerTestUI {

    static final String WINDOW_ID = "window";
    static final String COMBOBOX_ID = "combobox";

    @Override
    protected void setup(VaadinRequest request) {

        Window w = new Window();
        w.setId(WINDOW_ID);
        w.setWidth("300px");
        w.setHeight("300px");
        w.center();

        VerticalLayout content = new VerticalLayout();
        content.setMargin(false);
        content.setSpacing(false);
        w.setContent(content);
        content.setHeight("1000px");
        ComboBox<String> cb = new ComboBox<>();
        cb.setId(COMBOBOX_ID);
        content.addComponent(cb);
        content.setComponentAlignment(cb, Alignment.BOTTOM_CENTER);

        addWindow(w);

    }

    @Override
    protected String getTestDescription() {
        return "The combo box in the bottom of the scrollable window should remain visible when it is clicked.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12736;
    }

}
