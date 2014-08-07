package com.vaadin.tests.components.button;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ButtonToggleIcons extends UI {

    @Override
    protected void init(final VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        setContent(layout);

        final ThemeResource iconResource = new ThemeResource(
                "../runo/icons/16/arrow-left.png");

        final ClickListener iconToggleListener = new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                final Button btn = event.getButton();
                if (btn.getIcon() == null) {
                    btn.setIcon(iconResource);
                } else {
                    btn.setIcon(null);
                }
            }
        };

        layout.addComponent(new Button("Toggle icon", iconToggleListener));
        layout.addComponent(new NativeButton("Toggle icon", iconToggleListener));
    }
}
