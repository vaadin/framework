package com.vaadin.client.ui.popupview;

import com.google.gwt.event.shared.EventHandler;

public interface VisibilityChangeHandler extends EventHandler {

    void onVisibilityChange(VisibilityChangeEvent event);
}
