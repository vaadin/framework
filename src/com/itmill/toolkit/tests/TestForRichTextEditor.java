/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests;

import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.RichTextArea;

/**
 * 
 * @author IT Mill Ltd.
 */
public class TestForRichTextEditor extends CustomComponent implements
        ValueChangeListener {

    private final OrderedLayout main = new OrderedLayout();

    private Label l;

    private RichTextArea rte;

    public TestForRichTextEditor() {

        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();
        main.addComponent(new Label(
                "RTE uses google richtextArea and their examples toolbar."));

        rte = new RichTextArea();
        rte.addListener(this);

        main.addComponent(rte);

        main.addComponent(new Button("commit content to label below"));

        l = new Label("", Label.CONTENT_XHTML);
        main.addComponent(l);

    }

    public void valueChange(ValueChangeEvent event) {
        l.setValue(rte.getValue());
    }

}
