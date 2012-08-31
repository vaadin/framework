package com.vaadin.shared.ui.flash;

import java.util.Map;

import com.vaadin.shared.ui.AbstractEmbeddedState;

public class FlashState extends AbstractEmbeddedState {

    public String classId;

    public String codebase;

    public String codetype;

    public String archive;

    public String standby;

    public Map<String, String> embedParams;
}
