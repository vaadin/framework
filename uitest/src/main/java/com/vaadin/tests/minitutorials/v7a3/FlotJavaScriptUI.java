package com.vaadin.tests.minitutorials.v7a3;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class FlotJavaScriptUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        final Flot flot = new Flot();
        flot.setHeight("300px");
        flot.setWidth("400px");

        flot.addSeries(1, 2, 4, 8, 16);
        layout.addComponent(flot);

        layout.addComponent(
                new Button("Highlight point", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        flot.highlight(0, 3);
                    }
                }));
    }

}
