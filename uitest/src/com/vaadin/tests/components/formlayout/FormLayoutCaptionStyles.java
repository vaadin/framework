package com.vaadin.tests.components.formlayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

public class FormLayoutCaptionStyles extends TestBase {

    @Override
    protected void setup() {
        setTheme("reindeer-tests");
        FormLayout fl = new FormLayout();

        TextField f1 = createTextField("Text field 1", "");
        final TextField f2 = createTextField("Text field 2", "bold");

        fl.addComponent(f1);
        fl.addComponent(new Button("Toggle Text field 2 bold style",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        if ("bold".equals(f2.getStyleName())) {
                            f2.setStyleName("");
                        } else {
                            f2.setStyleName("bold");
                        }

                    }

                }));
        fl.addComponent(f2);

        addComponent(fl);

    }

    private TextField createTextField(String caption, String style) {
        TextField tf = new TextField(caption);
        tf.setStyleName(style);
        return tf;
    }

    @Override
    protected String getDescription() {
        return "The component style should be copied to the caption element. Changing the component style should update the caption style also";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5982;
    }

}
