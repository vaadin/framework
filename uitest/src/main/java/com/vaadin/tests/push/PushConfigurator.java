/**
 *
 */
package com.vaadin.tests.push;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PushConfiguration;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.TextField;

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
        List<String> names = new ArrayList<>();
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
            transport.addItem(t);
            fallbackTransport.addItem(t);
        }

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
        pushMode.addValueChangeListener(event -> {
            ui.getPushConfiguration()
                    .setPushMode((PushMode) pushMode.getValue());
            refreshStatus();
        });

        transport.addValueChangeListener(event -> {
            Transport t = (Transport) transport.getValue();
            ui.getPushConfiguration().setTransport(t);
            refreshStatus();
        });

        fallbackTransport.addValueChangeListener(event -> {
            Transport t = (Transport) fallbackTransport.getValue();
            ui.getPushConfiguration().setFallbackTransport(t);
            refreshStatus();
        });

        set.addClickListener(event -> {
            ui.getPushConfiguration().setParameter(parameter.getValue(),
                    value.getValue());
            refreshStatus();
        });
    }
}
