package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class RepaintWindowContents extends AbstractReindeerTestUI {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("serial")
    @Override
    protected void setup(VaadinRequest request) {
        final Window window = new Window("Test window");
        addWindow(window);

        final Layout layout1 = new VerticalLayout();
        Button button1 = new Button("Button 1");
        layout1.addComponent(button1);

        final Layout layout2 = new VerticalLayout();
        Button button2 = new Button("Button 2");
        layout2.addComponent(button2);

        window.setContent(layout1);

        button1.addClickListener(event -> window.setContent(layout2));

        button2.addClickListener(event -> window.setContent(layout1));
    }

    @Override
    protected String getTestDescription() {
        return "Clicking the button switches the content between content1 and content2";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8832;
    }

}
