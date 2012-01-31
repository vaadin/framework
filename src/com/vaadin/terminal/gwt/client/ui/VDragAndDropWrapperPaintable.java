package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class VDragAndDropWrapperPaintable extends VCustomComponentPaintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().client = client;
        super.updateFromUIDL(uidl, client);
        if (!uidl.hasAttribute("cached") && !uidl.hasAttribute("hidden")) {
            UIDL acceptCrit = uidl.getChildByTagName("-ac");
            if (acceptCrit == null) {
                getWidgetForPaintable().dropHandler = null;
            } else {
                if (getWidgetForPaintable().dropHandler == null) {
                    getWidgetForPaintable().dropHandler = getWidgetForPaintable().new CustomDropHandler();
                }
                getWidgetForPaintable().dropHandler
                        .updateAcceptRules(acceptCrit);
            }

            Set<String> variableNames = uidl.getVariableNames();
            for (String fileId : variableNames) {
                if (fileId.startsWith("rec-")) {
                    String receiverUrl = uidl.getStringVariable(fileId);
                    fileId = fileId.substring(4);
                    if (getWidgetForPaintable().fileIdToReceiver == null) {
                        getWidgetForPaintable().fileIdToReceiver = new HashMap<String, String>();
                    }
                    if ("".equals(receiverUrl)) {
                        Integer id = Integer.parseInt(fileId);
                        int indexOf = getWidgetForPaintable().fileIds
                                .indexOf(id);
                        if (indexOf != -1) {
                            getWidgetForPaintable().files.remove(indexOf);
                            getWidgetForPaintable().fileIds.remove(indexOf);
                        }
                    } else {
                        getWidgetForPaintable().fileIdToReceiver.put(fileId,
                                receiverUrl);
                    }
                }
            }
            getWidgetForPaintable().startNextUpload();

            getWidgetForPaintable().dragStartMode = uidl
                    .getIntAttribute(VDragAndDropWrapper.DRAG_START_MODE);
            getWidgetForPaintable().initDragStartMode();
            getWidgetForPaintable().html5DataFlavors = uidl
                    .getMapAttribute(VDragAndDropWrapper.HTML5_DATA_FLAVORS);
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VDragAndDropWrapper.class);
    }

    @Override
    public VDragAndDropWrapper getWidgetForPaintable() {
        return (VDragAndDropWrapper) super.getWidgetForPaintable();
    }

}
