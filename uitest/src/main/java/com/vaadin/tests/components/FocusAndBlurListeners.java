package com.vaadin.tests.components;

import java.util.Date;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.OptionGroup;

public class FocusAndBlurListeners extends TestBase {

    private FocusListener focusListener = new FocusListener() {

        @Override
        public void focus(FocusEvent event) {
            Label msg = new Label(new Date() + " Focused "
                    + event.getComponent().getCaption());
            messages.addComponentAsFirst(msg);
        }
    };
    private BlurListener blurListener = new BlurListener() {

        @Override
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

        AbstractDateField<?, ?> df = new TestDateField("DateField");
        l.addComponent(df);

        ComboBox cb = new ComboBox("ComboBox");
        l.addComponent(cb);

        Button btn = new Button("Button");
        l.addComponent(btn);

        NativeButton nbtn = new NativeButton("NativeButton");
        l.addComponent(nbtn);

        CheckBox chkb = new CheckBox("CheckBox");
        l.addComponent(chkb);

        OptionGroup og = createOptionGroup("OptionGroup");
        og.setMultiSelect(false);
        l.addComponent(og);

        final OptionGroup ogm = createOptionGroup("OptionGroup (multiselect)");
        ogm.setMultiSelect(true);
        l.addComponent(ogm);

        btn.addClickListener(new ClickListener() {

            private int i;

            @Override
            public void buttonClick(ClickEvent event) {
                ogm.addItem("newItem" + i++);

            }
        });

        tf.addFocusListener(focusListener);
        tf.addBlurListener(blurListener);
        df.addFocusListener(focusListener);
        df.addBlurListener(blurListener);
        cb.addFocusListener(focusListener);
        cb.addBlurListener(blurListener);
        btn.addFocusListener(focusListener);
        btn.addBlurListener(blurListener);
        nbtn.addFocusListener(focusListener);
        nbtn.addBlurListener(blurListener);
        chkb.addFocusListener(focusListener);
        chkb.addBlurListener(blurListener);
        og.addFocusListener(focusListener);
        og.addBlurListener(blurListener);
        ogm.addFocusListener(focusListener);
        ogm.addBlurListener(blurListener);

        l.addComponent(messages);

    }

    private OptionGroup createOptionGroup(String caption) {
        OptionGroup og = new OptionGroup(caption);
        og.addItem("Option 0");
        og.addItem("Option 1");
        og.addItem("Option 2");
        return og;
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
