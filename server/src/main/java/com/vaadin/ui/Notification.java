/*
 * Copyright 2000-2018 Vaadin Ltd.
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

package com.vaadin.ui;

import java.lang.reflect.Method;

import com.vaadin.event.ConnectorEvent;
import com.vaadin.event.HasUserOriginated;
import com.vaadin.event.SerializableEventListener;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.shared.Position;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.notification.NotificationServerRpc;
import com.vaadin.shared.ui.notification.NotificationState;

/**
 * A notification message, used to display temporary messages to the user - for
 * example "Document saved", or "Save failed".
 * <p>
 * The notification message can consist of several parts: caption, description
 * and icon. It is usually used with only caption - one should be wary of
 * filling the notification with too much information.
 * </p>
 * <p>
 * The notification message tries to be as unobtrusive as possible, while still
 * drawing needed attention. There are several basic types of messages that can
 * be used in different situations:
 * <ul>
 * <li>TYPE_HUMANIZED_MESSAGE fades away quickly as soon as the user uses the
 * mouse or types something. It can be used to show fairly unimportant messages,
 * such as feedback that an operation succeeded ("Document Saved") - the kind of
 * messages the user ignores once the application is familiar.</li>
 * <li>TYPE_WARNING_MESSAGE is shown for a short while after the user uses the
 * mouse or types something. It's default style is also more noticeable than the
 * humanized message. It can be used for messages that do not contain a lot of
 * important information, but should be noticed by the user. Despite the name,
 * it does not have to be a warning, but can be used instead of the humanized
 * message whenever you want to make the message a little more noticeable.</li>
 * <li>TYPE_ERROR_MESSAGE requires to user to click it before disappearing, and
 * can be used for critical messages.</li>
 * <li>TYPE_TRAY_NOTIFICATION is shown for a while in the lower right corner of
 * the window, and can be used for "convenience notifications" that do not have
 * to be noticed immediately, and should not interfere with the current task -
 * for instance to show "You have a new message in your inbox" while the user is
 * working in some other area of the application.</li>
 * </ul>
 * </p>
 * <p>
 * In addition to the basic pre-configured types, a Notification can also be
 * configured to show up in a custom position, for a specified time (or until
 * clicked), and with a custom stylename. An icon can also be added.
 * </p>
 *
 */
public class Notification extends AbstractExtension {

    public enum Type {
        HUMANIZED_MESSAGE("humanized"), WARNING_MESSAGE(
                "warning"), ERROR_MESSAGE("error"), TRAY_NOTIFICATION("tray"),
        /**
         * @since 7.2
         */
        ASSISTIVE_NOTIFICATION("assistive");

        private final String style;

        Type(String style) {
            this.style = style;
        }

        /**
         * @since 7.2
         *
         * @return the style name for this notification type.
         */
        public String getStyle() {
            return style;
        }
    }

    @Deprecated
    public static final Type TYPE_HUMANIZED_MESSAGE = Type.HUMANIZED_MESSAGE;
    @Deprecated
    public static final Type TYPE_WARNING_MESSAGE = Type.WARNING_MESSAGE;
    @Deprecated
    public static final Type TYPE_ERROR_MESSAGE = Type.ERROR_MESSAGE;
    @Deprecated
    public static final Type TYPE_TRAY_NOTIFICATION = Type.TRAY_NOTIFICATION;

    @Deprecated
    public static final Position POSITION_CENTERED = Position.MIDDLE_CENTER;
    @Deprecated
    public static final Position POSITION_CENTERED_TOP = Position.TOP_CENTER;
    @Deprecated
    public static final Position POSITION_CENTERED_BOTTOM = Position.BOTTOM_CENTER;
    @Deprecated
    public static final Position POSITION_TOP_LEFT = Position.TOP_LEFT;
    @Deprecated
    public static final Position POSITION_TOP_RIGHT = Position.TOP_RIGHT;
    @Deprecated
    public static final Position POSITION_BOTTOM_LEFT = Position.BOTTOM_LEFT;
    @Deprecated
    public static final Position POSITION_BOTTOM_RIGHT = Position.BOTTOM_RIGHT;

    public static final int DELAY_FOREVER = -1;
    public static final int DELAY_NONE = 0;

    /**
     * The server RPC.
     *
     * @since 8.2
     */
    private NotificationServerRpc rpc = () -> {
        close(true);
    };

    /**
     * Creates a "humanized" notification message.
     *
     * The caption is rendered as plain text with HTML automatically escaped.
     *
     * @param caption
     *            The message to show
     */
    public Notification(String caption) {
        this(caption, null, TYPE_HUMANIZED_MESSAGE);
    }

    /**
     * Creates a notification message of the specified type.
     *
     * The caption is rendered as plain text with HTML automatically escaped.
     *
     * @param caption
     *            The message to show
     * @param type
     *            The type of message
     */
    public Notification(String caption, Type type) {
        this(caption, null, type);
    }

    /**
     * Creates a "humanized" notification message with a bigger caption and
     * smaller description.
     *
     * The caption and description are rendered as plain text with HTML
     * automatically escaped.
     *
     * @param caption
     *            The message caption
     * @param description
     *            The message description
     */
    public Notification(String caption, String description) {
        this(caption, description, TYPE_HUMANIZED_MESSAGE);
    }

    /**
     * Creates a notification message of the specified type, with a bigger
     * caption and smaller description.
     *
     * The caption and description are rendered as plain text with HTML
     * automatically escaped.
     *
     * @param caption
     *            The message caption
     * @param description
     *            The message description
     * @param type
     *            The type of message
     */
    public Notification(String caption, String description, Type type) {
        this(caption, description, type, false);
    }

    /**
     * Creates a notification message of the specified type, with a bigger
     * caption and smaller description.
     *
     * Care should be taken to to avoid XSS vulnerabilities if html is allowed.
     *
     * @param caption
     *            The message caption
     * @param description
     *            The message description
     * @param type
     *            The type of message
     * @param htmlContentAllowed
     *            Whether html in the caption and description should be
     *            displayed as html or as plain text
     */
    public Notification(String caption, String description, Type type,
            boolean htmlContentAllowed) {
        registerRpc(rpc);
        setCaption(caption);
        setDescription(description);
        setHtmlContentAllowed(htmlContentAllowed);
        setType(type);
    }

    private void setType(Type type) {
        setStyleName(type.getStyle());
        switch (type) {
        case WARNING_MESSAGE:
            setDelayMsec(1500);
            break;
        case ERROR_MESSAGE:
            setDelayMsec(DELAY_FOREVER);
            break;
        case TRAY_NOTIFICATION:
            setDelayMsec(3000);
            setPosition(Position.BOTTOM_RIGHT);
            break;
        case ASSISTIVE_NOTIFICATION:
            setDelayMsec(3000);
            setPosition(Position.ASSISTIVE);
            break;
        case HUMANIZED_MESSAGE:
        default:
            break;
        }
    }

    /**
     * Gets the caption part of the notification message.
     *
     * @return The message caption
     */
    public String getCaption() {
        return getState(false).caption;
    }

    /**
     * Sets the caption part of the notification message.
     *
     * @param caption
     *            The message caption
     */
    public void setCaption(String caption) {
        getState().caption = caption;
    }

    /**
     * Gets the description part of the notification message.
     *
     * @return The message description
     */
    public String getDescription() {
        return getState(false).description;
    }

    /**
     * Sets the description part of the notification message.
     *
     * @param description
     *            The message description
     */
    public void setDescription(String description) {
        getState().description = description;
    }

    /**
     * Gets the position of the notification message.
     *
     * @return The position
     */
    public Position getPosition() {
        return getState(false).position;
    }

    /**
     * Sets the position of the notification message.
     *
     * @param position
     *            The desired notification position, not {@code null}
     */
    public void setPosition(Position position) {
        if (position == null) {
            throw new IllegalArgumentException("Position can not be null");
        }
        getState().position = position;
    }

    /**
     * Gets the icon part of the notification message.
     *
     * @return The message icon
     */
    public Resource getIcon() {
        return getResource("icon");
    }

    /**
     * Sets the icon part of the notification message.
     *
     * @param icon
     *            The desired message icon
     */
    public void setIcon(Resource icon) {
        setResource("icon", icon);
    }

    /**
     * Gets the delay before the notification disappears.
     *
     * @return the delay in milliseconds, {@value #DELAY_FOREVER} indicates the
     *         message has to be clicked.
     */
    public int getDelayMsec() {
        return getState(false).delay;
    }

    /**
     * Sets the delay before the notification disappears.
     *
     * @param delayMsec
     *            the desired delay in milliseconds, {@value #DELAY_FOREVER} to
     *            require the user to click the message
     */
    public void setDelayMsec(int delayMsec) {
        getState().delay = delayMsec;
    }

    /**
     * Sets the style name for the notification message.
     *
     * @param styleName
     *            The desired style name
     */
    public void setStyleName(String styleName) {
        getState().styleName = styleName;
    }

    /**
     * Gets the style name for the notification message.
     *
     * @return The style name
     */
    public String getStyleName() {
        return getState(false).styleName;
    }

    /**
     * Sets whether html is allowed in the caption and description. If set to
     * true, the texts are passed to the browser as html and the developer is
     * responsible for ensuring no harmful html is used. If set to false, the
     * texts are passed to the browser as plain text.
     *
     * @param htmlContentAllowed
     *            true if the texts are used as html, false if used as plain
     *            text
     */
    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        getState().htmlContentAllowed = htmlContentAllowed;
    }

    /**
     * Checks whether caption and description are interpreted as HTML or plain
     * text.
     *
     * @return {@code true} if the texts are used as HTML, {@code false} if used
     *         as plain text
     * @see #setHtmlContentAllowed(boolean)
     */
    public boolean isHtmlContentAllowed() {
        return getState(false).htmlContentAllowed;
    }

    /**
     * Shows this notification on a Page.
     *
     * @param page
     *            The page on which the notification should be shown
     */
    public void show(Page page) {
        extend(page.getUI());
    }

    /**
     * Closes (hides) the notification.
     * <p>
     * If the notification is not shown, does nothing.
     *
     * @since 8.4
     */
    public void close() {
        close(false);
    }

    /**
     * Closes (hides) the notification.
     * <p>
     * If the notification is not shown, does nothing.
     *
     * @param userOriginated
     *            <code>true</code> if the notification was closed because the
     *            user clicked on it, <code>false</code> if the notification was
     *            closed from the server
     * @since 8.4
     */
    protected void close(boolean userOriginated) {
        if (!isAttached()) {
            return;
        }

        remove();
        fireEvent(new CloseEvent(this, userOriginated));
    }

    @Override
    protected NotificationState getState() {
        return (NotificationState) super.getState();
    }

    @Override
    protected NotificationState getState(boolean markAsDirty) {
        return (NotificationState) super.getState(markAsDirty);
    }

    /**
     * Shows a notification message on the middle of the current page. The
     * message automatically disappears ("humanized message").
     *
     * The caption is rendered as plain text with HTML automatically escaped.
     *
     * @see #Notification(String)
     * @see #show(Page)
     *
     * @param caption
     *            The message
     * @return The Notification
     */
    public static Notification show(String caption) {
        Notification notification = new Notification(caption);
        notification.extend(UI.getCurrent());
        return notification;
    }

    /**
     * Shows a notification message the current page. The position and behavior
     * of the message depends on the type, which is one of the basic types
     * defined in {@link Notification}, for instance
     * {@link Type#WARNING_MESSAGE}.
     *
     * The caption is rendered as plain text with HTML automatically escaped.
     *
     * @see Notification(String, int)
     * @see #show(Page)
     *
     * @param caption
     *            The message
     * @param type
     *            The message type
     * @return The Notification
     */
    public static Notification show(String caption, Type type) {
        Notification notification = new Notification(caption, type);
        notification.extend(UI.getCurrent());
        return notification;
    }

    /**
     * Shows a notification message the current page. The position and behavior
     * of the message depends on the type, which is one of the basic types
     * defined in {@link Notification}, for instance
     * Notification.TYPE_WARNING_MESSAGE.
     *
     * The caption is rendered as plain text with HTML automatically escaped.
     *
     * @see #Notification(String, Type)
     * @see #show(Page)
     *
     * @param caption
     *            The message
     * @param description
     *            The message description
     * @param type
     *            The message type
     * @return The Notification
     */
    public static Notification show(String caption, String description,
            Type type) {
        Notification notification = new Notification(caption, description,
                type);
        notification.extend(UI.getCurrent());
        return notification;
    }

    /**
     * Adds a CloseListener to the Notification.
     *
     * @param listener
     *            the CloseListener to add, not {@code null}
     * @since 8.2
     */
    public Registration addCloseListener(CloseListener listener) {
        return addListener(CloseEvent.class, listener, CLOSE_METHOD);
    }

    private static final Method CLOSE_METHOD;
    static {
        try {
            CLOSE_METHOD = CloseListener.class
                    .getDeclaredMethod("notificationClose", CloseEvent.class);
        } catch (final NoSuchMethodException e) {
            // This should never happen
            throw new RuntimeException(
                    "Internal error, notification close method not found");
        }
    }

    /**
     * Event fired when a notification is closed.
     *
     * @since 8.2
     */
    public static class CloseEvent extends ConnectorEvent
            implements HasUserOriginated {

        private boolean userOriginated;

        /**
         * @param source
         */
        public CloseEvent(Notification source) {
            this(source, true);
        }

        public CloseEvent(Notification source, boolean userOriginated) {
            super(source);
            this.userOriginated = userOriginated;
        }

        /**
         * Gets the Notification.
         *
         * @return The Notification
         */
        public Notification getNotification() {
            return (Notification) getSource();
        }

        @Override
        public boolean isUserOriginated() {
            return userOriginated;
        }
    }

    /**
     * An interface used for listening to Notification close events. Add the
     * CloseListener to a Notification and
     * {@link CloseListener#notificationClose(CloseEvent)} will be called
     * whenever the Notification is closed.
     *
     * @since 8.2
     */
    @FunctionalInterface
    public interface CloseListener extends SerializableEventListener {
        /**
         * Use {@link CloseEvent#getNotification()} to get a reference to the
         * {@link Notification} that was closed.
         *
         * @param e
         *            The triggered event
         */
        public void notificationClose(CloseEvent e);
    }

}
