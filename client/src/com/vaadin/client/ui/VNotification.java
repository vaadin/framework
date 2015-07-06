/*
 * Copyright 2000-2014 Vaadin Ltd.
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
import java.util.EventObject;
import java.util.Iterator;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.AnimationUtil;
import com.vaadin.client.AnimationUtil.AnimationEndListener;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.aria.AriaHelper;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.ui.NotificationRole;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.shared.ui.ui.UIState.NotificationTypeConfiguration;

public class VNotification extends VOverlay {

    public static final Position CENTERED = Position.MIDDLE_CENTER;
    public static final Position CENTERED_TOP = Position.TOP_CENTER;
    public static final Position CENTERED_BOTTOM = Position.BOTTOM_CENTER;
    public static final Position TOP_LEFT = Position.TOP_LEFT;
    public static final Position TOP_RIGHT = Position.TOP_RIGHT;
    public static final Position BOTTOM_LEFT = Position.BOTTOM_LEFT;
    public static final Position BOTTOM_RIGHT = Position.BOTTOM_RIGHT;

    private static final String STYLENAME_POSITION_TOP = "v-position-top";
    private static final String STYLENAME_POSITION_RIGHT = "v-position-right";
    private static final String STYLENAME_POSITION_BOTTOM = "v-position-bottom";
    private static final String STYLENAME_POSITION_LEFT = "v-position-left";
    private static final String STYLENAME_POSITION_MIDDLE = "v-position-middle";
    private static final String STYLENAME_POSITION_CENTER = "v-position-center";
    private static final String STYLENAME_POSITION_ASSISTIVE = "v-position-assistive";

    public static final String CAPTION = "caption";
    public static final String DESCRIPTION = "description";
    public static final String DETAILS = "details";

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

    private static final ArrayList<VNotification> notifications = new ArrayList<VNotification>();

    private boolean infiniteDelay = false;
    private int hideDelay = 0;

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
        getElement().getStyle().setZIndex(Z_INDEX_BASE);
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
        setDelay(delayMsec);

        if (BrowserInfo.get().isTouchDevice()) {
            new Timer() {
                @Override
                public void run() {
                    if (isAttached()) {
                        hide();
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
        AnimationUtil.setAnimationDuration(getElement(), fadeMsec + "ms");
        getElement().getStyle().setOpacity(startOpacity / 100);
    }

    private void setDelay(int delayMsec) {
        if (delayMsec < 0) {
            infiniteDelay = true;
            hideDelay = 0;
        } else {
            infiniteDelay = false;
            hideDelay = delayMsec;
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
        return styleSetup != null && styleSetup.postfix != null
                && !styleSetup.postfix.isEmpty();
    }

    private boolean hasPrefix(NotificationTypeConfiguration styleSetup) {
        return styleSetup != null && styleSetup.prefix != null
                && !styleSetup.prefix.isEmpty();
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
        if (getApplicationConnection() == null
                || getApplicationConnection().getUIConnector() == null) {
            return null;
        }

        return getApplicationConnection().getUIConnector().getState().notificationConfigurations
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
        if (temporaryStyle != null) {
            removeStyleName(temporaryStyle);
            removeStyleDependentName(temporaryStyle);
            temporaryStyle = null;
        }
        if (style != null && style.length() > 0) {
            temporaryStyle = style;
            addStyleName(style);
            addStyleDependentName(style);
        }

        setPosition(position);
        super.show();
        updatePositionOffsets(position);
        notifications.add(this);
        positionOrSizeUpdated();
        /**
         * Android 4 fails to render notifications correctly without a little
         * nudge (#8551) Chrome 41 now requires this too (#17252)
         */
        if (BrowserInfo.get().isAndroid() || isChrome41OrHigher()) {
            WidgetUtil.setStyleTemporarily(getElement(), "display", "none");
        }
    }

    private boolean isChrome41OrHigher() {
        return BrowserInfo.get().isChrome()
                && BrowserInfo.get().getBrowserMajorVersion() >= 41;
    }

    protected void hideAfterDelay() {
        if (delay == null) {
            delay = new Timer() {
                @Override
                public void run() {
                    VNotification.super.hide();
                }
            };
            delay.schedule(hideDelay);
        }
    }

    @Override
    public void hide() {
        if (delay != null) {
            delay.cancel();
        }
        // Run only once
        if (notifications.contains(this)) {
            DOM.removeEventPreview(this);

            // Still animating in, wait for it to finish before touching
            // the animation delay (which would restart the animation-in
            // in some browsers)
            if (getStyleName().contains(
                    VOverlay.ADDITIONAL_CLASSNAME_ANIMATE_IN)) {
                AnimationUtil.addAnimationEndListener(getElement(),
                        new AnimationEndListener() {
                            @Override
                            public void onAnimationEnd(NativeEvent event) {
                                if (AnimationUtil
                                        .getAnimationName(event)
                                        .contains(
                                                VOverlay.ADDITIONAL_CLASSNAME_ANIMATE_IN)) {
                                    VNotification.this.hide();
                                }
                            }
                        });
            } else {
                VNotification.super.hide();
                fireEvent(new HideEvent(this));
                notifications.remove(this);
            }
        }
    }

    private void updatePositionOffsets(com.vaadin.shared.Position position) {
        final Element el = getElement();

        // Remove all offsets (GWT PopupPanel defaults)
        el.getStyle().clearTop();
        el.getStyle().clearLeft();

        switch (position) {
        case MIDDLE_LEFT:
        case MIDDLE_RIGHT:
            center();
            el.getStyle().clearLeft();
            break;
        case TOP_CENTER:
        case BOTTOM_CENTER:
            center();
            el.getStyle().clearTop();
            break;
        case MIDDLE_CENTER:
            center();
            break;
        }
    }

    public void setPosition(com.vaadin.shared.Position position) {
        final Element el = getElement();

        // Remove any previous positions
        el.removeClassName(STYLENAME_POSITION_TOP);
        el.removeClassName(STYLENAME_POSITION_RIGHT);
        el.removeClassName(STYLENAME_POSITION_BOTTOM);
        el.removeClassName(STYLENAME_POSITION_LEFT);
        el.removeClassName(STYLENAME_POSITION_MIDDLE);
        el.removeClassName(STYLENAME_POSITION_CENTER);
        el.removeClassName(STYLENAME_POSITION_ASSISTIVE);

        switch (position) {
        case TOP_LEFT:
            el.addClassName(STYLENAME_POSITION_TOP);
            el.addClassName(STYLENAME_POSITION_LEFT);
            break;
        case TOP_RIGHT:
            el.addClassName(STYLENAME_POSITION_TOP);
            el.addClassName(STYLENAME_POSITION_RIGHT);
            break;
        case MIDDLE_LEFT:
            el.addClassName(STYLENAME_POSITION_MIDDLE);
            el.addClassName(STYLENAME_POSITION_LEFT);
            break;
        case MIDDLE_RIGHT:
            el.addClassName(STYLENAME_POSITION_MIDDLE);
            el.addClassName(STYLENAME_POSITION_RIGHT);
            break;
        case BOTTOM_RIGHT:
            el.addClassName(STYLENAME_POSITION_BOTTOM);
            el.addClassName(STYLENAME_POSITION_RIGHT);
            break;
        case BOTTOM_LEFT:
            el.addClassName(STYLENAME_POSITION_BOTTOM);
            el.addClassName(STYLENAME_POSITION_LEFT);
            break;
        case TOP_CENTER:
            el.addClassName(STYLENAME_POSITION_TOP);
            el.addClassName(STYLENAME_POSITION_CENTER);
            break;
        case BOTTOM_CENTER:
            el.addClassName(STYLENAME_POSITION_BOTTOM);
            el.addClassName(STYLENAME_POSITION_CENTER);
            break;
        case ASSISTIVE:
            el.addClassName(STYLENAME_POSITION_ASSISTIVE);
            break;
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        hide();
    }

    @Override
    /*
     * Fix for #14689: {@link #onEventPreview(Event)} method is deprecated and
     * it's called now only for the very first handler (see super impl). We need
     * it to work for any handler. So let's call old {@link
     * #onEventPreview(Event)} method explicitly with updated logic for {@link
     * #onPreviewNativeEvent(Event)}.
     */
    protected void onPreviewNativeEvent(NativePreviewEvent event) {
        if (!onEventPreview(Event.as(event.getNativeEvent()))) {
            event.cancel();
        }
    }

    @Override
    public boolean onEventPreview(Event event) {
        int type = DOM.eventGetType(event);
        // "modal"
        if (infiniteDelay || temporaryStyle == STYLE_SYSTEM) {
            if (type == Event.ONCLICK || type == Event.ONTOUCHEND) {
                if (DOM.isOrHasChild(getElement(), DOM.eventGetTarget(event))) {
                    hide();
                    return false;
                }
            } else if (type == Event.ONKEYDOWN
                    && event.getKeyCode() == KeyCodes.KEY_ESCAPE) {
                hide();
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
                hideAfterDelay();
            }
            break;
        case Event.ONMOUSEDOWN:
        case Event.ONMOUSEWHEEL:
        case Event.ONSCROLL:
            hideAfterDelay();
            break;
        case Event.ONKEYDOWN:
            if (event.getRepeat()) {
                return true;
            }
            hideAfterDelay();
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
                caption = WidgetUtil.escapeHTML(caption);
                caption = caption.replaceAll("\\n", "<br />");
            }
            html += "<h1 class='" + getDependentStyle(client, CAPTION) + "'>"
                    + caption + "</h1>";
        }
        if (notification
                .hasAttribute(UIConstants.ATTRIBUTE_NOTIFICATION_MESSAGE)) {
            String message = notification
                    .getStringAttribute(UIConstants.ATTRIBUTE_NOTIFICATION_MESSAGE);
            if (onlyPlainText) {
                message = WidgetUtil.escapeHTML(message);
                message = message.replaceAll("\\n", "<br />");
            }
            html += "<p class='" + getDependentStyle(client, DESCRIPTION)
                    + "'>" + message + "</p>";
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

    /**
     * Meant for internal usage only.
     * 
     * @since 7.5.0
     * @param client
     *            application connection
     * @param style
     *            the dependent style name
     * @return the given dependent style name prefixed with current notification
     *         primary style
     */
    public static String getDependentStyle(ApplicationConnection client,
            String style) {
        VNotification notification = createNotification(-1, client
                .getUIConnector().getWidget());
        String styleName = notification.getStyleName();
        notification.addStyleDependentName(style);
        String extendedStyle = notification.getStyleName();
        return extendedStyle.substring(styleName.length()).trim();
    }

    public static VNotification createNotification(int delayMsec, Widget owner) {
        final VNotification notification = GWT.create(VNotification.class);
        notification.setWaiAriaRole(null);
        notification.setDelay(delayMsec);

        if (!notification.infiniteDelay && BrowserInfo.get().isTouchDevice()) {
            new Timer() {
                @Override
                public void run() {
                    if (notification.isAttached()) {
                        notification.hide();
                    }
                }
            }.schedule(delayMsec + TOUCH_DEVICE_IDLE_DELAY);
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

    /**
     * Shows an error notification and redirects the user to the given URL when
     * she clicks on the notification.
     * 
     * If both message and caption are null, redirects the user to the url
     * immediately
     * 
     * @param connection
     *            A reference to the ApplicationConnection
     * @param caption
     *            The caption for the error or null to exclude the caption
     * @param message
     *            The message for the error or null to exclude the message
     * @param details
     *            A details message or null to exclude the details
     * @param url
     *            A url to redirect to after the user clicks the error
     *            notification
     */
    public static void showError(ApplicationConnection connection,
            String caption, String message, String details, String url) {

        StringBuilder html = new StringBuilder();
        if (caption != null) {
            html.append("<h1 class='");
            html.append(getDependentStyle(connection, CAPTION));
            html.append("'>");
            html.append(caption);
            html.append("</h1>");
        }
        if (message != null) {
            html.append("<p class='");
            html.append(getDependentStyle(connection, DESCRIPTION));
            html.append("'>");
            html.append(message);
            html.append("</p>");
        }

        if (html.length() > 0) {

            // Add error description
            if (details != null) {
                html.append("<p class='");
                html.append(getDependentStyle(connection, DETAILS));
                html.append("'>");
                html.append("<i style=\"font-size:0.7em\">");
                html.append(details);
                html.append("</i></p>");
            }

            VNotification n = VNotification.createNotification(1000 * 60 * 45,
                    connection.getUIConnector().getWidget());
            n.addEventListener(new NotificationRedirect(url));
            n.show(html.toString(), VNotification.CENTERED_TOP,
                    VNotification.STYLE_SYSTEM);
        } else {
            ApplicationConnection.redirect(url);
        }
    }

    /**
     * Listens for Notification hide event, and redirects. Used for system
     * messages, such as session expired.
     * 
     */
    private static class NotificationRedirect implements
            VNotification.EventListener {
        String url;

        NotificationRedirect(String url) {
            this.url = url;
        }

        @Override
        public void notificationHidden(HideEvent event) {
            ApplicationConnection.redirect(url);
        }

    }

}
