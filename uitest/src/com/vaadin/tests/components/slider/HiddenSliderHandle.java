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
package com.vaadin.tests.components.slider;

import com.vaadin.data.Item;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Table;

public class HiddenSliderHandle extends AbstractTestUI {

    private static final long serialVersionUID = 1L;

    @Override
    protected void setup(VaadinRequest request) {
        Table t = new Table();
        Slider s = new Slider();
        t.setWidth("200px");
        s.setWidth("100px");
        t.addContainerProperty("s", Slider.class, null);
        Item i = t.addItem("123");
        i.getItemProperty("s").setValue(s);
        getLayout().addComponent(t);
    }

    @Override
    protected String getTestDescription() {
        return "Slider's handler should be accessible (visible) if slider is put inside table";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13681;
    }

}
