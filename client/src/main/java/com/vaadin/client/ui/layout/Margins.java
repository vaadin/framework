/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.client.ui.layout;

/**
 * A class for storing margin data.
 *
 * @author Vaadin Ltd
 */
public class Margins {

    private int marginTop;
    private int marginBottom;
    private int marginLeft;
    private int marginRight;

    private int horizontal = 0;
    private int vertical = 0;

    /**
     * Constructs an instance for storing margin data.
     *
     * @param marginTop
     *            top margin (in pixels)
     * @param marginBottom
     *            bottom margin (in pixels)
     * @param marginLeft
     *            left margin (in pixels)
     * @param marginRight
     *            right margin (in pixels)
     */
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

    /**
     * Returns the height of the top margin.
     *
     * @return top margin (in pixels)
     */
    public int getMarginTop() {
        return marginTop;
    }

    /**
     * Returns the height of the bottom margin.
     *
     * @return bottom margin (in pixels)
     */
    public int getMarginBottom() {
        return marginBottom;
    }

    /**
     * Returns the width of the left margin.
     *
     * @return left margin (in pixels)
     */
    public int getMarginLeft() {
        return marginLeft;
    }

    /**
     * Returns the width of the right margin.
     *
     * @return right margin (in pixels)
     */
    public int getMarginRight() {
        return marginRight;
    }

    /**
     * Returns the combined width of the left and the right margins.
     *
     * @return the sum of the left and the right margins (in pixels)
     */
    public int getHorizontal() {
        return horizontal;
    }

    /**
     * Returns the combined height of the top and the bottom margins.
     *
     * @return the sum of the top and the bottom margins (in pixels)
     */
    public int getVertical() {
        return vertical;
    }

    /**
     * Sets the height of the top margin.
     *
     * @param marginTop
     *            the top margin to set (in pixels)
     */
    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
        updateVertical();
    }

    /**
     * Sets the height of the bottom margin.
     *
     * @param marginBottom
     *            the bottom margin to set (in pixels)
     */
    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
        updateVertical();
    }

    /**
     * Sets the width of the left margin.
     *
     * @param marginLeft
     *            the left margin to set (in pixels)
     */
    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        updateHorizontal();
    }

    /**
     * Sets the width of the right margin.
     *
     * @param marginRight
     *            the right margin to set (in pixels)
     */
    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
        updateHorizontal();
    }

    /**
     * Updates the combined height of the top and the bottom margins.
     */
    private void updateVertical() {
        vertical = marginTop + marginBottom;
    }

    /**
     * Updates the combined width of the left and the right margins.
     */
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
