package com.vaadin.shared.ui.dnd;

import java.util.Map;

import com.vaadin.shared.communication.ServerRpc;

public interface DropTargetRpc extends ServerRpc {
    public void drop(Map<String, String> data, String dropEffect);
}
