/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.client.ui.notification;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.VNotification;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.notification.NotificationServerRpc;
import com.vaadin.shared.ui.notification.NotificationState;
import com.vaadin.ui.Notification;

/**
 * The client-side connector for the {@code Notification}.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.2
 */
@Connect(value = Notification.class)
public class NotificationConnector extends AbstractExtensionConnector {

    private VNotification notification;

    @Override
    public NotificationState getState() {
        return (NotificationState) super.getState();
    }

    @Override
    protected void extend(ServerConnector target) {
        NotificationState state = getState();
        notification = VNotification.showNotification(target.getConnection(),
                state.caption, state.description, state.htmlContentAllowed,
                getResourceUrl("icon"), state.styleName, state.position,
                state.delay);

        notification.addCloseHandler(new CloseHandler<PopupPanel>() {

            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                if (getParent() == null) {
                    // Unregistered already
                    return;
                }
                NotificationServerRpc rpc = getRpcProxy(
                        NotificationServerRpc.class);
                rpc.closed();
                notification = null;
            }
        });
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        if (notification != null) {
            notification.hide();
            notification = null;
        }
    }
}
