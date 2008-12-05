package com.itmill.toolkit.demo.sampler.features.blueprints;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class ProminentPrimaryActionExample extends OrderedLayout implements
        Button.ClickListener {

    public ProminentPrimaryActionExample() {
        setSpacing(true);

        { // Cancel / Save
            OrderedLayout horiz = new OrderedLayout(ORIENTATION_HORIZONTAL);
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
            OrderedLayout horiz = new OrderedLayout(ORIENTATION_HORIZONTAL);
            horiz.setSpacing(true);
            horiz.setMargin(true);
            addComponent(horiz);
            Button primary = new Button("Sign up", this);
            primary.addStyleName("primary");
            horiz.addComponent(primary);
            Button secondary = new Button("or Sign in", this);
            secondary.setStyleName(Button.STYLE_LINK);
            horiz.setComponentAlignment(secondary, ALIGNMENT_LEFT,
                    ALIGNMENT_VERTICAL_CENTER);
            horiz.addComponent(secondary);
        }

        { // Login / Forgot password?
            OrderedLayout vert = new OrderedLayout();
            vert.setSpacing(true);
            vert.setMargin(true);
            addComponent(vert);
            Button primary = new Button("Login", this);
            vert.addComponent(primary);
            vert.setComponentAlignment(primary, OrderedLayout.ALIGNMENT_RIGHT,
                    OrderedLayout.ALIGNMENT_BOTTOM);
            Button secondary = new Button("Forgot your password?", this);
            secondary.setStyleName(Button.STYLE_LINK);
            vert.addComponent(secondary);
            vert.setComponentAlignment(secondary,
                    OrderedLayout.ALIGNMENT_RIGHT,
                    OrderedLayout.ALIGNMENT_BOTTOM);
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
