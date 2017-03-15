package com.vaadin.shared.ui.grid;

import java.util.List;
import java.util.Map;

import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.dnd.DropEffect;

public interface GridDropTargetExtensionRpc extends ServerRpc {
    public void drop(List<String> types, Map<String, String> data,
            DropEffect dropEffect, String dragSourceId, String rowKey);
}
