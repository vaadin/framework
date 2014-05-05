/*
 * Copyright 2000-2014 Vaadin Ltd.
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

/**
 * 
 */
package com.vaadin.tests.components.datefield;

import java.util.Arrays;
import java.util.Calendar;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public class PopupDateFieldValueChangeEvents extends AbstractTestUI {

    private int count = 0;

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {

        HorizontalLayout hl = new HorizontalLayout();
        addComponent(hl);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, 1, 1, 18, 19, 20);

        final DateField df = new DateField(null, calendar.getTime());
        df.setResolution(Resolution.SECOND);
        df.setImmediate(true);
        hl.addComponent(df);

        NativeSelect resolution = new NativeSelect(null,
                Arrays.asList(Resolution.values()));
        resolution.setImmediate(true);
        resolution.setValue(df.getResolution());
        hl.addComponent(resolution);
        resolution.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                df.setResolution((Resolution) event.getProperty().getValue());
            }
        });

        final Label log = new Label("", ContentMode.PREFORMATTED);
        addComponent(log);

        df.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                log.setValue("Value changes: " + (++count));

            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "DateField Time resolution fields should only send events when focus is removed";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 6252;
    }

}
