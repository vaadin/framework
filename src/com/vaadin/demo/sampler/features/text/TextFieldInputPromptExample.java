package com.vaadin.demo.sampler.features.text;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class TextFieldInputPromptExample extends VerticalLayout implements
        Property.ValueChangeListener {

    public TextFieldInputPromptExample() {
        // add som 'air' to the layout
        setSpacing(true);
        setMargin(true, false, false, false);

        // Username field + input prompt
        TextField username = new TextField();
        username.setInputPrompt("Username");
        // configure & add to layout
        username.setImmediate(true);
        username.addListener(this);
        addComponent(username);

        // Password field + input prompt
        TextField password = new TextField();
        password.setInputPrompt("Password");
        // configure & add to layout
        password.setSecret(true);
        password.setImmediate(true);
        password.addListener(this);
        addComponent(password);

        // Comment field + input prompt
        TextField comment = new TextField();
        comment.setInputPrompt("Comment");
        // configure & add to layout
        comment.setRows(3);
        comment.setImmediate(true);
        comment.addListener(this);
        addComponent(comment);

    }

    public void valueChange(ValueChangeEvent event) {
        getWindow().showNotification("Received " + event.getProperty());

    }

}
