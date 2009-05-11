package com.vaadin.terminal.gwt.client.ui.layout;

public class Margins {

    private int marginTop;
    private int marginBottom;
    private int marginLeft;
    private int marginRight;

    private int horizontal = 0;
    private int vertical = 0;

    public Margins(int marginTop, int marginBottom, int marginLeft,
            int marginRight) {
        super();
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;

        updateHorizontal();
        updateVertical();
    }

    public int getMarginTop() {
        return marginTop;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public int getHorizontal() {
        return horizontal;
    }

    public int getVertical() {
        return vertical;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
        updateVertical();
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
        updateVertical();
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        updateHorizontal();
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
        updateHorizontal();
    }

    private void updateVertical() {
        vertical = marginTop + marginBottom;
    }

    private void updateHorizontal() {
        horizontal = marginLeft + marginRight;
    }

    @Override
    public String toString() {
        return "Margins [marginLeft=" + marginLeft + ",marginTop=" + marginTop
                + ",marginRight=" + marginRight + ",marginBottom="
                + marginBottom + "]";
    }
}
