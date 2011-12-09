/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.io.Serializable;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;

/**
 * Helper class to store and transfer mouse event details.
 */
public class MouseEventDetails implements Serializable {
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
    private int relativeX = -1;
    private int relativeY = -1;

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

    public int getRelativeX() {
        return relativeX;
    }

    public int getRelativeY() {
        return relativeY;
    }

    public MouseEventDetails(NativeEvent evt) {
        this(evt, null);
    }

    public MouseEventDetails(NativeEvent evt, Element relativeToElement) {
        type = Event.getTypeInt(evt.getType());
        clientX = Util.getTouchOrMouseClientX(evt);
        clientY = Util.getTouchOrMouseClientY(evt);
        button = evt.getButton();
        altKey = evt.getAltKey();
        ctrlKey = evt.getCtrlKey();
        metaKey = evt.getMetaKey();
        shiftKey = evt.getShiftKey();
        if (relativeToElement != null) {
            relativeX = getRelativeX(clientX, relativeToElement);
            relativeY = getRelativeY(clientY, relativeToElement);
        }
    }

    private MouseEventDetails() {
    }

    @Override
    public String toString() {
        return serialize();
    }

    public String serialize() {
        return "" + button + DELIM + clientX + DELIM + clientY + DELIM + altKey
                + DELIM + ctrlKey + DELIM + metaKey + DELIM + shiftKey + DELIM
                + type + DELIM + relativeX + DELIM + relativeY;
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
        instance.relativeX = Integer.parseInt(fields[8]);
        instance.relativeY = Integer.parseInt(fields[9]);
        return instance;
    }

    public String getButtonName() {
        if (button == BUTTON_LEFT) {
            return "left";
        } else if (button == BUTTON_RIGHT) {
            return "right";
        } else if (button == BUTTON_MIDDLE) {
            return "middle";
        }

        return "";
    }

    public Class<MouseEventDetails> getType() {
        return MouseEventDetails.class;
    }

    public boolean isDoubleClick() {
        return type == Event.ONDBLCLICK;
    }

    private static int getRelativeX(int clientX, Element target) {
        return clientX - target.getAbsoluteLeft() + target.getScrollLeft()
                + target.getOwnerDocument().getScrollLeft();
    }

    private static int getRelativeY(int clientY, Element target) {
        return clientY - target.getAbsoluteTop() + target.getScrollTop()
                + target.getOwnerDocument().getScrollTop();
    }

}
