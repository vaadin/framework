package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class CssLayoutRemoveComponent extends TestBase {

    @Override
    protected void setup() {
        final CssLayout layout = new CssLayout();
        final TextField tf = new TextField("Caption1");
        Button b = new Button("Remove field ", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                layout.removeComponent(tf);
            }

        });
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
        return 5778;
    }

}
