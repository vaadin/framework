/*
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.terminal.gwt.client.ui.upload;

import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;
import com.vaadin.ui.Upload;

@Connect(value = Upload.class, loadStyle = LoadStyle.LAZY)
public class UploadConnector extends AbstractComponentConnector implements
        Paintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (!isRealUpdate(uidl)) {
            return;
        }
        if (uidl.hasAttribute("notStarted")) {
            getWidget().t.schedule(400);
            return;
        }
        if (uidl.hasAttribute("forceSubmit")) {
            getWidget().submit();
            return;
        }
        getWidget().setImmediate(getState().isImmediate());
        getWidget().client = client;
        getWidget().paintableId = uidl.getId();
        getWidget().nextUploadId = uidl.getIntAttribute("nextid");
        final String action = client.translateVaadinUri(uidl
                .getStringVariable("action"));
        getWidget().element.setAction(action);
        if (uidl.hasAttribute("buttoncaption")) {
            getWidget().submitButton.setText(uidl
                    .getStringAttribute("buttoncaption"));
            getWidget().submitButton.setVisible(true);
        } else {
            getWidget().submitButton.setVisible(false);
        }
        getWidget().fu.setName(getWidget().paintableId + "_file");

        if (!isEnabled() || isReadOnly()) {
            getWidget().disableUpload();
        } else if (!uidl.getBooleanAttribute("state")) {
            // Enable the button only if an upload is not in progress
            getWidget().enableUpload();
            getWidget().ensureTargetFrame();
        }
    }

    @Override
    public VUpload getWidget() {
        return (VUpload) super.getWidget();
    }
}
