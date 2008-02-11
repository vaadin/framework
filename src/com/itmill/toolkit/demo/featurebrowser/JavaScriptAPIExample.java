/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.featurebrowser;

import java.util.Date;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Button.ClickEvent;

/**
 * An example using a RichTextArea to edit a Label in XHTML-mode.
 * 
 */
public class JavaScriptAPIExample extends CustomComponent {

    public static final String txt = "<A href=\"javascript:itmill.forceSync();\">javascript:itmill.forceSync();</A>";

    private final OrderedLayout main;
    private final Label l;
    private final TextField editor = new TextField();

    public JavaScriptAPIExample() {
        // main layout
        main = new OrderedLayout();
        main.setMargin(true);
        setCompositionRoot(main);
        editor.setRows(7);
        editor.setColumns(50);
        // Add the label
        l = new Label(txt);
        l.setContentMode(Label.CONTENT_XHTML);
        main.addComponent(l);
        // Edit button with inline click-listener
        Button b = new Button("Edit", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                // swap Label <-> RichTextArea
                if (main.getComponentIterator().next() == l) {
                    editor.setValue(l.getValue());
                    main.replaceComponent(l, editor);
                    event.getButton().setCaption("Save");
                } else {
                    l.setValue(editor.getValue());
                    main.replaceComponent(editor, l);
                    event.getButton().setCaption("Edit");
                }
            }
        });
        main.addComponent(b);
        main.setComponentAlignment(b, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_VERTICAL_CENTER);

        // 
        Label l = new Label(
                "This label will update it's server-side value AFTER it's rendered to the client-side. "
                        + "The client will be synchronized on reload, when you click a button, "
                        + "or when itmill.forceSync() is called.") {

            public void paintContent(PaintTarget target) throws PaintException {

                super.paintContent(target);
                Delay d = new Delay(this);
                d.start();
            }

        };
        main.addComponent(l);

    }

    private class Delay extends Thread {
        Label label;

        public Delay(Label l) {
            label = l;
        }

        public void run() {
            try {
                Thread.sleep(500);
                label.setValue(new Date().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
