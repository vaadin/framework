package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class GridLayoutWidthChange extends TestBase {

    private GridLayout generateLayout() {
        VerticalLayout fields1 = new VerticalLayout();

        NativeButton nb = new NativeButton("A button");
        nb.setHeight("300px");
        fields1.addComponent(nb);

        VerticalLayout fields3 = new VerticalLayout();
        fields3.addComponent(new TextField("field14"));

        NativeButton b = new NativeButton("A big button");
        b.setWidth("200px");
        b.setHeight("200px");

        GridLayout layout = new GridLayout(3, 2);
        layout.setWidth("100%");
        layout.addComponent(fields1, 0, 0, 0, 1);
        layout.addComponent(b, 2, 1);

        return layout;
    }

    @Override
    protected void setup() {
        final GridLayout layout1 = generateLayout();
        final CustomComponent cc = new CustomComponent(layout1);
        cc.setWidth("500px");
        addComponent(cc);

        Button testButton = new Button("Reduce GridLayout parent width",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        cc.setWidth((cc.getWidth() - 10) + "px");
                    }

                });
        addComponent(testButton);
    }

    @Override
    protected String getDescription() {
        return "A 100% wide GridLayout is wrapped inside a CustomComponent. When the width of the CustomComponent is reduced, the size of the GridLayout should be reduced accordingly. The Buttons should stay in place vertically and just move closer to each other horizontally.";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }
}
