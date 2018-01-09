package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.v7.ui.TextField;

public class CssLayoutRemoveComponentWithCaption extends TestBase {

    @Override
    protected void setup() {
        final CssLayout layout = new CssLayout();
        final TextField tf = new TextField("Caption");
        Button b = new Button("Remove field and add new", event -> {
            layout.removeComponent(tf);
            addComponent(new TextField("new field"));
        });
        layout.addComponent(tf);
        layout.addComponent(b);

        addComponent(layout);
    }

    @Override
    protected String getDescription() {
        return "Clicking on the button should remove the text field and add a new 'new field' text field";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4204;
    }

}
