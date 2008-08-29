package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

/**
 * Helper class to store and transfer mouse event details.
 */
public class MouseEventDetails {
    public static final int BUTTON_LEFT = Event.BUTTON_LEFT;
    public static final int BUTTON_MIDDLE = Event.BUTTON_MIDDLE;
    public static final int BUTTON_RIGHT = Event.BUTTON_RIGHT;

    private static final char DELIM = ',';

    private int button;
    private int clientX;
    private int clientY;
    private boolean altKey;
    private boolean ctrlKey;
    private boolean metaKey;
    private boolean shiftKey;
    private int type;

    public int getButton() {
        return button;
    }

    public int getClientX() {
        return clientX;
    }

    public int getClientY() {
        return clientY;
    }

    public boolean isAltKey() {
        return altKey;
    }

    public boolean isCtrlKey() {
        return ctrlKey;
    }

    public boolean isMetaKey() {
        return metaKey;
    }

    public boolean isShiftKey() {
        return shiftKey;
    }

    public MouseEventDetails(Event evt) {
        button = DOM.eventGetButton(evt);
        clientX = DOM.eventGetClientX(evt);
        clientY = DOM.eventGetClientY(evt);
        altKey = DOM.eventGetAltKey(evt);
        ctrlKey = DOM.eventGetCtrlKey(evt);
        metaKey = DOM.eventGetMetaKey(evt);
        shiftKey = DOM.eventGetShiftKey(evt);
        type = DOM.eventGetType(evt);
    }

    private MouseEventDetails() {
    }

    public String toString() {
        return "" + button + DELIM + clientX + DELIM + clientY + DELIM + altKey
                + DELIM + ctrlKey + DELIM + metaKey + DELIM + shiftKey + DELIM
                + type;
    }

    public static MouseEventDetails deSerialize(String serializedString) {
        MouseEventDetails instance = new MouseEventDetails();
        String[] fields = serializedString.split(",");

        instance.button = Integer.parseInt(fields[0]);
        instance.clientX = Integer.parseInt(fields[1]);
        instance.clientY = Integer.parseInt(fields[2]);
        instance.altKey = Boolean.valueOf(fields[3]).booleanValue();
        instance.ctrlKey = Boolean.valueOf(fields[4]).booleanValue();
        instance.metaKey = Boolean.valueOf(fields[5]).booleanValue();
        instance.shiftKey = Boolean.valueOf(fields[6]).booleanValue();
        instance.type = Integer.parseInt(fields[7]);
        return instance;
    }

    public boolean isDoubleClick() {
        return type == Event.ONDBLCLICK;
    }

}
