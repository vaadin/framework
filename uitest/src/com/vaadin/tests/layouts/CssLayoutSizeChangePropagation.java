package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class CssLayoutSizeChangePropagation extends TestBase {

    @Override
    protected void setup() {
        getLayout().setSizeFull();
        final VerticalLayout sp = new VerticalLayout();

        sp.setHeight("100%");

        final CssLayout cssLayout = new CssLayout() {
            @Override
            protected String getCss(Component c) {
                return "background-color: yellow;";
            }
        };
        cssLayout.setSizeFull();
        Label l = new Label("bö");
        l.setSizeFull();
        cssLayout.addComponent(l);

        sp.addComponent(cssLayout);

        Button button = new Button("b");
        button.addListener(new ClickListener() {
            boolean bool = true;

            @Override
            public void buttonClick(ClickEvent event) {
                sp.setExpandRatio(cssLayout, bool ? 1 : 0);
                bool = !bool;
            }
        });

        sp.addComponent(button);
        sp.setExpandRatio(button, 1);

        getLayout().addComponent(sp);

    }

    @Override
    protected String getDescription() {
        return "Upper part of view should become yellow on button click.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4351;
    }

}
