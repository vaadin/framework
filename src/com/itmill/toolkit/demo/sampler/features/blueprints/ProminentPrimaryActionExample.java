package com.itmill.toolkit.demo.sampler.features.blueprints;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class ProminentPrimaryActionExample extends OrderedLayout implements
        Button.ClickListener {

    public ProminentPrimaryActionExample() {
        setSpacing(true);

        OrderedLayout horiz = new OrderedLayout(ORIENTATION_HORIZONTAL);
        horiz.setSpacing(true);
        addComponent(horiz);
        Button primary = new Button("Save", this);
        horiz.addComponent(primary);
        Button secondary = new Button("Cancel", this);
        secondary.setStyleName(Button.STYLE_LINK);
        horiz.addComponent(secondary);

        horiz = new OrderedLayout(ORIENTATION_HORIZONTAL);
        horiz.setSpacing(true);
        addComponent(horiz);
        primary = new Button("Sign up", this);
        horiz.addComponent(primary);
        secondary = new Button("or Sign in", this);
        secondary.setStyleName(Button.STYLE_LINK);
        horiz.addComponent(secondary);

        horiz = new OrderedLayout(ORIENTATION_HORIZONTAL);
        horiz.setSpacing(true);
        addComponent(horiz);
        primary = new Button("Login", this);
        horiz.addComponent(primary);
        secondary = new Button("Forgot your password?", this);
        secondary.setStyleName(Button.STYLE_LINK);
        horiz.addComponent(secondary);

    }

    /*
     * Shows a notification when a button is clicked.
     */
    public void buttonClick(ClickEvent event) {
        getWindow().showNotification(
                "\"" + event.getButton().getCaption() + "\" clicked");
    }
}
