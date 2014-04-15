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

package com.vaadin.shared.ui;

import java.io.Serializable;

public class MarginInfo implements Serializable {

    private static final int TOP = 1;
    private static final int RIGHT = 2;
    private static final int BOTTOM = 4;
    private static final int LEFT = 8;

    private int bitMask;

    public MarginInfo(boolean enabled) {
        this(enabled, enabled, enabled, enabled);
    }

    public MarginInfo(int bitMask) {
        this.bitMask = bitMask;
    }

    public MarginInfo(boolean top, boolean right, boolean bottom, boolean left) {
        setMargins(top, right, bottom, left);
    }

    public void setMargins(boolean top, boolean right, boolean bottom,
            boolean left) {
        bitMask = top ? TOP : 0;
        bitMask += right ? RIGHT : 0;
        bitMask += bottom ? BOTTOM : 0;
        bitMask += left ? LEFT : 0;
    }

    public void setMargins(MarginInfo marginInfo) {
        bitMask = marginInfo.bitMask;
    }

    public boolean hasLeft() {
        return (bitMask & LEFT) == LEFT;
    }

    public boolean hasRight() {
        return (bitMask & RIGHT) == RIGHT;
    }

    public boolean hasTop() {
        return (bitMask & TOP) == TOP;
    }

    public boolean hasBottom() {
        return (bitMask & BOTTOM) == BOTTOM;
    }

    public int getBitMask() {
        return bitMask;
    }

    public void setMargins(boolean enabled) {
        if (enabled) {
            bitMask = TOP + RIGHT + BOTTOM + LEFT;
        } else {
            bitMask = 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MarginInfo)) {
            return false;
        }

        return ((MarginInfo) obj).bitMask == bitMask;
    }

    @Override
    public int hashCode() {
        return bitMask;
    }

}
