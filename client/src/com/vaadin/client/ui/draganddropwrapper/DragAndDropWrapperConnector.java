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
package com.vaadin.client.ui.draganddropwrapper;

import java.util.HashMap;
import java.util.Set;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.VDragAndDropWrapper;
import com.vaadin.client.ui.customcomponent.CustomComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.draganddropwrapper.DragAndDropWrapperConstants;
import com.vaadin.ui.DragAndDropWrapper;

@Connect(DragAndDropWrapper.class)
public class DragAndDropWrapperConnector extends CustomComponentConnector
        implements Paintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().client = client;
        if (isRealUpdate(uidl) && !uidl.hasAttribute("hidden")) {
            UIDL acceptCrit = uidl.getChildByTagName("-ac");
            if (acceptCrit == null) {
                getWidget().dropHandler = null;
            } else {
                if (getWidget().dropHandler == null) {
                    getWidget().dropHandler = getWidget().new CustomDropHandler();
                }
                getWidget().dropHandler.updateAcceptRules(acceptCrit);
            }

            Set<String> variableNames = uidl.getVariableNames();
            for (String fileId : variableNames) {
                if (fileId.startsWith("rec-")) {
                    String receiverUrl = uidl.getStringVariable(fileId);
                    fileId = fileId.substring(4);
                    if (getWidget().fileIdToReceiver == null) {
                        getWidget().fileIdToReceiver = new HashMap<String, String>();
                    }
                    if ("".equals(receiverUrl)) {
                        Integer id = Integer.parseInt(fileId);
                        int indexOf = getWidget().fileIds.indexOf(id);
                        if (indexOf != -1) {
                            getWidget().files.remove(indexOf);
                            getWidget().fileIds.remove(indexOf);
                        }
                    } else {
                        getWidget().fileIdToReceiver.put(fileId, receiverUrl);
                    }
                }
            }
            getWidget().startNextUpload();

            getWidget().dragStartMode = uidl
                    .getIntAttribute(DragAndDropWrapperConstants.DRAG_START_MODE);
            getWidget().initDragStartMode();
            getWidget().html5DataFlavors = uidl
                    .getMapAttribute(DragAndDropWrapperConstants.HTML5_DATA_FLAVORS);

            // Used to prevent wrapper from stealing tooltips when not defined
            getWidget().hasTooltip = getState().description != null;
        }
    }

    @Override
    public VDragAndDropWrapper getWidget() {
        return (VDragAndDropWrapper) super.getWidget();
    }

}
