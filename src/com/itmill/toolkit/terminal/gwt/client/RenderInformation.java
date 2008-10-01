package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.Element;

/**
 * Contains size information about a rendered container and its content area.
 * 
 * @author Artur Signell
 * 
 */
public class RenderInformation {

    private Size contentArea = new Size(0, 0);
    private Size renderedSize = new Size(-1, -1);

    public void setContentAreaWidth(int w) {
        contentArea.setWidth(w);
    }

    public void setContentAreaHeight(int h) {
        contentArea.setHeight(h);
    }

    public Size getContentAreaSize() {
        return contentArea;

    }

    public Size getRenderedSize() {
        return renderedSize;
    }

    /**
     * Update the size of the widget.
     * 
     * @param widget
     * 
     * @return true if the size has changed since last update
     */
    public boolean updateSize(Element element) {
        Size newSize = new Size(element.getOffsetWidth(), element
                .getOffsetHeight());
        if (newSize.equals(renderedSize)) {
            return false;
        } else {
            renderedSize = newSize;
            return true;
        }
    }

    @Override
    public String toString() {
        return "RenderInformation [contentArea=" + contentArea
                + ",renderedSize=" + renderedSize + "]";

    }

    public static class FloatSize {

        private float width, height;

        public FloatSize(float width, float height) {
            this.width = width;
            this.height = height;
        }

        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
        }

    }

    public static class Size {

        private int width, height;

        @Override
        public boolean equals(Object obj) {
            Size other = (Size) obj;
            return other.width == width && other.height == height;
        }

        public Size(int width, int height) {
            this.height = height;
            this.width = width;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        @Override
        public String toString() {
            return "Size [width=" + width + ",height=" + height + "]";
        }
    }

}
