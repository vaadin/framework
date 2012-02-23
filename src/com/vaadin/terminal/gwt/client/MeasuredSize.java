/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

public final class MeasuredSize {
    private int width = -1;
    private int height = -1;

    private int[] paddings = new int[4];
    private int[] borders = new int[4];
    private int[] margins = new int[4];

    private final VPaintableWidget paintable;

    private boolean heightChanged = true;
    private boolean widthChanged = true;

    private final Map<Element, int[]> dependencySizes = new HashMap<Element, int[]>();

    public MeasuredSize(VPaintableWidget paintable) {
        this.paintable = paintable;
    }

    public int getOuterHeight() {
        return height;
    }

    public int getOuterWidth() {
        return width;
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

    public void registerDependency(Element element) {
        if (!dependencySizes.containsKey(element)) {
            dependencySizes.put(element, new int[] { -1, -1 });
        }
    }

    public void unregisterDependency(Element element) {
        dependencySizes.remove(element);
    }

    public int getDependencyOuterWidth(Element e) {
        return getDependencySize(e, 0);
    }

    public int getDependencyOuterHeight(Element e) {
        return getDependencySize(e, 1);
    }

    private int getDependencySize(Element e, int index) {
        int[] sizes = dependencySizes.get(e);
        if (sizes == null) {
            return -1;
        } else {
            return sizes[index];
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

    void measure() {
        Widget widget = paintable.getWidgetForPaintable();
        ComputedStyle computedStyle = new ComputedStyle(widget.getElement());

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

        int requiredHeight = Util.getRequiredHeight(widget);
        int marginHeight = sumHeights(margins);
        setOuterHeight(requiredHeight + marginHeight);

        int requiredWidth = Util.getRequiredWidth(widget);
        int marginWidth = sumWidths(margins);
        setOuterWidth(requiredWidth + marginWidth);

        // int i = 0;
        for (Entry<Element, int[]> entry : dependencySizes.entrySet()) {
            Element element = entry.getKey();
            // int[] elementMargin = new ComputedStyle(element).getMargin();
            int[] sizes = entry.getValue();

            int elementWidth = element.getOffsetWidth();
            // elementWidth += elementMargin[1] + elementMargin[3];
            if (elementWidth != sizes[0]) {
                // System.out.println(paintable.getId() + " dependency " + i
                // + " width changed from " + sizes[0] + " to "
                // + elementWidth);
                sizes[0] = elementWidth;
                widthChanged = true;
            }

            int elementHeight = element.getOffsetHeight();
            // Causes infinite loops as a negative margin based on the
            // measured height is currently used for captions
            // elementHeight += elementMargin[0] + elementMargin[1];
            if (elementHeight != sizes[1]) {
                // System.out.println(paintable.getId() + " dependency " + i
                // + " height changed from " + sizes[1] + " to "
                // + elementHeight);
                sizes[1] = elementHeight;
                heightChanged = true;
            }
            // i++;
        }
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