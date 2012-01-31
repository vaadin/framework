package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public class VCustomComponentPaintable extends
        VAbstractPaintableWidgetContainer {

    public void updateFromUIDL(UIDL uidl, final ApplicationConnection client) {
        getWidgetForPaintable().rendering = true;
        if (client.updateComponent(this, uidl, true)) {
            getWidgetForPaintable().rendering = false;
            return;
        }
        getWidgetForPaintable().client = client;

        final UIDL child = uidl.getChildUIDL(0);
        if (child != null) {
            final VPaintableWidget paintable = client.getPaintable(child);
            Widget widget = paintable.getWidgetForPaintable();
            if (widget != getWidgetForPaintable().getWidget()) {
                if (getWidgetForPaintable().getWidget() != null) {
                    client.unregisterPaintable(VPaintableMap.get(client)
                            .getPaintable(getWidgetForPaintable().getWidget()));
                    getWidgetForPaintable().clear();
                }
                getWidgetForPaintable().setWidget(widget);
            }
            paintable.updateFromUIDL(child, client);
        }

        boolean updateDynamicSize = getWidgetForPaintable().updateDynamicSize();
        if (updateDynamicSize) {
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    // FIXME deferred relative size update needed to fix some
                    // scrollbar issues in sampler. This must be the wrong way
                    // to do it. Might be that some other component is broken.
                    client.handleComponentRelativeSize(getWidgetForPaintable());

                }
            });
        }

        getWidgetForPaintable().renderSpace.setWidth(getWidgetForPaintable()
                .getElement().getOffsetWidth());
        getWidgetForPaintable().renderSpace.setHeight(getWidgetForPaintable()
                .getElement().getOffsetHeight());

        /*
         * Needed to update client size if the size of this component has
         * changed and the child uses relative size(s).
         */
        client.runDescendentsLayout(getWidgetForPaintable());

        getWidgetForPaintable().rendering = false;
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VCustomComponent.class);
    }

    @Override
    public VCustomComponent getWidgetForPaintable() {
        return (VCustomComponent) super.getWidgetForPaintable();
    }

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        // NOP, custom component dont render composition roots caption
    }

}
