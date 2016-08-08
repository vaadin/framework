package com.vaadin.tests.components.textfield;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.legacy.ui.LegacyAbstractTextField.TextChangeEventMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class TextChangeEvents2 extends TestBase {
    @Override
    protected void setup() {

        {
            final LegacyTextField tf = new LegacyTextField("Debug");
            getLayout().addComponent(tf);

            tf.addTextChangeListener(new TextChangeListener() {
                @Override
                public void textChange(TextChangeEvent event) {
                    System.err.println(tf.getCaption() + " textChange");
                }
            });

            tf.addListener(new ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    System.err.println(tf.getCaption() + " valueChange");
                }
            });

            tf.addBlurListener(new BlurListener() {

                @Override
                public void blur(BlurEvent event) {
                    System.err.println(tf.getCaption() + " blur");
                }
            });

            tf.addFocusListener(new FocusListener() {
                @Override
                public void focus(FocusEvent event) {
                    System.err.println(tf.getCaption() + " focus");
                }
            });

        }

        {
            final LegacyTextField tf = new LegacyTextField("Label");
            getLayout().addComponent(tf);
            final Label l = new Label();
            getLayout().addComponent(l);
            tf.addTextChangeListener(new TextChangeListener() {
                @Override
                public void textChange(TextChangeEvent event) {
                    l.setValue(event.getText());
                }
            });

            tf.addListener(new ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    System.err.println(tf.getCaption() + " valueChange");
                }
            });

        }

        {
            final LegacyTextField tf = new LegacyTextField("Slow label");
            tf.setTextChangeTimeout(2000);
            tf.setImmediate(true);
            getLayout().addComponent(tf);
            final Label l = new Label();
            getLayout().addComponent(l);
            tf.addTextChangeListener(new TextChangeListener() {

                @Override
                public void textChange(TextChangeEvent event) {
                    l.setValue(event.getText());
                }
            });

            tf.addListener(new ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    System.err.println(tf.getCaption() + " valueChange");
                }
            });

        }

        {
            final LegacyTextField tf = new LegacyTextField("Uppercase");
            tf.setTextChangeTimeout(1);
            getLayout().addComponent(tf);
            final Label l = new Label();
            getLayout().addComponent(l);
            tf.addTextChangeListener(new TextChangeListener() {

                @Override
                public void textChange(TextChangeEvent event) {
                    tf.setValue(event.getText().toUpperCase());
                }
            });

            tf.addListener(new ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    System.err.println(tf.getCaption() + " valueChange");
                }
            });

        }

        {
            final LegacyTextField[] tfs = new LegacyTextField[] { new LegacyTextField(),
                    new LegacyTextField(), new LegacyTextField(), new LegacyTextField() };
            HorizontalLayout hl = new HorizontalLayout();
            hl.setCaption("Blää");
            getLayout().addComponent(hl);
            for (LegacyTextField tf : tfs) {
                tf.setColumns(4);
                tf.setTextChangeEventMode(TextChangeEventMode.EAGER);
                hl.addComponent(tf);

                tf.addTextChangeListener(new TextChangeListener() {

                    @Override
                    public void textChange(TextChangeEvent event) {
                        String txt = event.getText();
                        int len = txt.length();
                        if (len >= 4) {
                            int idx = 0;
                            while (tfs[idx] != event.getComponent()) {
                                idx++;
                            }
                            tfs[idx].setValue("");
                            tfs[idx].setValue(txt.substring(0, 4));
                            if (idx < tfs.length - 1) {
                                LegacyTextField next = tfs[idx + 1];
                                next.focus();
                                if (len > 4) {
                                    next.setValue(txt.substring(4,
                                            len > 8 ? 8 : len));
                                } else {
                                    next.selectAll();
                                }
                            }
                        }
                    }
                });
                tf.addListener(new ValueChangeListener() {

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        LegacyTextField tf = (LegacyTextField) event.getProperty();
                        String val = tf.getValue();
                        if (val != null && val.length() > 4) {
                            tf.setValue(val.substring(0, 4));
                        }
                    }
                });
            }

        }
    }

    @Override
    protected String getDescription() {
        return "Another set of simple use case/tests for TextChangeEvents";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2387;
    }

}
