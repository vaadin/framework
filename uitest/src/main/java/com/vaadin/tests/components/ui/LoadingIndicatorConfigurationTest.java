package com.vaadin.tests.components.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.v7.ui.TextField;

public class LoadingIndicatorConfigurationTest extends AbstractTestUIWithLog {

    private TextField firstDelay;
    private TextField secondDelay;
    private TextField thirdDelay;

    @Override
    protected void setup(VaadinRequest request) {
        final TextField delayField = new TextField("Delay (ms)");
        delayField.setConverter(Integer.class);
        delayField.setConvertedValue(1000);

        NativeButton delayButton = new NativeButton("Wait");
        delayButton.addClickListener(event -> {
            try {
                Thread.sleep((Integer) delayField.getConvertedValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        firstDelay = createIntegerTextField("First delay (ms)",
                getState().loadingIndicatorConfiguration.firstDelay);
        firstDelay.addValueChangeListener(
                value -> getLoadingIndicatorConfiguration().setFirstDelay(
                        (Integer) firstDelay.getConvertedValue()));
        secondDelay = createIntegerTextField("Second delay (ms)",
                getState().loadingIndicatorConfiguration.secondDelay);
        secondDelay.addValueChangeListener(
                value -> getLoadingIndicatorConfiguration().setSecondDelay(
                        (Integer) secondDelay.getConvertedValue()));
        thirdDelay = createIntegerTextField("Third delay (ms)",
                getState().loadingIndicatorConfiguration.thirdDelay);
        thirdDelay.addValueChangeListener(
                value -> getLoadingIndicatorConfiguration().setThirdDelay(
                        (Integer) thirdDelay.getConvertedValue()));

        getLayout().addComponents(firstDelay, secondDelay, thirdDelay);

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
