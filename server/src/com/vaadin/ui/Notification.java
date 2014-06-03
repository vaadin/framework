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

package com.vaadin.ui;

import java.io.Serializable;

import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.shared.Position;

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
 * <li>TYPE_TRAY_NOTIFICATION is shown for a while in the lower left corner of
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
public class Notification implements Serializable {
    public enum Type {
        HUMANIZED_MESSAGE("humanized"), WARNING_MESSAGE("warning"), ERROR_MESSAGE(
                "error"), TRAY_NOTIFICATION("tray"),
        /**
         * @since 7.2
         */
        ASSISTIVE_NOTIFICATION("assistive");

        private String style;

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

    private String caption;
    private String description;
    private Resource icon;
    private Position position = Position.MIDDLE_CENTER;
    private int delayMsec = 0;
    private String styleName;
    private boolean htmlContentAllowed;

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
        this.caption = caption;
        this.description = description;
        this.htmlContentAllowed = htmlContentAllowed;
        setType(type);
    }

    private void setType(Type type) {
        styleName = type.getStyle();
        switch (type) {
        case WARNING_MESSAGE:
            delayMsec = 1500;
            break;
        case ERROR_MESSAGE:
            delayMsec = -1;
            break;
        case TRAY_NOTIFICATION:
            delayMsec = 3000;
            position = Position.BOTTOM_RIGHT;
            break;
        case ASSISTIVE_NOTIFICATION:
            delayMsec = 3000;
            position = Position.ASSISTIVE;
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
        return caption;
    }

    /**
     * Sets the caption part of the notification message
     * 
     * @param caption
     *            The message caption
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Gets the description part of the notification message.
     * 
     * @return The message description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description part of the notification message.
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the position of the notification message.
     * 
     * @return The position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets the position of the notification message.
     * 
     * @param position
     *            The desired notification position
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Gets the icon part of the notification message.
     * 
     * @return The message icon
     */
    public Resource getIcon() {
        return icon;
    }

    /**
     * Sets the icon part of the notification message.
     * 
     * @param icon
     *            The desired message icon
     */
    public void setIcon(Resource icon) {
        this.icon = icon;
    }

    /**
     * Gets the delay before the notification disappears.
     * 
     * @return the delay in msec, -1 indicates the message has to be clicked.
     */
    public int getDelayMsec() {
        return delayMsec;
    }

    /**
     * Sets the delay before the notification disappears.
     * 
     * @param delayMsec
     *            the desired delay in msec, -1 to require the user to click the
     *            message
     */
    public void setDelayMsec(int delayMsec) {
        this.delayMsec = delayMsec;
    }

    /**
     * Sets the style name for the notification message.
     * 
     * @param styleName
     *            The desired style name.
     */
    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    /**
     * Gets the style name for the notification message.
     * 
     * @return
     */
    public String getStyleName() {
        return styleName;
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
        this.htmlContentAllowed = htmlContentAllowed;
    }

    /**
     * Checks whether caption and description are interpreted as html or plain
     * text.
     * 
     * @return true if the texts are used as html, false if used as plain text
     * @see #setHtmlContentAllowed(boolean)
     */
    public boolean isHtmlContentAllowed() {
        return htmlContentAllowed;
    }

    /**
     * Shows this notification on a Page.
     * 
     * @param page
     *            The page on which the notification should be shown
     */
    public void show(Page page) {
        // TODO Can avoid deprecated API when Notification extends Extension
        page.showNotification(this);
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
     */
    public static void show(String caption) {
        new Notification(caption).show(Page.getCurrent());
    }

    /**
     * Shows a notification message the current page. The position and behavior
     * of the message depends on the type, which is one of the basic types
     * defined in {@link Notification}, for instance
     * Notification.TYPE_WARNING_MESSAGE.
     * 
     * The caption is rendered as plain text with HTML automatically escaped.
     * 
     * @see #Notification(String, int)
     * @see #show(Page)
     * 
     * @param caption
     *            The message
     * @param type
     *            The message type
     */
    public static void show(String caption, Type type) {
        new Notification(caption, type).show(Page.getCurrent());
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
     */
    public static void show(String caption, String description, Type type) {
        new Notification(caption, description, type).show(Page.getCurrent());
    }
}
