package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TextField;

public class HorizontalLayoutBasicExample extends HorizontalLayout {

    public HorizontalLayoutBasicExample() {
        // this is a HorizontalLayout

        // First TextField
        TextField tf = new TextField();
        tf.setWidth("70px");
        addComponent(tf);

        // A dash
        Label dash = new Label("-");
        addComponent(dash);
        setComponentAlignment(dash, "middle");

        // Second TextField
        tf = new TextField();
        tf.setWidth("70px");
        addComponent(tf);

        // Another dash
        dash = new Label("-");
        addComponent(dash);
        setComponentAlignment(dash, "middle");

        // Third TextField
        tf = new TextField();
        tf.setWidth("70px");
        addComponent(tf);

        // Yet another dash
        dash = new Label("-");
        addComponent(dash);
        setComponentAlignment(dash, "middle");

        // Forth and last TextField
        tf = new TextField();
        tf.setWidth("70px");
        addComponent(tf);

    }
}
