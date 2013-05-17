package com.vaadin.tests.components.page;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class PageReload extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        Button b = new Button("Press to reload");
        b.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                getPage().reload();
            }
        });
        addComponent(b);
        log("UI id: " + getUIId());
    }

    @Override
    protected String getTestDescription() {
        return "Tests Page.reload(). Click button to refresh the page.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10250;
    }

}
