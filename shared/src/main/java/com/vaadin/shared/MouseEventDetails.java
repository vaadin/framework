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
package com.vaadin.shared;

import java.io.Serializable;

/**
 * Helper class to store and transfer mouse event details.
 */
public class MouseEventDetails implements Serializable {
    /**
     * @deprecated use {@link MouseButton#LEFT} instead.
     */
    @Deprecated
    public static final MouseButton BUTTON_LEFT = MouseButton.LEFT;
    /**
     * @deprecated use {@link MouseButton#MIDDLE} instead.
     */
    @Deprecated
    public static final MouseButton BUTTON_MIDDLE = MouseButton.MIDDLE;
    /**
     * @deprecated use {@link MouseButton#RIGHT} instead.
     */
    @Deprecated
    public static final MouseButton BUTTON_RIGHT = MouseButton.RIGHT;

    /**
     * Constants for mouse buttons.
     * 
     * @author Vaadin Ltd
     * @version @VERSION@
     * @since 7.0
     * 
     */
    public enum MouseButton {
        LEFT("left"), RIGHT("right"), MIDDLE("middle");

        private String name;

        private MouseButton(String name) {
            this.name = name;
        }

        /**
         * Returns a human readable text representing the button
         * 
         * @return
         */
        public String getName() {
            return name;
        }

    }

    private static final char DELIM = ',';
    // From com.google.gwt.user.client.Event
    private static final int ONDBLCLICK = 0x00002;

    private MouseButton button;
    private int clientX;
    private int clientY;
    private boolean altKey;
    private boolean ctrlKey;
    private boolean metaKey;
    private boolean shiftKey;
    private int type;
    private int relativeX = -1;
    private int relativeY = -1;

    public MouseButton getButton() {
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

    public void setButton(MouseButton button) {
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
        return button.toString() + DELIM + clientX + DELIM + clientY + DELIM
                + altKey + DELIM + ctrlKey + DELIM + metaKey + DELIM + shiftKey
                + DELIM + type + DELIM + relativeX + DELIM + relativeY;
    }

    public static MouseEventDetails deSerialize(String serializedString) {
        MouseEventDetails instance = new MouseEventDetails();
        String[] fields = serializedString.split(",");
        instance.button = MouseButton.valueOf(fields[0]);
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
        return button == null ? "" : button.getName();
    }

    public int getType() {
        return type;
    }

    public boolean isDoubleClick() {
        return type == ONDBLCLICK;
    }

}
