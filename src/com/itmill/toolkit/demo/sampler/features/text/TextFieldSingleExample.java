package com.itmill.toolkit.demo.sampler.features.text;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.VerticalLayout;

public class TextFieldSingleExample extends VerticalLayout implements
        Property.ValueChangeListener {

    private Label plainText;
    private final TextField editor = new TextField();

    public TextFieldSingleExample() {
        setSpacing(true);

        plainText = new Label("Initial text");
        plainText.setContentMode(Label.CONTENT_TEXT);

        editor.addListener(this);
        editor.setImmediate(true);
        editor.setColumns(5);
        // editor.setSecret(true);

        addComponent(plainText);
        addComponent(editor);
    }

    /*
     * Catch the valuechange event of the textfield and update the value of the
     * label component
     */
    public void valueChange(ValueChangeEvent event) {
        plainText.setValue(editor.getValue());
    }
}
