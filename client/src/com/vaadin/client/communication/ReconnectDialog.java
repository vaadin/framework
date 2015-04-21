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
package com.vaadin.client.communication;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.client.ui.VOverlay;

/**
 * 
 * 
 * @since 7.6
 * @author Vaadin Ltd
 */
public class ReconnectDialog extends VOverlay {
    interface MyUiBinder extends UiBinder<HTMLPanel, ReconnectDialog> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    public Label label;

    public ReconnectDialog() {
        super(false, true);
        addStyleName("v-reconnect-dialog");
        setWidget(uiBinder.createAndBindUi(this));
    }

    public void setText(String text) {
        label.setText(text);
    }
}
