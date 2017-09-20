package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class VerticalLayoutRemoveComponent extends TestBase {

    @Override
    protected void setup() {
        final VerticalLayout layout = new VerticalLayout();
        layout.setId("targetLayout");
        // spacing makes it harder to track only the relevant events
        layout.setSpacing(false);
        final TextField tf = new TextField("Caption1");
        Button b = new Button("Remove field ",
                event -> layout.removeComponent(tf));
        layout.addComponent(tf);
        layout.addComponent(b);
        layout.addComponent(new TextField("Caption2"));
        layout.addComponent(new TextField("Caption3"));

        addComponent(layout);
    }

    @Override
    protected String getDescription() {
        return "Clicking on the button should remove one text field but other textfields and their captions should stay intact.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7113;
    }

}
