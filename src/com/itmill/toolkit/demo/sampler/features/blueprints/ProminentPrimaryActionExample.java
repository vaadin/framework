package com.itmill.toolkit.demo.sampler.features.blueprints;

import com.itmill.toolkit.ui.Alignment;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class ProminentPrimaryActionExample extends VerticalLayout implements
        Button.ClickListener {

    public ProminentPrimaryActionExample() {
        setSpacing(true);

        { // Cancel / Save
            HorizontalLayout horiz = new HorizontalLayout();
            horiz.setCaption("Save/cancel example:");
            horiz.setSpacing(true);
            horiz.setMargin(true);
            addComponent(horiz);
            Button secondary = new Button("Cancel", this);
            secondary.setStyleName(Button.STYLE_LINK);
            horiz.addComponent(secondary);
            Button primary = new Button("Save", this);
            horiz.addComponent(primary);
        }

        { // Sign up / Sign in
            HorizontalLayout horiz = new HorizontalLayout();
            horiz.setCaption("Sign up example:");
            horiz.setSpacing(true);
            horiz.setMargin(true);
            addComponent(horiz);
            Button primary = new Button("Sign up", this);
            primary.addStyleName("primary");
            horiz.addComponent(primary);
            Button secondary = new Button("or Sign in", this);
            secondary.setStyleName(Button.STYLE_LINK);
            horiz.addComponent(secondary);
            horiz.setComponentAlignment(secondary, Alignment.MIDDLE_LEFT);
        }

        { // Login / Forgot password?
            VerticalLayout vert = new VerticalLayout();
            vert.setCaption("Login example:");
            vert.setSizeUndefined();
            vert.setSpacing(true);
            vert.setMargin(true);
            addComponent(vert);
            Button primary = new Button("Login", this);
            vert.addComponent(primary);
            vert.setComponentAlignment(primary, Alignment.BOTTOM_RIGHT);
            Button secondary = new Button("Forgot your password?", this);
            secondary.setStyleName(Button.STYLE_LINK);
            vert.addComponent(secondary);
            vert.setComponentAlignment(secondary, Alignment.BOTTOM_RIGHT);
        }

    }

    /*
     * Shows a notification when a button is clicked.
     */
    public void buttonClick(ClickEvent event) {
        getWindow().showNotification(
                "\"" + event.getButton().getCaption() + "\" clicked");
    }
}
