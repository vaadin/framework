/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

public class AlignmentInfo {

    public static final int ALIGNMENT_LEFT = 1;
    public static final int ALIGNMENT_RIGHT = 2;
    public static final int ALIGNMENT_TOP = 4;
    public static final int ALIGNMENT_BOTTOM = 8;
    public static final int ALIGNMENT_HORIZONTAL_CENTER = 16;
    public static final int ALIGNMENT_VERTICAL_CENTER = 32;

    private int bitMask;

    public AlignmentInfo(int bitMask) {
        this.bitMask = bitMask;
    }

    public AlignmentInfo(int horizontal, int vertical) {
        setAlignment(horizontal, vertical);
    }

    public void setAlignment(int horiz, int vert) {
        bitMask = horiz + vert;
    }

    public int getBitMask() {
        return bitMask;
    }

    public boolean isTop() {
        return (bitMask & ALIGNMENT_TOP) == ALIGNMENT_TOP;
    }

    public boolean isBottom() {
        return (bitMask & ALIGNMENT_BOTTOM) == ALIGNMENT_BOTTOM;
    }

    public boolean isLeft() {
        return (bitMask & ALIGNMENT_LEFT) == ALIGNMENT_LEFT;
    }

    public boolean isRight() {
        return (bitMask & ALIGNMENT_RIGHT) == ALIGNMENT_RIGHT;
    }

    public boolean isVerticalCenter() {
        return (bitMask & ALIGNMENT_VERTICAL_CENTER) == ALIGNMENT_VERTICAL_CENTER;
    }

    public boolean isHorizontalCenter() {
        return (bitMask & ALIGNMENT_HORIZONTAL_CENTER) == ALIGNMENT_HORIZONTAL_CENTER;
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
