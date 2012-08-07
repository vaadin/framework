package com.vaadin.tests.components.orderedlayout;

import java.util.Arrays;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Select;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LayoutClickListenerTest extends TestBase {

    @Override
    protected void setup() {

        // Create a grid layout with click events
        final GridLayout layout = new GridLayout(3, 2);
        layout.addStyleName("border");
        layout.setSpacing(true);
        layout.setSizeFull();

        // Add some components to the layout
        layout.addComponent(new TextField(null, "Click here"));
        layout.addComponent(new Link("Click here", null));

        Select select = new Select(null, Arrays.asList("Click here"));
        select.select("Click here");
        layout.addComponent(select);

        // Tab content
        VerticalLayout l1 = new VerticalLayout();
        l1.setMargin(true);
        l1.addComponent(new Label("This is a label."));
        l1.addComponent(new TextField(null, "Click here"));
        l1.addComponent(new Link("Click here", null));

        TabSheet t = new TabSheet();
        t.setHeight("200px");
        t.addTab(l1, "Tab", null);
        layout.addComponent(t);

        VerticalLayout nestedLayout = new VerticalLayout();
        nestedLayout.addComponent(new Label("This is a label."));
        nestedLayout.addComponent(new TextField(null, "Click here"));
        nestedLayout.addComponent(new Link("Click here", null));

        HorizontalLayout nestedLayout2 = new HorizontalLayout();
        nestedLayout2.addComponent(new Label("Deeply nested label"));
        nestedLayout.addComponent(nestedLayout2);

        layout.addComponent(nestedLayout);

        // Listen for layout click events
        layout.addListener(new LayoutClickListener() {
            @Override
            public void layoutClick(LayoutClickEvent event) {

                // Get the deepest nested component which was clicked
                Component clickedComponent = event.getClickedComponent();

                if (clickedComponent == null) {
                    // Not over any child component
                    LayoutClickListenerTest.this.addComponent(new Label(
                            "The click was not over any component."));
                } else {
                    // Over a child component
                    String message = "The click was over a "
                            + clickedComponent.getClass().getCanonicalName()
                            + " in an immediate child component of type "
                            + event.getChildComponent().getClass()
                                    .getCanonicalName();
                    LayoutClickListenerTest.this
                            .addComponent(new Label(message));
                }
            }
        });

        addComponent(layout);
    }

    @Override
    protected String getDescription() {
        return "Layout click listeners should provide access to the deepest nested component clicked - click anywhere in the layout.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6493;
    }

}
