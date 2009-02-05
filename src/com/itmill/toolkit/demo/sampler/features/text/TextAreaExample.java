package com.itmill.toolkit.demo.sampler.features.text;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TextField;

public class TextAreaExample extends HorizontalLayout implements
        Property.ValueChangeListener {

    private static final String initialText = "The quick brown fox jumps over the lazy dog.";

    private Label plainText;
    private final TextField editor;

    public TextAreaExample() {
        setSpacing(true);

        editor = new TextField("", initialText);
        editor.addListener(this);
        editor.setImmediate(true);
        editor.setColumns(20);
        editor.setRows(20);
        addComponent(editor);

        addComponent(new Button(">>"));

        plainText = new Label(initialText);
        plainText.setContentMode(Label.CONTENT_XHTML);
        addComponent(plainText);
    }

    /*
     * Catch the valuechange event of the textfield and update the value of the
     * label component
     */
    public void valueChange(ValueChangeEvent event) {
        String text = (String) editor.getValue();
        if (text != null) {
            // replace newline with BR, because we're using Label.CONTENT_XHTML
            text = text.replaceAll("\n", "<br/>");
        }
        plainText.setValue(text);
    }
}
