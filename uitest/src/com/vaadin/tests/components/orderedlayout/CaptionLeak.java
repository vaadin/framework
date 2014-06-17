package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * HorizontalLayout and VerticalLayout should not leak caption elements via
 * listeners when removing components from a layout.
 * 
 * @since 7.1.13
 * @author Vaadin Ltd
 */
public class CaptionLeak extends AbstractTestUI {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(false);
        root.setSpacing(false);

        HorizontalLayout layout = new HorizontalLayout();
        Panel parent = new Panel();
        Button setLeakyContent = makeButton("Set leaky content", parent,
                VerticalLayout.class);
        Button setNonLeakyContent = makeButton("Set non leaky content", parent,
                CssLayout.class);
        layout.addComponent(setLeakyContent);
        layout.addComponent(setNonLeakyContent);
        root.addComponent(layout);
        root.addComponent(parent);
        setContent(root);
    }

    private Button makeButton(String caption, final Panel parent,
            final Class<? extends ComponentContainer> targetClass) {
        Button btn = new Button(caption);
        btn.setId(caption);
        btn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    ComponentContainer target = targetClass.newInstance();
                    for (int i = 0; i < 61; i++) {
                        target.addComponent(new TextField("Test"));
                    }
                    parent.setContent(target);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return btn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Open this UI with ?debug and count measured non-connector elements after setting leaky and non leaky content.";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
