/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.Iterator;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.ui.aria.AriaHelper;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.shared.ui.ui.UIState.NotificationTypeConfiguration;
import com.vaadin.shared.ui.ui.NotificationRole;

public class VNotification extends VOverlay {

    public static final Position CENTERED = Position.MIDDLE_CENTER;
    public static final Position CENTERED_TOP = Position.TOP_CENTER;
    public static final Position CENTERED_BOTTOM = Position.BOTTOM_CENTER;
    public static final Position TOP_LEFT = Position.TOP_LEFT;
    public static final Position TOP_RIGHT = Position.TOP_RIGHT;
    public static final Position BOTTOM_LEFT = Position.BOTTOM_LEFT;
    public static final Position BOTTOM_RIGHT = Position.BOTTOM_RIGHT;

    /**
     * Position that is only accessible for assistive devices, invisible for
     * visual users.
     */
    public static final Position ASSISTIVE = Position.ASSISTIVE;

    public static final int DELAY_FOREVER = -1;
    public static final int DELAY_NONE = 0;

    private static final String STYLENAME = "v-Notification";
    private static final int mouseMoveThreshold = 7;
    private static final int Z_INDEX_BASE = 20000;
    public static final String STYLE_SYSTEM = "system";
    private static final int FADE_ANIMATION_INTERVAL = 50; // == 20 fps

    private static final ArrayList<VNotification> notifications = new ArrayList<VNotification>();

    private int startOpacity = 90;
    private int fadeMsec = 400;
    private int delayMsec = 1000;

    private Timer fader;
    private Timer delay;

    private int x = -1;
    private int y = -1;

    private String temporaryStyle;

    private ArrayList<EventListener> listeners;
    private static final int TOUCH_DEVICE_IDLE_DELAY = 1000;

    /**
     * Default constructor. You should use GWT.create instead.
     */
    public VNotification() {
        setStyleName(STYLENAME);
        sinkEvents(Event.ONCLICK);
        DOM.setStyleAttribute(getElement(), "zIndex", "" + Z_INDEX_BASE);
    }

    /**
     * @deprecated Use static {@link #createNotification(int)} instead to enable
     *             GWT deferred binding.
     * 
     * @param delayMsec
     */
    @Deprecated
    public VNotification(int delayMsec) {
        this();
        this.delayMsec = delayMsec;
        if (BrowserInfo.get().isTouchDevice()) {
            new Timer() {
                @Override
                public void run() {
                    if (isAttached()) {
                        fade();
                    }
                }
            }.schedule(delayMsec + TOUCH_DEVICE_IDLE_DELAY);
        }
    }

    /**
     * @deprecated Use static {@link #createNotification(int, int, int)} instead
     *             to enable GWT deferred binding.
     * 
     * @param delayMsec
     * @param fadeMsec
     * @param startOpacity
     */
    @Deprecated
    public VNotification(int delayMsec, int fadeMsec, int startOpacity) {
        this(delayMsec);
        this.fadeMsec = fadeMsec;
        this.startOpacity = startOpacity;
    }

    public void startDelay() {
        DOM.removeEventPreview(this);
        if (delayMsec > 0) {
            if (delay == null) {
                delay = new Timer() {
                    @Override
                    public void run() {
                        fade();
                    }
                };
                delay.schedule(delayMsec);
            }
        } else if (delayMsec == 0) {
            fade();
        }
    }

    @Override
    public void show() {
        show(CENTERED);
    }

    public void show(String style) {
        show(CENTERED, style);
    }

    public void show(com.vaadin.shared.Position position) {
        show(position, null);
    }

    public void show(Widget widget, Position position, String style) {
        NotificationTypeConfiguration styleSetup = getUiState(style);
        setWaiAriaRole(styleSetup);

        FlowPanel panel = new FlowPanel();
        if (hasPrefix(styleSetup)) {
            panel.add(new Label(styleSetup.prefix));
            AriaHelper.setVisibleForAssistiveDevicesOnly(panel.getElement(),
                    true);
        }

        panel.add(widget);

        if (hasPostfix(styleSetup)) {
            panel.add(new Label(styleSetup.postfix));
            AriaHelper.setVisibleForAssistiveDevicesOnly(panel.getElement(),
                    true);
        }
        setWidget(panel);
        show(position, style);
    }

    private boolean hasPostfix(NotificationTypeConfiguration styleSetup) {
        return styleSetup != null && styleSetup.postfix != null && !styleSetup.postfix.isEmpty();
    }

    private boolean hasPrefix(NotificationTypeConfiguration styleSetup) {
        return styleSetup != null && styleSetup.prefix != null && !styleSetup.prefix.isEmpty();
    }

    public void show(String html, Position position, String style) {
        NotificationTypeConfiguration styleSetup = getUiState(style);
        String assistiveDeviceOnlyStyle = AriaHelper.ASSISTIVE_DEVICE_ONLY_STYLE;

        setWaiAriaRole(styleSetup);

        String type = "";
        String usage = "";

        if (hasPrefix(styleSetup)) {
            type = "<span class='" + assistiveDeviceOnlyStyle + "'>"
                    + styleSetup.prefix + "</span>";
        }

        if (hasPostfix(styleSetup)) {
            usage = "<span class='" + assistiveDeviceOnlyStyle + "'>"
                    + styleSetup.postfix + "</span>";
        }

        setWidget(new HTML(type + html + usage));
        show(position, style);
    }

    private NotificationTypeConfiguration getUiState(String style) {
        return getApplicationConnection()
                .getUIConnector().getState().notificationConfigurations
                .get(style);
    }

    private void setWaiAriaRole(NotificationTypeConfiguration styleSetup) {
        Roles.getAlertRole().set(getElement());

        if (styleSetup != null && styleSetup.notificationRole != null) {
            if (NotificationRole.STATUS == styleSetup.notificationRole) {
                Roles.getStatusRole().set(getElement());
            }
        }
    }

    public void show(Position position, String style) {
        setOpacity(getElement(), startOpacity);
        if (style != null) {
            temporaryStyle = style;
            addStyleName(style);
            addStyleDependentName(style);
        }
        super.show();
        notifications.add(this);
        setPosition(position);
        positionOrSizeUpdated();
        /**
         * Android 4 fails to render notifications correctly without a little
         * nudge (#8551)
         */
        if (BrowserInfo.get().isAndroid()) {
            Util.setStyleTemporarily(getElement(), "display", "none");
        }
    }

    @Override
    public void hide() {
        DOM.removeEventPreview(this);
        cancelDelay();
        cancelFade();
        if (temporaryStyle != null) {
            removeStyleName(temporaryStyle);
            removeStyleDependentName(temporaryStyle);
            temporaryStyle = null;
        }
        super.hide();
        notifications.remove(this);
        fireEvent(new HideEvent(this));
    }

    public void fade() {
        DOM.removeEventPreview(this);
        cancelDelay();
        if (fader == null) {
            fader = new Timer() {
                private final long start = new Date().getTime();

                @Override
                public void run() {
                    /*
                     * To make animation smooth, don't count that event happens
                     * on time. Reduce opacity according to the actual time
                     * spent instead of fixed decrement.
                     */
                    long now = new Date().getTime();
                    long timeEplaced = now - start;
                    float remainingFraction = 1 - timeEplaced
                            / (float) fadeMsec;
                    int opacity = (int) (startOpacity * remainingFraction);
                    if (opacity <= 0) {
                        cancel();
                        hide();
                    } else {
                        setOpacity(getElement(), opacity);
                    }
                }
            };
            fader.scheduleRepeating(FADE_ANIMATION_INTERVAL);
        }
    }

    public void setPosition(com.vaadin.shared.Position position) {
        final Element el = getElement();
        DOM.setStyleAttribute(el, "top", "");
        DOM.setStyleAttribute(el, "left", "");
        DOM.setStyleAttribute(el, "bottom", "");
        DOM.setStyleAttribute(el, "right", "");
        switch (position) {
        case TOP_LEFT:
            DOM.setStyleAttribute(el, "top", "0px");
            DOM.setStyleAttribute(el, "left", "0px");
            break;
        case TOP_RIGHT:
            DOM.setStyleAttribute(el, "top", "0px");
            DOM.setStyleAttribute(el, "right", "0px");
            break;
        case MIDDLE_LEFT:
            center();
            DOM.setStyleAttribute(el, "left", "0px");
            break;
        case MIDDLE_RIGHT:
            center();
            DOM.setStyleAttribute(el, "left", "");
            DOM.setStyleAttribute(el, "right", "0px");
            break;
        case BOTTOM_RIGHT:
            DOM.setStyleAttribute(el, "position", "absolute");
            DOM.setStyleAttribute(el, "bottom", "0px");
            DOM.setStyleAttribute(el, "right", "0px");
            break;
        case BOTTOM_LEFT:
            DOM.setStyleAttribute(el, "bottom", "0px");
            DOM.setStyleAttribute(el, "left", "0px");
            break;
        case TOP_CENTER:
            center();
            DOM.setStyleAttribute(el, "top", "0px");
            break;
        case BOTTOM_CENTER:
            center();
            DOM.setStyleAttribute(el, "top", "");
            DOM.setStyleAttribute(el, "bottom", "0px");
            break;
        case ASSISTIVE:
            DOM.setStyleAttribute(el, "top", "-2000px");
            DOM.setStyleAttribute(el, "left", "-2000px");
            break;
        default:
        case MIDDLE_CENTER:
            center();
            break;
        }
    }

    private void cancelFade() {
        if (fader != null) {
            fader.cancel();
            fader = null;
        }
    }

    private void cancelDelay() {
        if (delay != null) {
            delay.cancel();
            delay = null;
        }
    }

    private void setOpacity(Element el, int opacity) {
        DOM.setStyleAttribute(el, "opacity", "" + (opacity / 100.0));
        if (BrowserInfo.get().isIE()) {
            DOM.setStyleAttribute(el, "filter", "Alpha(opacity=" + opacity
                    + ")");
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        DOM.removeEventPreview(this);
        if (fader == null) {
            fade();
        }
    }

    @Override
    public boolean onEventPreview(Event event) {
        int type = DOM.eventGetType(event);
        // "modal"
        if (delayMsec == -1 || temporaryStyle == STYLE_SYSTEM) {
            if (type == Event.ONCLICK) {
                if (DOM.isOrHasChild(getElement(), DOM.eventGetTarget(event))) {
                    fade();
                    return false;
                }
            } else if (type == Event.ONKEYDOWN
                    && event.getKeyCode() == KeyCodes.KEY_ESCAPE) {
                fade();
                return false;
            }
            if (temporaryStyle == STYLE_SYSTEM) {
                return true;
            } else {
                return false;
            }
        }
        // default
        switch (type) {
        case Event.ONMOUSEMOVE:

            if (x < 0) {
                x = DOM.eventGetClientX(event);
                y = DOM.eventGetClientY(event);
            } else if (Math.abs(DOM.eventGetClientX(event) - x) > mouseMoveThreshold
                    || Math.abs(DOM.eventGetClientY(event) - y) > mouseMoveThreshold) {
                startDelay();
            }
            break;
        case Event.ONMOUSEDOWN:
        case Event.ONMOUSEWHEEL:
        case Event.ONSCROLL:
            startDelay();
            break;
        case Event.ONKEYDOWN:
            if (event.getRepeat()) {
                return true;
            }
            startDelay();
            break;
        default:
            break;
        }
        return true;
    }

    public void addEventListener(EventListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<EventListener>();
        }
        listeners.add(listener);
    }

    public void removeEventListener(EventListener listener) {
        if (listeners == null) {
            return;
        }
        listeners.remove(listener);
    }

    private void fireEvent(HideEvent event) {
        if (listeners != null) {
            for (Iterator<EventListener> it = listeners.iterator(); it
                    .hasNext();) {
                EventListener l = it.next();
                l.notificationHidden(event);
            }
        }
    }

    public static void showNotification(ApplicationConnection client,
            final UIDL notification) {
        boolean onlyPlainText = notification
                .hasAttribute(UIConstants.NOTIFICATION_HTML_CONTENT_NOT_ALLOWED);
        String html = "";
        if (notification.hasAttribute(UIConstants.ATTRIBUTE_NOTIFICATION_ICON)) {
            String iconUri = notification
                    .getStringAttribute(UIConstants.ATTRIBUTE_NOTIFICATION_ICON);
            html += client.getIcon(iconUri).getElement().getString();
        }
        if (notification
                .hasAttribute(UIConstants.ATTRIBUTE_NOTIFICATION_CAPTION)) {
            String caption = notification
                    .getStringAttribute(UIConstants.ATTRIBUTE_NOTIFICATION_CAPTION);
            if (onlyPlainText) {
                caption = Util.escapeHTML(caption);
                caption = caption.replaceAll("\\n", "<br />");
            }
            html += "<h1>" + caption + "</h1>";
        }
        if (notification
                .hasAttribute(UIConstants.ATTRIBUTE_NOTIFICATION_MESSAGE)) {
            String message = notification
                    .getStringAttribute(UIConstants.ATTRIBUTE_NOTIFICATION_MESSAGE);
            if (onlyPlainText) {
                message = Util.escapeHTML(message);
                message = message.replaceAll("\\n", "<br />");
            }
            html += "<p>" + message + "</p>";
        }

        final String style = notification
                .hasAttribute(UIConstants.ATTRIBUTE_NOTIFICATION_STYLE) ? notification
                .getStringAttribute(UIConstants.ATTRIBUTE_NOTIFICATION_STYLE)
                : null;

        final int pos = notification
                .getIntAttribute(UIConstants.ATTRIBUTE_NOTIFICATION_POSITION);
        Position position = Position.values()[pos];

        final int delay = notification
                .getIntAttribute(UIConstants.ATTRIBUTE_NOTIFICATION_DELAY);
        createNotification(delay, client.getUIConnector().getWidget()).show(
                html, position, style);
    }

    public static VNotification createNotification(int delayMsec, Widget owner) {
        final VNotification notification = GWT.create(VNotification.class);
        notification.setWaiAriaRole(null);

        notification.delayMsec = delayMsec;
        if (BrowserInfo.get().isTouchDevice()) {
            new Timer() {
                @Override
                public void run() {
                    if (notification.isAttached()) {
                        notification.fade();
                    }
                }
            }.schedule(notification.delayMsec + TOUCH_DEVICE_IDLE_DELAY);
        }
        notification.setOwner(owner);
        return notification;
    }

    public class HideEvent extends EventObject {

        public HideEvent(Object source) {
            super(source);
        }
    }

    public interface EventListener extends java.util.EventListener {
        public void notificationHidden(HideEvent event);
    }

    /**
     * Moves currently visible notifications to the top of the event preview
     * stack. Can be called when opening other overlays such as subwindows to
     * ensure the notifications receive the events they need and don't linger
     * indefinitely. See #7136.
     * 
     * TODO Should this be a generic Overlay feature instead?
     */
    public static void bringNotificationsToFront() {
        for (VNotification notification : notifications) {
            DOM.removeEventPreview(notification);
            DOM.addEventPreview(notification);
        }
    }
}
