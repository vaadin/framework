/*
 * Copyright 2012 Vaadin Ltd.
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

package com.vaadin.tests.components.form;

import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class FormErrorVisible extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(buildForm("With footer and error", true, true));
        addComponent(buildForm("With footer", false, true));
        addComponent(buildForm("With error", true, false));
        addComponent(buildForm("With nothing", false, false));
        addComponent(new Label("The end to see where the last form ends"));
    }

    private Form buildForm(String caption, boolean withError, boolean withFooter) {
        Form form = new Form();
        form.setCaption(caption);
        form.addField("value", new TextField("MyField"));

        if (withError) {
            form.setComponentError(new UserError("Has error"));
        }

        if (withFooter) {
            form.getFooter().addComponent(new Button("Footer button"));
        } else {
            form.setFooter(null);
        }

        return form;
    }

    @Override
    protected String getTestDescription() {
        return "Footer and error should be taken into account when rendering Form";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(10460);
    }

}
