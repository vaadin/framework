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

public final class AlignmentInfo implements Serializable {
    /** Bitmask values for client server communication */
    public static class Bits implements Serializable {
        public static final int ALIGNMENT_LEFT = 1;
        public static final int ALIGNMENT_RIGHT = 2;
        public static final int ALIGNMENT_TOP = 4;
        public static final int ALIGNMENT_BOTTOM = 8;
        public static final int ALIGNMENT_HORIZONTAL_CENTER = 16;
        public static final int ALIGNMENT_VERTICAL_CENTER = 32;
    }

    public static final AlignmentInfo LEFT = new AlignmentInfo(
            Bits.ALIGNMENT_LEFT);
    public static final AlignmentInfo RIGHT = new AlignmentInfo(
            Bits.ALIGNMENT_RIGHT);
    public static final AlignmentInfo TOP = new AlignmentInfo(
            Bits.ALIGNMENT_TOP);
    public static final AlignmentInfo BOTTOM = new AlignmentInfo(
            Bits.ALIGNMENT_BOTTOM);
    public static final AlignmentInfo CENTER = new AlignmentInfo(
            Bits.ALIGNMENT_HORIZONTAL_CENTER);
    public static final AlignmentInfo MIDDLE = new AlignmentInfo(
            Bits.ALIGNMENT_VERTICAL_CENTER);
    public static final AlignmentInfo TOP_LEFT = new AlignmentInfo(
            Bits.ALIGNMENT_TOP + Bits.ALIGNMENT_LEFT);

    private final int bitMask;

    public AlignmentInfo(int bitMask) {
        this.bitMask = bitMask;
    }

    public AlignmentInfo(AlignmentInfo horizontal, AlignmentInfo vertical) {
        this(horizontal.getBitMask() + vertical.getBitMask());
    }

    public int getBitMask() {
        return bitMask;
    }

    public boolean isTop() {
        return (bitMask & Bits.ALIGNMENT_TOP) == Bits.ALIGNMENT_TOP;
    }

    public boolean isBottom() {
        return (bitMask & Bits.ALIGNMENT_BOTTOM) == Bits.ALIGNMENT_BOTTOM;
    }

    public boolean isLeft() {
        return (bitMask & Bits.ALIGNMENT_LEFT) == Bits.ALIGNMENT_LEFT;
    }

    public boolean isRight() {
        return (bitMask & Bits.ALIGNMENT_RIGHT) == Bits.ALIGNMENT_RIGHT;
    }

    public boolean isVerticalCenter() {
        return (bitMask & Bits.ALIGNMENT_VERTICAL_CENTER) == Bits.ALIGNMENT_VERTICAL_CENTER;
    }

    public boolean isHorizontalCenter() {
        return (bitMask & Bits.ALIGNMENT_HORIZONTAL_CENTER) == Bits.ALIGNMENT_HORIZONTAL_CENTER;
    }

    public String getVerticalAlignment() {
        if (isBottom()) {
            return "bottom";
        } else if (isVerticalCenter()) {
            return "middle";
        }
        return "top";
    }

    public String getHorizontalAlignment() {
        if (isRight()) {
            return "right";
        } else if (isHorizontalCenter()) {
            return "center";
        }
        return "left";
    }

}
