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

import com.vaadin.shared.ui.AlignmentInfo.Bits;

/**
 * Class containing information about alignment of a component. Use the
 * pre-instantiated classes.
 */
@SuppressWarnings("serial")
public final class Alignment implements Serializable {

    public static final Alignment TOP_RIGHT = new Alignment(Bits.ALIGNMENT_TOP
            + Bits.ALIGNMENT_RIGHT);
    public static final Alignment TOP_LEFT = new Alignment(Bits.ALIGNMENT_TOP
            + Bits.ALIGNMENT_LEFT);
    public static final Alignment TOP_CENTER = new Alignment(Bits.ALIGNMENT_TOP
            + Bits.ALIGNMENT_HORIZONTAL_CENTER);
    public static final Alignment MIDDLE_RIGHT = new Alignment(
            Bits.ALIGNMENT_VERTICAL_CENTER + Bits.ALIGNMENT_RIGHT);
    public static final Alignment MIDDLE_LEFT = new Alignment(
            Bits.ALIGNMENT_VERTICAL_CENTER + Bits.ALIGNMENT_LEFT);
    public static final Alignment MIDDLE_CENTER = new Alignment(
            Bits.ALIGNMENT_VERTICAL_CENTER + Bits.ALIGNMENT_HORIZONTAL_CENTER);
    public static final Alignment BOTTOM_RIGHT = new Alignment(
            Bits.ALIGNMENT_BOTTOM + Bits.ALIGNMENT_RIGHT);
    public static final Alignment BOTTOM_LEFT = new Alignment(
            Bits.ALIGNMENT_BOTTOM + Bits.ALIGNMENT_LEFT);
    public static final Alignment BOTTOM_CENTER = new Alignment(
            Bits.ALIGNMENT_BOTTOM + Bits.ALIGNMENT_HORIZONTAL_CENTER);

    private final int bitMask;

    public Alignment(int bitMask) {
        this.bitMask = bitMask;
    }

    /**
     * Returns a bitmask representation of the alignment value. Used internally
     * by terminal.
     * 
     * @return the bitmask representation of the alignment value
     */
    public int getBitMask() {
        return bitMask;
    }

    /**
     * Checks if component is aligned to the top of the available space.
     * 
     * @return true if aligned top
     */
    public boolean isTop() {
        return (bitMask & Bits.ALIGNMENT_TOP) == Bits.ALIGNMENT_TOP;
    }

    /**
     * Checks if component is aligned to the bottom of the available space.
     * 
     * @return true if aligned bottom
     */
    public boolean isBottom() {
        return (bitMask & Bits.ALIGNMENT_BOTTOM) == Bits.ALIGNMENT_BOTTOM;
    }

    /**
     * Checks if component is aligned to the left of the available space.
     * 
     * @return true if aligned left
     */
    public boolean isLeft() {
        return (bitMask & Bits.ALIGNMENT_LEFT) == Bits.ALIGNMENT_LEFT;
    }

    /**
     * Checks if component is aligned to the right of the available space.
     * 
     * @return true if aligned right
     */
    public boolean isRight() {
        return (bitMask & Bits.ALIGNMENT_RIGHT) == Bits.ALIGNMENT_RIGHT;
    }

    /**
     * Checks if component is aligned middle (vertically center) of the
     * available space.
     * 
     * @return true if aligned bottom
     */
    public boolean isMiddle() {
        return (bitMask & Bits.ALIGNMENT_VERTICAL_CENTER) == Bits.ALIGNMENT_VERTICAL_CENTER;
    }

    /**
     * Checks if component is aligned center (horizontally) of the available
     * space.
     * 
     * @return true if aligned center
     */
    public boolean isCenter() {
        return (bitMask & Bits.ALIGNMENT_HORIZONTAL_CENTER) == Bits.ALIGNMENT_HORIZONTAL_CENTER;
    }

    /**
     * Returns string representation of vertical alignment.
     * 
     * @return vertical alignment as CSS value
     */
    public String getVerticalAlignment() {
        if (isBottom()) {
            return "bottom";
        } else if (isMiddle()) {
            return "middle";
        }
        return "top";
    }

    /**
     * Returns string representation of horizontal alignment.
     * 
     * @return horizontal alignment as CSS value
     */
    public String getHorizontalAlignment() {
        if (isRight()) {
            return "right";
        } else if (isCenter()) {
            return "center";
        }
        return "left";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        Alignment a = (Alignment) obj;
        return bitMask == a.bitMask;
    }

    @Override
    public int hashCode() {
        return bitMask;
    }

    @Override
    public String toString() {
        return String.valueOf(bitMask);
    }

}
