package com.vaadin.tests.widgetset.client.dd;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.VUI;
import com.vaadin.client.ui.dd.VDragAndDropManager;
import com.vaadin.client.ui.ui.UIConnector;
import com.vaadin.shared.ui.Connect;

@Connect(com.vaadin.tests.dd.SpacebarPanner.class)
public class SpacebarPannerConnector extends AbstractExtensionConnector {

    Logger logger = Logger.getLogger(SpacebarPannerConnector.class
            .getSimpleName());

    private boolean trigger = false;
    private VUI vui;
    private boolean first = true;

    @Override
    protected void extend(ServerConnector target) {
        UIConnector uic = (UIConnector) target;
        vui = uic.getWidget();
        Event.sinkEvents(vui.getElement(), Event.MOUSEEVENTS | Event.KEYEVENTS);
        Event.addNativePreviewHandler(createNativePreviewHandler());
    }

    @OnStateChange("enabled")
    private void trigger() {
        if (first) {
            // ignore initial state change
            first = false;
        } else {
            trigger = true;
            vui.addStyleName("triggered");
        }
    }

    private NativePreviewHandler createNativePreviewHandler() {
        return new NativePreviewHandler() {

            private boolean spacebarDown = false;
            private boolean shouldPan = false;
            private boolean mouseDown = false;

            private int lastMouseX;
            private int lastMouseY;

            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event) {
                NativeEvent ne = event.getNativeEvent();
                int type = event.getTypeInt();

                switch (type) {
                case Event.ONKEYDOWN:
                    if (ne.getKeyCode() == KeyCodes.KEY_SPACE) {
                        event.cancel();
                        ne.preventDefault();
                        ne.stopPropagation();
                        spacebarDown = true;
                        break;
                    }
                case Event.ONKEYUP:
                    if (ne.getKeyCode() == KeyCodes.KEY_SPACE) {
                        spacebarDown = false;
                    }
                    break;
                case Event.ONMOUSEDOWN:
                    logger.log(Level.INFO, "Drag started");
                    lastMouseX = ne.getClientX();
                    lastMouseY = ne.getClientY();

                    shouldPan = spacebarDown || trigger;
                    mouseDown = true;
                    break;

                case Event.ONMOUSEUP:
                    shouldPan = false || trigger;
                    mouseDown = false;
                    break;

                case Event.ONMOUSEMOVE:
                    if (mouseDown && shouldPan) {
                        logger.log(Level.INFO, "In mousemove: mouseDown:"
                                + mouseDown + ", shouldPan: " + shouldPan);
                        trigger = false;
                        vui.removeStyleName("triggered");

                        logger.log(Level.INFO, "Panning!");
                        int currentClientX = ne.getClientX();
                        int currentClientY = ne.getClientY();

                        int deltaX = lastMouseX - currentClientX;
                        int deltaY = lastMouseY - currentClientY;
                        lastMouseX = currentClientX;
                        lastMouseY = currentClientY;

                        // this causes #17163
                        VDragAndDropManager.get().interruptDrag();

                        Element uiElement = vui.getElement();
                        int top = uiElement.getScrollTop();
                        int left = uiElement.getScrollLeft();

                        uiElement.setScrollTop(top + deltaY);
                        uiElement.setScrollLeft(left + deltaX);
                    }

                default:
                    break;
                }
            }
        };
    }
}
