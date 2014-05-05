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
package com.vaadin.tests.push;

import java.util.ArrayList;
import java.util.Collections;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PushConfiguration;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public class PushConfigurator extends VerticalLayout {
    private NativeSelect pushMode = new NativeSelect("Push mode");
    private NativeSelect transport = new NativeSelect("Transport");
    private NativeSelect fallbackTransport = new NativeSelect("Fallback");
    private TextField parameter = new TextField("Parameter");
    private TextField value = new TextField("Value");
    private Button set = new Button("Set");
    private HorizontalLayout paramValue = new HorizontalLayout();
    private VerticalLayout vl = new VerticalLayout();
    private UI ui;

    private Label status = new Label("", ContentMode.PREFORMATTED);

    public PushConfigurator(UI ui) {
        this.ui = ui;
        construct();
        refreshStatus();
    }

    /**
     * @since
     */
    private void refreshStatus() {
        PushConfiguration pc = ui.getPushConfiguration();
        String value = "";
        ArrayList<String> names = new ArrayList<String>();
        names.addAll(pc.getParameterNames());
        Collections.sort(names);
        for (String param : names) {
            value += param + ": " + pc.getParameter(param) + "\n";
        }
        status.setValue(value);
    }

    /**
     * @since
     */
    private void construct() {
        pushMode.addItem(PushMode.DISABLED);
        pushMode.addItem(PushMode.MANUAL);
        pushMode.addItem(PushMode.AUTOMATIC);

        for (Transport t : Transport.values()) {
            transport.addItem(t.toString());
            fallbackTransport.addItem(t.toString());
        }
        transport.addItem("");
        fallbackTransport.addItem("");

        pushMode.setImmediate(true);
        transport.setImmediate(true);
        fallbackTransport.setImmediate(true);

        listeners();

        paramValue.setDefaultComponentAlignment(Alignment.BOTTOM_RIGHT);
        paramValue.addComponents(parameter, value, set);
        status.setId("status");
        vl.addComponents(pushMode, transport, fallbackTransport, paramValue,
                new Label("<hr/>", ContentMode.HTML), status);
        addComponent(vl);

    }

    /**
     * @since
     */
    private void listeners() {
        pushMode.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                ui.getPushConfiguration().setPushMode(
                        (PushMode) pushMode.getValue());
                refreshStatus();
            }
        });

        transport.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                Transport t = Transport.valueOf((String) transport.getValue());
                ui.getPushConfiguration().setTransport(t);
                refreshStatus();
            }
        });

        fallbackTransport.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                Transport t = Transport.valueOf((String) fallbackTransport
                        .getValue());
                ui.getPushConfiguration().setFallbackTransport(t);
                refreshStatus();
            }
        });

        set.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                ui.getPushConfiguration().setParameter(parameter.getValue(),
                        value.getValue());
                refreshStatus();
            }
        });

    }
}
