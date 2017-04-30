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
package com.vaadin.test.webcomponent.app;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Binder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.test.webcomponent.addon.PaperSlider;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class WebComponentAppUI extends UI {

    private TextField min;
    private TextField value;
    private TextField max;

    @Override
    protected void init(VaadinRequest request) {
        min = new TextField("Min");
        value = new TextField("value");
        max = new TextField("Max");

        VerticalLayout layout = new VerticalLayout();
        setContent(layout);
        layout.addComponent(new HorizontalLayout(min, value, max));
        PaperSlider slider = new PaperSlider();
        slider.setId("slider");
        layout.addComponent(slider);

        Binder<PaperSlider> binder = new Binder<>(PaperSlider.class);
        binder.bind(min, s -> String.valueOf(s.getMin()),
                (s, value) -> s.setMin(Integer.parseInt(value)));
        binder.bind(value, s -> String.valueOf(s.getValue()),
                (s, value) -> s.setValue(Integer.parseInt(value)));
        binder.bind(max, s -> String.valueOf(s.getMax()),
                (s, value) -> s.setMax(Integer.parseInt(value)));
        binder.setBean(slider);

        slider.addValueChangeListener(e -> {
            if (e.isUserOriginated()) {
                value.setValue(String.valueOf(slider.getValue()));
            }
        });
        value.setValue("23");
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = WebComponentAppUI.class, productionMode = false)
    public static class WCServlet extends VaadinServlet {
    }
}
