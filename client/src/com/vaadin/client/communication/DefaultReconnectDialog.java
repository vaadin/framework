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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.VOverlay;

/**
 * The default implementation of the reconnect dialog
 * 
 * @since 7.6
 * @author Vaadin Ltd
 */
public class DefaultReconnectDialog extends VOverlay implements ReconnectDialog {
    interface MyUiBinder extends UiBinder<HTMLPanel, DefaultReconnectDialog> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private static final String STYLE_RECONNECTING = "active";

    @UiField
    public Label label;

    private HandlerRegistration clickHandler = null;

    public DefaultReconnectDialog() {
        super(false, true);
        addStyleName("v-reconnect-dialog");
        setWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setText(String text) {
        label.setText(text);
    }

    @Override
    public void setReconnecting(boolean reconnecting) {
        setStyleName(STYLE_RECONNECTING, reconnecting);

        // Click to refresh after giving up
        if (!reconnecting) {
            clickHandler = addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    // refresh
                    WidgetUtil.redirect(null);
                }
            }, ClickEvent.getType());
        } else {
            if (clickHandler != null) {
                clickHandler.removeHandler();
            }
        }
    }

    @Override
    public void show(ApplicationConnection connection) {
        ac = connection;
        show();
    }

    @Override
    public void setPopupPosition(int left, int top) {
        // Don't set inline styles for position, handle it in the theme
    }

    @Override
    public void preload(ApplicationConnection connection) {
        setModal(false); // Don't interfere with application use
        show(connection);
        getElement().getStyle().setVisibility(Visibility.HIDDEN);
        setStyleName(STYLE_RECONNECTING, true);

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                getElement().getStyle().setVisibility(Visibility.VISIBLE);
                setStyleName(STYLE_RECONNECTING, false);
                hide();

            }
        });
    }
}
