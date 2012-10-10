package com.vaadin.tests.components.absolutelayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.NativeButton;

public class AbsoluteLayoutPrimaryStylename extends TestBase {

    @Override
    protected void setup() {
        final AbsoluteLayout layout = new AbsoluteLayout();
        layout.setWidth("100px");
        layout.setWidth("200px");
        layout.setPrimaryStyleName("my-absolute-layout");

        Component comp1 = new NativeButton("Child 1");
        comp1.setWidth("100%");
        comp1.setHeight("50px");
        layout.addComponent(comp1);

        addComponent(layout);

        addComponent(new Button("Change primary stylename",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        layout.setPrimaryStyleName("my-second-absolute-layout");
                    }
                }));
    }

    @Override
    protected String getDescription() {
        return "Absolutelayout should handle setting primary stylename both initially and dynamically";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9899;
    }

}
