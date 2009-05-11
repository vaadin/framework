package com.vaadin.terminal.gwt.client;

import com.vaadin.terminal.gwt.client.RenderInformation.Size;

/**
 * Contains information about render area.
 */
public class RenderSpace extends Size {

    private int scrollBarSize = 0;

    public RenderSpace(int width, int height) {
        super(width, height);
    }

    public RenderSpace() {
    }

    public RenderSpace(int width, int height, boolean useNativeScrollbarSize) {
        super(width, height);
        if (useNativeScrollbarSize) {
            scrollBarSize = Util.getNativeScrollbarSize();
        }
    }

    /**
     * Returns pixels available vertically for contained widget, including
     * possible scrollbars.
     */
    @Override
    public int getHeight() {
        return super.getHeight();
    }

    /**
     * Returns pixels available horizontally for contained widget, including
     * possible scrollbars.
     */
    @Override
    public int getWidth() {
        return super.getWidth();
    }

    /**
     * In case containing block has oveflow: auto, this method must return
     * number of pixels used by scrollbar. Returning zero means either that no
     * scrollbar will be visible.
     */
    public int getScrollbarSize() {
        return scrollBarSize;
    }

}
