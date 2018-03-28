package com.vaadin.tests.widgetset.client;

import com.vaadin.shared.AbstractComponentState;

/**
 * State class with the same simple name as
 * {@link com.vaadin.v7.shared.ui.label.LabelState} to test #8683
 *
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class LabelState extends AbstractComponentState {

    private String text;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
