package com.itmill.toolkit.demo.sampler.features.text;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.RichTextArea;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class LabelRichExample extends VerticalLayout implements ClickListener {

    private Button b;
    private Label richText;

    private final RichTextArea editor = new RichTextArea();

    public LabelRichExample() {
        setSpacing(true);
        setSizeUndefined(); // let layout grow with content

        richText = new Label("<h1>Rich text label example</h1>"
                + "<p>In this example, the content mode is set to "
                + "<i>CONTENT_XHTML</i>.</p>"
                + "<p>This text can be edited with the Edit-button</p>");
        richText.setContentMode(Label.CONTENT_XHTML);

        addComponent(richText);

        b = new Button("Edit");
        b.addListener(this);
        addComponent(b);
        setComponentAlignment(b, "right");
    }

    public void buttonClick(ClickEvent event) {
        if (getComponentIterator().next() == richText) {
            editor.setValue(richText.getValue());
            replaceComponent(richText, editor);
            b.setCaption("Apply");
        } else {
            richText.setValue(editor.getValue());
            replaceComponent(editor, richText);
            b.setCaption("Edit");
        }
    }

}
