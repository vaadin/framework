/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;

public class MeasuredSize {
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

    private int width = -1;
    private int height = -1;

    private int[] paddings = new int[4];
    private int[] borders = new int[4];
    private int[] margins = new int[4];

    private FastStringSet dependents = FastStringSet.create();

    public int getOuterHeight() {
        return height;
    }

    public int getOuterWidth() {
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

    public int getInnerHeight() {
        return height - sumHeights(margins) - sumHeights(borders)
                - sumHeights(paddings);
    }

    public int getInnerWidth() {
        return width - sumWidths(margins) - sumWidths(borders)
                - sumWidths(paddings);
    }

    public boolean setOuterHeight(int height) {
        if (this.height != height) {
            this.height = height;
            return true;
        } else {
            return false;
        }
    }

    public boolean setOuterWidth(int width) {
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
        return margins[0];
    }

    public int getBorderRight() {
        return margins[1];
    }

    public int getBorderBottom() {
        return margins[2];
    }

    public int getBorderLeft() {
        return margins[3];
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

    public MeasureResult measure(Element element) {
        boolean heightChanged = false;
        boolean widthChanged = false;

        ComputedStyle computedStyle = new ComputedStyle(element);
        int[] paddings = computedStyle.getPadding();
        if (!heightChanged && hasHeightChanged(this.paddings, paddings)) {
            heightChanged = true;
        }
        if (!widthChanged && hasWidthChanged(this.paddings, paddings)) {
            widthChanged = true;
        }
        this.paddings = paddings;

        int[] margins = computedStyle.getMargin();
        if (!heightChanged && hasHeightChanged(this.margins, margins)) {
            heightChanged = true;
        }
        if (!widthChanged && hasWidthChanged(this.margins, margins)) {
            widthChanged = true;
        }
        this.margins = margins;

        int[] borders = computedStyle.getBorder();
        if (!heightChanged && hasHeightChanged(this.borders, borders)) {
            heightChanged = true;
        }
        if (!widthChanged && hasWidthChanged(this.borders, borders)) {
            widthChanged = true;
        }
        this.borders = borders;

        int requiredHeight = Util.getRequiredHeight(element);
        int marginHeight = sumHeights(margins);
        if (setOuterHeight(requiredHeight + marginHeight)) {
            heightChanged = true;
        }

        int requiredWidth = Util.getRequiredWidth(element);
        int marginWidth = sumWidths(margins);
        if (setOuterWidth(requiredWidth + marginWidth)) {
            widthChanged = true;
        }

        return new MeasureResult(widthChanged, heightChanged);
    }

    private static boolean hasWidthChanged(int[] sizes1, int[] sizes2) {
        return sizes1[1] != sizes2[1] || sizes1[3] != sizes2[3];
    }

    private static boolean hasHeightChanged(int[] sizes1, int[] sizes2) {
        return sizes1[0] != sizes2[0] || sizes1[2] != sizes2[2];
    }

}