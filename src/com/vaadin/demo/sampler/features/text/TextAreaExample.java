package com.vaadin.demo.sampler.features.text;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class TextAreaExample extends HorizontalLayout implements
        Property.ValueChangeListener {

    private static final String initialText = "The quick brown fox jumps over the lazy dog.";

    private Label plainText;
    private final TextField editor;

    public TextAreaExample() {
        setSpacing(true);

        editor = new TextField("", initialText);
        editor.setRows(20); // this will make it an 'area', i.e multiline
        editor.setColumns(20);
        editor.addListener(this);
        editor.setImmediate(true);
        addComponent(editor);

        // the TextArea is immediate, and it's valueCahnge updates the Label,
        // so this button actually does nothing
        addComponent(new Button(">"));

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
