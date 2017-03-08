/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
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

        firstDelay = createIntegerTextField("First delay (ms)",
                getState().loadingIndicatorConfiguration.firstDelay);
        firstDelay.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                getLoadingIndicatorConfiguration().setFirstDelay(
                        (Integer) firstDelay.getConvertedValue());
            }
        });
        secondDelay = createIntegerTextField("Second delay (ms)",
                getState().loadingIndicatorConfiguration.secondDelay);
        secondDelay.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                getLoadingIndicatorConfiguration().setSecondDelay(
                        (Integer) secondDelay.getConvertedValue());
            }
        });
        thirdDelay = createIntegerTextField("Third delay (ms)",
                getState().loadingIndicatorConfiguration.thirdDelay);
        thirdDelay.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                getLoadingIndicatorConfiguration().setThirdDelay(
                        (Integer) thirdDelay.getConvertedValue());
            }
        });

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
