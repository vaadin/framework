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
package com.vaadin.tests.tooltip;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUIWithLog;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.NativeButton;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.ui.TextField;

public class TooltipConfiguration extends AbstractReindeerTestUIWithLog {

    private TextField closeTimeout;
    private TextField quickOpenTimeout;
    private TextField maxWidth;
    private TextField openDelay;
    private TextField quickOpenDelay;

    @Override
    protected void setup(VaadinRequest request) {
        NativeButton componentWithShortTooltip = new NativeButton(
                "Short tooltip");
        componentWithShortTooltip.setDescription("This is a short tooltip");
        componentWithShortTooltip.setId("shortTooltip");

        NativeButton componentWithLongTooltip = new NativeButton(
                "Long tooltip");
        componentWithLongTooltip.setId("longTooltip");
        componentWithLongTooltip.setDescription(LoremIpsum.get(5000));

        closeTimeout = createIntegerTextField("Close timeout",
                getState().tooltipConfiguration.closeTimeout);
        closeTimeout.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (closeTimeout.getConvertedValue() != null) {
                    getTooltipConfiguration().setCloseTimeout(
                            (Integer) closeTimeout.getConvertedValue());
                }
            }
        });
        maxWidth = createIntegerTextField("Max width",
                getState().tooltipConfiguration.maxWidth);
        maxWidth.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (maxWidth.getConvertedValue() != null) {
                    getTooltipConfiguration().setMaxWidth(
                            (Integer) maxWidth.getConvertedValue());
                }
            }
        });
        openDelay = createIntegerTextField("Open delay",
                getState().tooltipConfiguration.openDelay);
        openDelay.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (openDelay.getConvertedValue() != null) {
                    getTooltipConfiguration().setOpenDelay(
                            (Integer) openDelay.getConvertedValue());
                }
            }
        });

        quickOpenDelay = createIntegerTextField("Quick open delay",
                getState().tooltipConfiguration.quickOpenDelay);
        quickOpenDelay
                .addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        if (quickOpenDelay.getConvertedValue() != null) {
                            getTooltipConfiguration()
                                    .setQuickOpenDelay((Integer) quickOpenDelay
                                            .getConvertedValue());
                        }
                    }
                });

        quickOpenTimeout = createIntegerTextField("Quick open timeout",
                getState().tooltipConfiguration.quickOpenTimeout);
        quickOpenTimeout
                .addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        if (quickOpenTimeout.getConvertedValue() != null) {
                            getTooltipConfiguration().setQuickOpenTimeout(
                                    (Integer) quickOpenTimeout
                                            .getConvertedValue());
                        }
                    }
                });

        getLayout().addComponents(closeTimeout, openDelay, quickOpenDelay,
                quickOpenTimeout, maxWidth);

        getLayout().addComponents(componentWithShortTooltip,
                componentWithLongTooltip);

    }

    private TextField createIntegerTextField(String caption, int initialValue) {
        TextField tf = new TextField(caption);
        tf.setId(caption);
        tf.setConverter(Integer.class);
        tf.setImmediate(true);
        tf.setConvertedValue(initialValue);
        // makes TB3 tests simpler - no "null" added when clearing a field
        tf.setNullRepresentation("");
        return tf;
    }

    @Override
    protected String getTestDescription() {
        return "Tests that tooltip delays can be configured";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8065;
    }

}
