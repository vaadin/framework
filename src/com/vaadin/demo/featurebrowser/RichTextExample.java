/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.featurebrowser;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

/**
 * An example using a RichTextArea to edit a Label in XHTML-mode.
 * 
 */
public class RichTextExample extends CustomComponent {

    public static final String txt = "<h1>RichText editor example</h1>"
            + "To edit this text, press the <b>Edit</b> button below."
            + "<br/>"
            + "See the <A href=\"http://www.vaadin.com/book\">Book of Vaadin</a> "
            + "for more information.";

    private final VerticalLayout main;
    private final Label l;
    private final RichTextArea editor = new RichTextArea();
    private final Button b;

    public RichTextExample() {
        // main layout
        main = new VerticalLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        editor.setWidth("100%");

        // Add the label
        l = new Label(txt);
        l.setContentMode(Label.CONTENT_XHTML);
        main.addComponent(l);
        // Edit button with inline click-listener
        b = new Button("Edit", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                // swap Label <-> RichTextArea
                if (main.getComponentIterator().next() == l) {
                    editor.setValue(l.getValue());
                    main.replaceComponent(l, editor);
                    b.setCaption("Save");
                } else {
                    l.setValue(editor.getValue());
                    main.replaceComponent(editor, l);
                    b.setCaption("Edit");
                }
            }
        });
        main.addComponent(b);
        main.setComponentAlignment(b, Alignment.MIDDLE_RIGHT);
    }

}
