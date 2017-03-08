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
package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.TextField;

public class HorizontalLayoutFullsizeContentWithErrorMsg
        extends AbstractReindeerTestUI {

    static final String FIELD_ID = "f";
    static final String BUTTON_ID = "b";
    private TextField tf;

    @Override
    protected Integer getTicketNumber() {
        return 12564;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("500px");

        tf = new TextField();
        tf.setId(FIELD_ID);
        tf.setWidth("100%");
        hl.addComponent(tf);
        hl.setExpandRatio(tf, 1);
        hl.setComponentAlignment(tf, Alignment.MIDDLE_CENTER);

        Button toggleError = new Button("Toggle error");
        toggleError.setId(BUTTON_ID);
        toggleError.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                tf.setComponentError(tf.getComponentError() == null
                        ? new UserError("foo") : null);
            }
        });
        hl.addComponent(toggleError);

        addComponent(hl);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "TextField should remain at same level vertically, horizontally width should adjust to fit error indicator.";
    }

}
