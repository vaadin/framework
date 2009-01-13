package com.itmill.toolkit.demo.sampler.features.text;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.VerticalLayout;

public class TextAreaExample extends VerticalLayout implements
        Property.ValueChangeListener {

    private Label plainText;
    private final TextField editor = new TextField();

    public TextAreaExample() {
        setSpacing(true);

        plainText = new Label("Initial text.\n"
                + "\nPlease note that within a textarea,"
                + "\nthe enter key will not dispatch the"
                + "\nchanges to the server. To fire a"
                + "\nvaluechange event, you must deactivate"
                + "\nthe textarea.");
        plainText.setContentMode(Label.CONTENT_PREFORMATTED);

        editor.addListener(this);
        editor.setImmediate(true);
        editor.setColumns(20);
        editor.setRows(20);
        // editor.setSecret(true);

        addComponent(editor);
        addComponent(plainText);
    }

    /*
     * Catch the valuechange event of the textfield and update the value of the
     * label component
     */
    public void valueChange(ValueChangeEvent event) {
        plainText.setValue(editor.getValue());
    }
}
