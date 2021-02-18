/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;

public class MeasuredSize {
    private static final boolean DEBUG_SIZE_CHANGES = false;

    public static class MeasureResult {
        private final boolean widthChanged;
        private final boolean heightChanged;

        private MeasureResult(boolean widthChanged, boolean heightChanged) {
            this.widthChanged = widthChanged;
            this.heightChanged = heightChanged;
        }

        public boolean isHeightChanged() {
            return heightChanged;
        }

        public boolean isWidthChanged() {
            return widthChanged;
        }

        public boolean isChanged() {
            return heightChanged || widthChanged;
        }
    }

    private double width = -1;
    private double height = -1;

    private int[] paddings = new int[4];
    private int[] borders = new int[4];
    private int[] margins = new int[4];

    private FastStringSet dependents = FastStringSet.create();

    public double getOuterHeight() {
        return height;
    }

    public double getOuterWidth() {
        return width;
    }

    public void addDependent(String pid) {
        dependents.add(pid);
    }

    public void removeDependent(String pid) {
        dependents.remove(pid);
    }

    public boolean hasDependents() {
        return !dependents.isEmpty();
    }

    public JsArrayString getDependents() {
        return dependents.dump();
    }

    private static int sumWidths(int[] sizes) {
        return sizes[1] + sizes[3];
    }

    private static int sumHeights(int[] sizes) {
        return sizes[0] + sizes[2];
    }

    public double getInnerHeight() {
        return height - sumHeights(margins) - sumHeights(borders)
                - sumHeights(paddings);
    }

    public double getInnerWidth() {
        return width - sumWidths(margins) - sumWidths(borders)
                - sumWidths(paddings);
    }

    public boolean setOuterHeight(double height) {
        if (this.height != height) {
            this.height = height;
            return true;
        } else {
            return false;
        }
    }

    public boolean setOuterWidth(double width) {
        if (this.width != width) {
            this.width = width;
            return true;
        } else {
            return false;
        }
    }

    public int getBorderHeight() {
        return sumHeights(borders);
    }

    public int getBorderWidth() {
        return sumWidths(borders);
    }

    public int getPaddingHeight() {
        return sumHeights(paddings);
    }

    public int getPaddingWidth() {
        return sumWidths(paddings);
    }

    public int getMarginHeight() {
        return sumHeights(margins);
    }

    public int getMarginWidth() {
        return sumWidths(margins);
    }

    public int getMarginTop() {
        return margins[0];
    }

    public int getMarginRight() {
        return margins[1];
    }

    public int getMarginBottom() {
        return margins[2];
    }

    public int getMarginLeft() {
        return margins[3];
    }

    public int getBorderTop() {
        return borders[0];
    }

    public int getBorderRight() {
        return borders[1];
    }

    public int getBorderBottom() {
        return borders[2];
    }

    public int getBorderLeft() {
        return borders[3];
    }

    public int getPaddingTop() {
        return paddings[0];
    }

    public int getPaddingRight() {
        return paddings[1];
    }

    public int getPaddingBottom() {
        return paddings[2];
    }

    public int getPaddingLeft() {
        return paddings[3];
    }

    /**
     * Measures paddings, margins, border, height, and weight of the given
     * element and stores the results within this {@link MeasuredSize} object.
     * The measurements are unreliable if the element or any of its parents are
     * in the middle of a transform animation.
     *
     * @param element
     *            element to be measured
     * @return data object for whether the width or height of the given element
     *         has changed since previous measure
     * @see {@link #measure(Element, boolean)}
     */
    public MeasureResult measure(Element element) {
        return measure(element, false);
    }

    /**
     * Measures paddings, margins, border, height, and weight of the given
     * element and stores the results within this {@link MeasuredSize} object.
     *
     * @param element
     *            element to be measured
     * @param thoroughSizeCheck
     *            {@code true} if the measuring should use the more reliable
     *            size check that requires ensuring that the element is still
     *            present in the DOM tree, {@code false} for the slightly faster
     *            check that will give incorrect size information if this method
     *            is called while the element or any of its parents are in the
     *            middle of a transform animation.
     * @return data object for whether the width or height of the given element
     *         has changed since previous measure
     */
    public MeasureResult measure(Element element, boolean thoroughSizeCheck) {
        if (thoroughSizeCheck
                && !Document.get().getBody().isOrHasChild(element)) {
            return new MeasureResult(false, false);
        }

        Profiler.enter("MeasuredSize.measure");
        boolean heightChanged = false;
        boolean widthChanged = false;

        Profiler.enter("new ComputedStyle");
        ComputedStyle computedStyle = new ComputedStyle(element);
        int[] paddings = computedStyle.getPadding();
        // Some browsers do not reflow until accessing data from the computed
        // style object
        Profiler.leave("new ComputedStyle");

        Profiler.enter("Measure paddings");
        if (!heightChanged && hasHeightChanged(this.paddings, paddings)) {
            debugSizeChange(element, "Height (padding)", this.paddings,
                    paddings);
            heightChanged = true;
        }
        if (!widthChanged && hasWidthChanged(this.paddings, paddings)) {
            debugSizeChange(element, "Width (padding)", this.paddings,
                    paddings);
            widthChanged = true;
        }
        this.paddings = paddings;
        Profiler.leave("Measure paddings");

        Profiler.enter("Measure margins");
        int[] margins = computedStyle.getMargin();
        if (!heightChanged && hasHeightChanged(this.margins, margins)) {
            debugSizeChange(element, "Height (margins)", this.margins, margins);
            heightChanged = true;
        }
        if (!widthChanged && hasWidthChanged(this.margins, margins)) {
            debugSizeChange(element, "Width (margins)", this.margins, margins);
            widthChanged = true;
        }
        this.margins = margins;
        Profiler.leave("Measure margins");

        Profiler.enter("Measure borders");
        int[] borders = computedStyle.getBorder();
        if (!heightChanged && hasHeightChanged(this.borders, borders)) {
            debugSizeChange(element, "Height (borders)", this.borders, borders);
            heightChanged = true;
        }
        if (!widthChanged && hasWidthChanged(this.borders, borders)) {
            debugSizeChange(element, "Width (borders)", this.borders, borders);
            widthChanged = true;
        }
        this.borders = borders;
        Profiler.leave("Measure borders");

        Profiler.enter("Measure height");
        double requiredHeight;
        if (thoroughSizeCheck) {
            requiredHeight = computedStyle.getHeightIncludingBorderPadding();
            if (Double.isNaN(requiredHeight)) {
                requiredHeight = 0;
            }
        } else {
            requiredHeight = WidgetUtil.getRequiredHeightDouble(element);
        }
        double outerHeight = requiredHeight + sumHeights(margins);
        double oldHeight = height;
        if (setOuterHeight(outerHeight)) {
            debugSizeChange(element, "Height (outer)", oldHeight, height);
            heightChanged = true;
        }
        Profiler.leave("Measure height");

        Profiler.enter("Measure width");
        double requiredWidth;
        if (thoroughSizeCheck) {
            requiredWidth = computedStyle.getWidthIncludingBorderPadding();
            if (Double.isNaN(requiredWidth)) {
                requiredWidth = 0;
            }
        } else {
            requiredWidth = WidgetUtil.getRequiredWidthDouble(element);
        }
        double outerWidth = requiredWidth + sumWidths(margins);
        double oldWidth = width;
        if (setOuterWidth(outerWidth)) {
            debugSizeChange(element, "Width (outer)", oldWidth, width);
            widthChanged = true;
        }
        Profiler.leave("Measure width");

        Profiler.leave("MeasuredSize.measure");

        return new MeasureResult(widthChanged, heightChanged);
    }

    private void debugSizeChange(Element element, String sizeChangeType,
            int[] changedFrom, int[] changedTo) {
        debugSizeChange(element, sizeChangeType,
                java.util.Arrays.asList(changedFrom).toString(),
                java.util.Arrays.asList(changedTo).toString());
    }

    private void debugSizeChange(Element element, String sizeChangeType,
            double changedFrom, double changedTo) {
        debugSizeChange(element, sizeChangeType, String.valueOf(changedFrom),
                String.valueOf(changedTo));
    }

    private void debugSizeChange(Element element, String sizeChangeType,
            String changedFrom, String changedTo) {
        if (DEBUG_SIZE_CHANGES) {
            getLogger().info(sizeChangeType + " has changed from " + changedFrom
                    + " to " + changedTo + " for " + element);
        }
    }

    private static boolean hasWidthChanged(int[] sizes1, int[] sizes2) {
        return sizes1[1] != sizes2[1] || sizes1[3] != sizes2[3];
    }

    private static boolean hasHeightChanged(int[] sizes1, int[] sizes2) {
        return sizes1[0] != sizes2[0] || sizes1[2] != sizes2[2];
    }

    private static Logger getLogger() {
        return Logger.getLogger(MeasuredSize.class.getName());
    }

}
