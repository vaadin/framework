package com.vaadin.shared.ui.dnd;

import java.util.LinkedHashMap;
import java.util.Map;

import com.vaadin.shared.communication.SharedState;

public class DragSourceState extends SharedState {
    public String effectAllowed;

    public Map<String, String> data = new LinkedHashMap<>();
}
