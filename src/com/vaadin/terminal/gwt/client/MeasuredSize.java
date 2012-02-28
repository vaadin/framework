/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;

public class MeasuredSize {
    private int width = -1;
    private int height = -1;

    private int[] paddings = new int[4];
    private int[] borders = new int[4];
    private int[] margins = new int[4];

    private boolean heightChanged = true;
    private boolean widthChanged = true;

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

    public void setOuterHeight(int height) {
        if (this.height != height) {
            heightChanged = true;
            this.height = height;
        }
    }

    public void setOuterWidth(int width) {
        if (this.width != width) {
            widthChanged = true;
            this.width = width;
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

    boolean measure(Element element) {
        boolean wasHeightChanged = heightChanged;
        boolean wasWidthChanged = widthChanged;

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
        setOuterHeight(requiredHeight + marginHeight);

        int requiredWidth = Util.getRequiredWidth(element);
        VConsole.log("Width of " + element.toString() + " is " + requiredWidth);
        int marginWidth = sumWidths(margins);
        setOuterWidth(requiredWidth + marginWidth);

        return wasHeightChanged != heightChanged
                || wasWidthChanged != widthChanged;
    }

    void clearDirtyState() {
        heightChanged = widthChanged = false;
    }

    public boolean isHeightNeedsUpdate() {
        return heightChanged;
    }

    public boolean isWidthNeedsUpdate() {
        return widthChanged;
    }

    private static boolean hasWidthChanged(int[] sizes1, int[] sizes2) {
        return sizes1[1] != sizes2[1] || sizes1[3] != sizes2[3];
    }

    private static boolean hasHeightChanged(int[] sizes1, int[] sizes2) {
        return sizes1[0] != sizes2[0] || sizes1[2] != sizes2[2];
    }

    public void setWidthNeedsUpdate() {
        widthChanged = true;
    }

    public void setHeightNeedsUpdate() {
        heightChanged = true;
    }
}