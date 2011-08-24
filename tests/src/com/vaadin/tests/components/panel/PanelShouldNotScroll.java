package com.vaadin.tests.components.panel;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class PanelShouldNotScroll extends TestBase {

    private Button addMore;

    @Override
    protected void setup() {
        final Panel p = new Panel(new CssLayout());
        p.setScrollable(true);
        p.setSizeFull();
        p.setHeight("600px");
        p.addComponent(foo());
        addMore = new Button("Add");
        addMore.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                p.removeComponent(addMore);
                p.addComponent(foo());
                p.addComponent(addMore);
            }
        });
        p.addComponent(addMore);
        addComponent(p);
        ((VerticalLayout) getMainWindow().getContent()).setSizeFull();
    }

    private Component foo() {
        Panel panel = new Panel();
        panel.addComponent(new Label(
                "fooooooooo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>"
                        + "foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>",
                Label.CONTENT_XHTML));
        return panel;
    }

    @Override
    protected String getDescription() {
        return "adding a panel to the bottom of the scrolling panel should not scroll up to the top";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7462;
    }

}
