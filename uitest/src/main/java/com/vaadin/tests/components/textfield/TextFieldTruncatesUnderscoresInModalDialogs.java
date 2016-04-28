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

package com.vaadin.tests.components.textfield;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
@Theme("chameleon")
public class TextFieldTruncatesUnderscoresInModalDialogs extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Window dialog = new Window();

        FormLayout formLayout = new FormLayout();
        formLayout.setSpacing(true);

        formLayout.addComponent(new Button("Disappear",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        event.getButton().setVisible(false);
                    }
                }));

        formLayout.addComponent(new TextField(null, "____pqjgy____"));

        dialog.setContent(formLayout);

        getUI().addWindow(dialog);
    }

    @Override
    protected String getTestDescription() {
        return "Text field must not truncate underscores in modal dialogs.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12974;
    }

}
