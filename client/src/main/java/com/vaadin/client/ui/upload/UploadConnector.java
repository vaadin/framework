/*
 * Copyright 2000-2018 Vaadin Ltd.
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

package com.vaadin.client.ui.upload;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.VUpload;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.upload.UploadClientRpc;
import com.vaadin.shared.ui.upload.UploadState;
import com.vaadin.ui.Upload;

@Connect(Upload.class)
public class UploadConnector extends AbstractComponentConnector
        implements Paintable {

    public UploadConnector() {
        registerRpc(UploadClientRpc.class, () -> getWidget().submit());
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (!isRealUpdate(uidl)) {
            return;
        }
        VUpload upload = getWidget();
        if (uidl.hasAttribute("notStarted")) {
            upload.t.schedule(400);
            return;
        }
        upload.setImmediateMode(getState().immediateMode);
        upload.client = client;
        upload.paintableId = uidl.getId();
        upload.nextUploadId = uidl.getIntAttribute("nextid");
        final String action = client
                .translateVaadinUri(uidl.getStringVariable("action"));
        upload.element.setAction(action);
        if (uidl.hasAttribute("buttoncaption")) {
            upload.submitButton
                    .setText(uidl.getStringAttribute("buttoncaption"));
            if (uidl.hasAttribute("buttonstylename")) {
                upload.submitButton.setStyleName(
                        uidl.getStringAttribute("buttonstylename"));
            }
            upload.submitButton.setVisible(true);
        } else {
            upload.submitButton.setVisible(false);
        }
        upload.fu.setName(upload.paintableId + "_file");

        if (!isEnabled()) {
            upload.disableUpload();
        } else if (!uidl.getBooleanAttribute("state")) {
            // Enable the button only if an upload is not in progress
            upload.enableUpload();
            upload.ensureTargetFrame();
        }
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().disableTitle(hasTooltip());
    }

    @Override
    public VUpload getWidget() {
        return (VUpload) super.getWidget();
    }

    @Override
    public UploadState getState() {
        return (UploadState) super.getState();
    }
}
