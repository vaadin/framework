package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * HorizontalLayout and VerticalLayout should not leak caption elements via
 * listeners when removing components from a layout.
 * 
 * @since 7.1.13
 * @author Vaadin Ltd
 */
public class CaptionLeak extends UI {

    public static final String USAGE = "Open this UI with ?debug and count"
            + " measured non-connector elements after setting leaky and non leaky"
            + " content.";

    @Override
    public void init(VaadinRequest req) {
        final VerticalLayout root = new VerticalLayout();
        setContent(root);
        Label usage = new Label(USAGE);
        Panel parent = new Panel();
        Button setLeakyContent = makeButton("Set leaky content", parent,
                VerticalLayout.class);
        Button setNonLeakyContent = makeButton("Set non leaky content", parent,
                CssLayout.class);
        root.addComponents(usage, setLeakyContent, setNonLeakyContent, parent);
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

}
