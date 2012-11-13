package com.vaadin.client.ui.popupview;

import com.google.gwt.event.shared.GwtEvent;

public class VisibilityChangeEvent extends
        GwtEvent<VisibilityChangeEventHandler> {

    private static Type<VisibilityChangeEventHandler> TYPE;

    private Boolean visible;

    public VisibilityChangeEvent(final Boolean visible) {
        this.visible = visible;
    }

    public Boolean getValue() {
        return visible;
    }

    @Override
    public Type<VisibilityChangeEventHandler> getAssociatedType() {
        return getType();
    }

    public static Type<VisibilityChangeEventHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<VisibilityChangeEventHandler>();
        }
        return TYPE;
    }

    @Override
    protected void dispatch(final VisibilityChangeEventHandler handler) {
        handler.onVisibilityChangeEvent(this);
    }
}
