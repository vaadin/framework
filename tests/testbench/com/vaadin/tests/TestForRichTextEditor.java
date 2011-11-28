/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author IT Mill Ltd.
 */
public class TestForRichTextEditor extends CustomComponent implements
        ValueChangeListener {

    private final VerticalLayout main = new VerticalLayout();

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

        CheckBox b = new CheckBox("enabled");
        b.setImmediate(true);
        b.addListener(new Property.ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                rte.setEnabled(!rte.isEnabled());
            }
        });
        main.addComponent(b);

    }

    public void valueChange(ValueChangeEvent event) {
        l.setValue(rte.getValue());
    }

}
