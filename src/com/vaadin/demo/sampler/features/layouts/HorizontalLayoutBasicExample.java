package com.vaadin.demo.sampler.features.layouts;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
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
