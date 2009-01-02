/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

public class IMarginInfo {

    private static final int TOP = 1;
    private static final int RIGHT = 2;
    private static final int BOTTOM = 4;
    private static final int LEFT = 8;

    private int bitMask;

    public IMarginInfo(int bitMask) {
        this.bitMask = bitMask;
    }

    public IMarginInfo(boolean top, boolean right, boolean bottom, boolean left) {
        setMargins(top, right, bottom, left);
    }

    public void setMargins(boolean top, boolean right, boolean bottom,
            boolean left) {
        bitMask = top ? TOP : 0;
        bitMask += right ? RIGHT : 0;
        bitMask += bottom ? BOTTOM : 0;
        bitMask += left ? LEFT : 0;
    }

    public void setMargins(IMarginInfo marginInfo) {
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
        if (!(obj instanceof IMarginInfo)) {
            return false;
        }

        return ((IMarginInfo) obj).bitMask == bitMask;
    }

}
