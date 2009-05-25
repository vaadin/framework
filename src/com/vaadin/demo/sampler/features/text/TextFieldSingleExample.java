package com.vaadin.demo.sampler.features.text;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class TextFieldSingleExample extends VerticalLayout implements
        Property.ValueChangeListener {

    private final TextField editor = new TextField("Echo this:");

    public TextFieldSingleExample() {
        setSpacing(true);

        editor.addListener(this);
        editor.setImmediate(true);
        // editor.setColumns(5); // guarantees that at least 5 chars fit

        addComponent(editor);
    }

    /*
     * Catch the valuechange event of the textfield and update the value of the
     * label component
     */
    public void valueChange(ValueChangeEvent event) {
        // Show the new value we received
        getWindow().showNotification((String) editor.getValue());
    }
}
