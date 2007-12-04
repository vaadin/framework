/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.featurebrowser;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.RichTextArea;
import com.itmill.toolkit.ui.Button.ClickEvent;

/**
 * An example using a RichTextArea to edit a Label in XHTML-mode.
 * 
 */
public class RichTextExample extends CustomComponent {

    public static final String txt = "<h1>RichText editor example</h1>"
            + "To edit this text, press the <b>Edit</b> button below."
            + "<br/>"
            + "See the <A href=\"http://www.itmill.com/manual/\">manual</a> "
            + "for more information.";

    private final OrderedLayout main;
    private final Label l;
    private final RichTextArea editor = new RichTextArea();
    private final Button b;

    public RichTextExample() {
        // main layout
        main = new OrderedLayout();
        main.setMargin(true);
        setCompositionRoot(main);
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
        main.setComponentAlignment(b, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_VERTICAL_CENTER);
    }

}
