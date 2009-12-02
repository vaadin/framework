package com.vaadin.tests.components;

import java.util.Date;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class FocusAndBlurListeners extends TestBase {

    private FocusListener focusListener = new FocusListener() {

        public void focus(FocusEvent event) {
            Label msg = new Label(new Date() + " Focused "
                    + event.getComponent().getCaption());
            messages.addComponentAsFirst(msg);
        }
    };
    private BlurListener blurListener = new BlurListener() {

        public void blur(BlurEvent event) {
            Label msg = new Label(new Date() + " Blurred "
                    + event.getComponent().getCaption());
            messages.addComponentAsFirst(msg);

        }
    };
    private VerticalLayout messages = new VerticalLayout();

    @Override
    protected void setup() {
        Layout l = getLayout();
        TextField tf = new TextField("TextField");
        l.addComponent(tf);
        DateField df = new DateField("DateField");
        l.addComponent(df);

        ComboBox cb = new ComboBox("ComboBox");

        l.addComponent(cb);

        tf.addListener(focusListener);
        tf.addListener(blurListener);
        df.addListener(focusListener);
        df.addListener(blurListener);
        cb.addListener(focusListener);
        cb.addListener(blurListener);

        l.addComponent(messages);

    }

    @Override
    protected String getDescription() {
        return "Testing blur and focus listeners added in 6.2";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
