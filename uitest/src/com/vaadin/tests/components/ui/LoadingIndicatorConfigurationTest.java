package com.vaadin.tests.components.ui;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;

public class LoadingIndicatorConfigurationTest extends AbstractTestUIWithLog {

    private TextField initialDelay;
    private TextField delayStateDelay;
    private TextField waitStateDelay;

    @Override
    protected void setup(VaadinRequest request) {
        final TextField delayField = new TextField("Delay (ms)");
        delayField.setConverter(Integer.class);
        delayField.setConvertedValue(1000);

        NativeButton delayButton = new NativeButton("Wait");
        delayButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    Thread.sleep((Integer) delayField.getConvertedValue());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        initialDelay = createIntegerTextField("Initial delay (ms)",
                getState().loadingIndicatorConfiguration.initialDelay);
        initialDelay.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                getLoadingIndicatorConfiguration().setInitialDelay(
                        (Integer) initialDelay.getConvertedValue());
            }
        });
        delayStateDelay = createIntegerTextField("Delay state delay (ms)",
                getState().loadingIndicatorConfiguration.delayStateDelay);
        delayStateDelay
                .addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        getLoadingIndicatorConfiguration().setDelayStateDelay(
                                (Integer) delayStateDelay.getConvertedValue());
                    }
                });
        waitStateDelay = createIntegerTextField("Wait state delay (ms)",
                getState().loadingIndicatorConfiguration.waitStateDelay);
        waitStateDelay
                .addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        getLoadingIndicatorConfiguration().setWaitStateDelay(
                                (Integer) waitStateDelay.getConvertedValue());
                    }
                });

        getLayout()
                .addComponents(initialDelay, delayStateDelay, waitStateDelay);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setMargin(true);
        hl.setDefaultComponentAlignment(Alignment.BOTTOM_RIGHT);
        hl.addComponents(delayField, delayButton);
        addComponent(hl);

    }

    private TextField createIntegerTextField(String caption, int initialValue) {
        TextField tf = new TextField(caption);
        tf.setId(caption);
        tf.setConverter(Integer.class);
        tf.setImmediate(true);
        tf.setConvertedValue(initialValue);
        return tf;
    }

    @Override
    protected String getTestDescription() {
        return "Tests that loading indicator delay can be configured";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7448;
    }

}
