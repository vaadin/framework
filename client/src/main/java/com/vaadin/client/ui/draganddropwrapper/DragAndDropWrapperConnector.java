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
package com.vaadin.client.ui.draganddropwrapper;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.extensions.DragSourceExtensionConnector;
import com.vaadin.client.extensions.DropTargetExtensionConnector;
import com.vaadin.client.ui.VDragAndDropWrapper;
import com.vaadin.client.ui.customcomponent.CustomComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.draganddropwrapper.DragAndDropWrapperConstants;
import com.vaadin.shared.ui.draganddropwrapper.DragAndDropWrapperServerRpc;
import com.vaadin.ui.DragAndDropWrapper;

/**
 *
 * @author Vaadin Ltd
 * @deprecated Replaced in 8.1 with {@link DragSourceExtensionConnector} and
 *             {@link DropTargetExtensionConnector}.
 */
@Deprecated
@Connect(DragAndDropWrapper.class)
public class DragAndDropWrapperConnector extends CustomComponentConnector
        implements Paintable, VDragAndDropWrapper.UploadHandler {

    @Override
    protected void init() {
        super.init();
        getWidget().uploadHandler = this;
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        VDragAndDropWrapper widget = getWidget();
        widget.client = client;
        if (isRealUpdate(uidl) && !uidl.hasAttribute("hidden")) {
            UIDL acceptCrit = uidl.getChildByTagName("-ac");
            if (acceptCrit == null) {
                widget.dropHandler = null;
            } else {
                if (widget.dropHandler == null) {
                    widget.dropHandler = widget.new CustomDropHandler();
                }
                widget.dropHandler.updateAcceptRules(acceptCrit);
            }

            Set<String> variableNames = uidl.getVariableNames();
            for (String fileId : variableNames) {
                if (fileId.startsWith("rec-")) {
                    String receiverUrl = uidl.getStringVariable(fileId);
                    fileId = fileId.substring(4);
                    if (widget.fileIdToReceiver == null) {
                        widget.fileIdToReceiver = new HashMap<>();
                    }
                    if ("".equals(receiverUrl)) {
                        Integer id = Integer.parseInt(fileId);
                        int indexOf = widget.fileIds.indexOf(id);
                        if (indexOf != -1) {
                            widget.files.remove(indexOf);
                            widget.fileIds.remove(indexOf);
                        }
                    } else {
                        if (widget.fileIdToReceiver.containsKey(fileId)
                                && receiverUrl != null && !receiverUrl.equals(
                                        widget.fileIdToReceiver.get(fileId))) {
                            getLogger().severe(
                                    "Overwriting file receiver mapping for fileId "
                                            + fileId + " . Old receiver URL: "
                                            + widget.fileIdToReceiver
                                                    .get(fileId)
                                            + " New receiver URL: "
                                            + receiverUrl);
                        }
                        widget.fileIdToReceiver.put(fileId, receiverUrl);
                    }
                }
            }
            widget.startNextUpload();

            widget.dragStartMode = uidl.getIntAttribute(
                    DragAndDropWrapperConstants.DRAG_START_MODE);

            String dragImageComponentConnectorId = uidl.getStringAttribute(
                    DragAndDropWrapperConstants.DRAG_START_COMPONENT_ATTRIBUTE);

            ComponentConnector connector = null;
            if (dragImageComponentConnectorId != null) {
                connector = (ComponentConnector) ConnectorMap.get(client)
                        .getConnector(dragImageComponentConnectorId);

                if (connector == null) {
                    getLogger().log(Level.WARNING,
                            "DragAndDropWrapper drag image component"
                                    + " connector now found. Make sure the"
                                    + " component is attached.");
                } else {
                    widget.setDragAndDropWidget(connector.getWidget());
                }
            }
            widget.initDragStartMode();
            widget.html5DataFlavors = uidl.getMapAttribute(
                    DragAndDropWrapperConstants.HTML5_DATA_FLAVORS);

            // Used to prevent wrapper from stealing tooltips when not defined
            widget.hasTooltip = getState().description != null;
        }
    }

    @Override
    public VDragAndDropWrapper getWidget() {
        return (VDragAndDropWrapper) super.getWidget();
    }

    private static Logger getLogger() {
        return Logger.getLogger(DragAndDropWrapperConnector.class.getName());
    }

    @Override
    public void uploadDone() {
        // #19616 RPC to poll the server for changes
        getRpcProxy(DragAndDropWrapperServerRpc.class).poll();
    }

}
