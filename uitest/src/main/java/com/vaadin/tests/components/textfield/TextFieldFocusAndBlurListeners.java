package com.vaadin.tests.components.textfield;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.TextField;

public class TextFieldFocusAndBlurListeners extends TestBase
        implements FocusListener, BlurListener {
    private Log log = new Log(5).setNumberLogRows(false);

    @Override
    protected String getDescription() {
        return "Tests the focus and blur functionality of TextField";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3544;
    }

    @Override
    public void setup() {
        addComponent(log);
        TextField tf1 = new TextField("TextField 1",
                "Has focus and blur listeners");
        tf1.setWidth("300px");
        tf1.addFocusListener(this);
        tf1.addBlurListener(this);

        addComponent(tf1);

        TextField tf2 = new TextField("TextField 2",
                "Has focus, blur and valuechange listeners");
        tf2.setWidth("300px");
        tf2.addValueChangeListener(l -> valueChange(tf2));
        tf2.addFocusListener(this);
        tf2.addBlurListener(this);

        addComponent(tf2);

        TextField tf3 = new TextField("TextField 3",
                "Has non-immediate valuechange listener");
        tf3.setWidth("300px");
        tf3.addValueChangeListener(l -> valueChange(tf3));

        addComponent(tf3);

        TextField tf4 = new TextField("TextField 4",
                "Has immediate valuechange listener");
        tf4.setWidth("300px");
        tf4.addValueChangeListener(l -> valueChange(tf4));

        addComponent(tf4);
    }

    @Override
    public void focus(FocusEvent event) {
        log.log(event.getComponent().getCaption() + ": Focus");

    }

    @Override
    public void blur(BlurEvent event) {
        TextField tf = (TextField) event.getComponent();
        log.log(tf.getCaption() + ": Blur. Value is: " + tf.getValue());

    }

    public void valueChange(TextField source) {
        log.log(source.getCaption() + ": ValueChange: " + source.getValue());
    }
}
