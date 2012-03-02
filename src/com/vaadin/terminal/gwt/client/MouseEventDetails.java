/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.io.Serializable;

/**
 * Helper class to store and transfer mouse event details.
 */
public class MouseEventDetails implements Serializable {
    // From com.google.gwt.dom.client.NativeEvent
    public static final int BUTTON_LEFT = 1;
    public static final int BUTTON_MIDDLE = 4;
    public static final int BUTTON_RIGHT = 2;

    private static final char DELIM = ',';
    // From com.google.gwt.user.client.Event
    private static final int ONDBLCLICK = 0x00002;

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

    public void setButton(int button) {
        this.button = button;
    }

    public void setClientX(int clientX) {
        this.clientX = clientX;
    }

    public void setClientY(int clientY) {
        this.clientY = clientY;
    }

    public void setAltKey(boolean altKey) {
        this.altKey = altKey;
    }

    public void setCtrlKey(boolean ctrlKey) {
        this.ctrlKey = ctrlKey;
    }

    public void setMetaKey(boolean metaKey) {
        this.metaKey = metaKey;
    }

    public void setShiftKey(boolean shiftKey) {
        this.shiftKey = shiftKey;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setRelativeX(int relativeX) {
        this.relativeX = relativeX;
    }

    public void setRelativeY(int relativeY) {
        this.relativeY = relativeY;
    }

    public MouseEventDetails() {
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

    public int getType() {
        return type;
    }

    public boolean isDoubleClick() {
        return type == ONDBLCLICK;
    }

}
