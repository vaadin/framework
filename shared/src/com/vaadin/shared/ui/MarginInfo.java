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

/**
 * Describes the margin settings for each edge of a Component.
 * 
 * @author Vaadin Ltd
 */
public class MarginInfo implements Serializable {

    private static final int TOP = 1;
    private static final int RIGHT = 2;
    private static final int BOTTOM = 4;
    private static final int LEFT = 8;
    private static final int ALL = TOP | RIGHT | BOTTOM | LEFT;

    private int bitMask;

    /**
     * Creates a MarginInfo object with all edges set to either enabled or
     * disabled.
     * 
     * @param enabled
     *            the value to set for all edges
     */
    public MarginInfo(boolean enabled) {
        setMargins(enabled);
    }

    /**
     * Creates a MarginInfo object from a bit mask.
     * 
     * @param bitMask
     *            bits to set
     */
    public MarginInfo(int bitMask) {
        this.bitMask = bitMask;
    }

    /**
     * Creates a MarginInfo object by having each edge specified in clockwise
     * order (analogous to CSS).
     * 
     * @param top
     *            enable or disable top margin
     * @param right
     *            enable or disable right margin
     * @param bottom
     *            enable or disable bottom margin
     * @param left
     *            enable or disable left margin
     */
    public MarginInfo(boolean top, boolean right, boolean bottom, boolean left) {
        setMargins(top, right, bottom, left);
    }

    /**
     * Enables or disables margins on all edges simultaneously.
     * 
     * @param enabled
     *            if true, enables margins on all edges. If false, disables
     *            margins on all edges.
     */
    public void setMargins(boolean enabled) {
        bitMask = enabled ? ALL : 0;
    }

    /**
     * Sets margins on all edges individually.
     * 
     * @param top
     *            enable or disable top margin
     * @param right
     *            enable or disable right margin
     * @param bottom
     *            enable or disable bottom margin
     * @param left
     *            enable or disable left margin
     */
    public void setMargins(boolean top, boolean right, boolean bottom,
            boolean left) {
        bitMask = top ? TOP : 0;
        bitMask += right ? RIGHT : 0;
        bitMask += bottom ? BOTTOM : 0;
        bitMask += left ? LEFT : 0;
    }

    /**
     * Copies margin values from another MarginInfo object.
     * 
     * @param marginInfo
     *            another marginInfo object
     */
    public void setMargins(MarginInfo marginInfo) {
        bitMask = marginInfo.bitMask;
    }

    /**
     * Checks if this MarginInfo object has margins on all edges enabled.
     * 
     * @since 7.5.0
     * 
     * @return true if all edges have margins enabled
     */
    public boolean hasAll() {
        return (bitMask & ALL) == ALL;
    }

    /**
     * Checks if this MarginInfo object has the left edge margin enabled.
     * 
     * @return true if left edge margin is enabled
     */
    public boolean hasLeft() {
        return (bitMask & LEFT) == LEFT;
    }

    /**
     * Checks if this MarginInfo object has the right edge margin enabled.
     * 
     * @return true if right edge margin is enabled
     */
    public boolean hasRight() {
        return (bitMask & RIGHT) == RIGHT;
    }

    /**
     * Checks if this MarginInfo object has the top edge margin enabled.
     * 
     * @return true if top edge margin is enabled
     */
    public boolean hasTop() {
        return (bitMask & TOP) == TOP;
    }

    /**
     * Checks if this MarginInfo object has the bottom edge margin enabled.
     * 
     * @return true if bottom edge margin is enabled
     */
    public boolean hasBottom() {
        return (bitMask & BOTTOM) == BOTTOM;
    }

    /**
     * Returns the current bit mask that make up the margin settings.
     * 
     * @return an integer bit mask
     */
    public int getBitMask() {
        return bitMask;
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

    @Override
    public String toString() {
        return "MarginInfo(" + hasTop() + ", " + hasRight() + ", "
                + hasBottom() + ", " + hasLeft() + ")";

    }
}
