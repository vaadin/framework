package com.vaadin.terminal.gwt.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class MeasureManager {

    public static final class MeasuredSize {
        private int width = -1;
        private int height = -1;

        private int[] paddings = new int[4];
        private int[] borders = new int[4];
        private int[] margins = new int[4];

        private final VPaintableWidget paintable;

        private boolean isDirty = true;

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
                isDirty = true;
                this.height = height;
            }
        }

        public void setOuterWidth(int width) {
            if (this.width != width) {
                isDirty = true;
                this.width = width;
            }
        }

        public boolean isDirty() {
            return isDirty;
        }

        public void setDirty(boolean isDirty) {
            this.isDirty = isDirty;
        }

        public void registerDependency(Element element) {
            if (!dependencySizes.containsKey(element)) {
                dependencySizes.put(element, new int[] { -1, -1 });
                isDirty = true;
            }
        }

        public void deRegisterDependency(Element element) {
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

        private void measure() {
            boolean changed = isDirty;

            Widget widget = paintable.getWidgetForPaintable();
            ComputedStyle computedStyle = new ComputedStyle(widget.getElement());

            int[] paddings = computedStyle.getPadding();
            if (!changed && !Arrays.equals(this.paddings, paddings)) {
                changed = true;
                this.paddings = paddings;
            }

            int[] margins = computedStyle.getMargin();
            if (!changed && !Arrays.equals(this.margins, margins)) {
                changed = true;
                this.margins = margins;
            }

            int[] borders = computedStyle.getBorder();
            if (!changed && !Arrays.equals(this.borders, borders)) {
                changed = true;
                this.borders = borders;
            }

            int offsetHeight = widget.getOffsetHeight();
            int marginHeight = sumHeights(margins);
            setOuterHeight(offsetHeight + marginHeight);

            int offsetWidth = widget.getOffsetWidth();
            int marginWidth = sumWidths(margins);
            setOuterWidth(offsetWidth + marginWidth);

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
                    changed = true;
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
                    changed = true;
                }
                // i++;
            }

            if (changed) {
                setDirty(true);
            }
        }
    }

    public void doLayout(ApplicationConnection client) {
        VPaintableMap paintableMap = client.getPaintableMap();
        VPaintableWidget[] paintableWidgets = paintableMap
                .getRegisteredPaintableWidgets();

        int passes = 0;
        long start = System.currentTimeMillis();
        while (true) {
            long passStart = System.currentTimeMillis();
            passes++;
            long measureStart = System.currentTimeMillis();
            FastStringSet changedSet = findChangedWidgets(paintableWidgets,
                    paintableMap);
            JsArrayString changed = changedSet.dump();
            long measureEnd = System.currentTimeMillis();

            VConsole.log("Measure in " + (measureEnd - measureStart) + " ms");

            if (changed.length() == 0) {
                VConsole.log("No more changes in pass " + passes);
                break;
            }

            if (passes > 100) {
                VConsole.log("Aborting layout");
                break;
            }

            FastStringSet affectedContainers = FastStringSet.create();
            for (int i = 0; i < changed.length(); i++) {
                VPaintableWidget paintable = (VPaintableWidget) paintableMap
                        .getPaintable(changed.get(i));
                VPaintableWidget parentPaintable = paintable.getParent();
                if (parentPaintable instanceof CalculatingLayout) {
                    affectedContainers
                            .add(paintableMap.getPid(parentPaintable));
                }
            }

            long layoutStart = System.currentTimeMillis();
            for (int i = 0; i < changed.length(); i++) {
                String pid = changed.get(i);
                VPaintableWidget paintable = (VPaintableWidget) paintableMap
                        .getPaintable(pid);
                if (!affectedContainers.contains(pid)) {
                    Widget widget = paintable.getWidgetForPaintable();
                    if (widget instanceof RequiresResize) {
                        // TODO Do nothing here if parent instanceof
                        // ProvidesRepaint?
                        ((RequiresResize) widget).onResize();
                    } else if (paintable instanceof CalculatingLayout) {
                        CalculatingLayout calculating = (CalculatingLayout) paintable;
                        calculating.updateHorizontalSizes();
                        calculating.updateVerticalSizes();
                    }
                }
            }

            JsArrayString affectedPids = affectedContainers.dump();
            for (int i = 0; i < affectedPids.length(); i++) {
                // Find all changed children
                String containerPid = affectedPids.get(i);
                CalculatingLayout container = (CalculatingLayout) paintableMap
                        .getPaintable(containerPid);

                container.updateHorizontalSizes();
                container.updateVerticalSizes();
            }

            long layoutEnd = System.currentTimeMillis();
            VConsole.log(affectedPids.length()
                    + " requestLayout invocations in "
                    + (layoutEnd - layoutStart) + "ms");

            long passEnd = System.currentTimeMillis();
            StringBuilder b = new StringBuilder();
            b.append(changed.length());
            b.append(" changed widgets in pass ");
            b.append(passes);
            b.append(" in ");
            b.append((passEnd - passStart));
            b.append(" ms: ");
            if (changed.length() < 10) {
                for (int i = 0; i < changed.length(); i++) {
                    if (i != 0) {
                        b.append(", ");
                    }
                    b.append(changed.get(i));
                }
            }
            VConsole.log(b.toString());
        }
        long end = System.currentTimeMillis();
        VConsole.log("Total layout time: " + (end - start) + "ms");
    }

    private FastStringSet findChangedWidgets(
            VPaintableWidget[] paintableWidgets, VPaintableMap paintableMap) {

        FastStringSet changed = FastStringSet.create();
        for (VPaintableWidget paintableWidget : paintableWidgets) {
            MeasureManager.MeasuredSize measuredSize = paintableWidget
                    .getMeasuredSize();
            measuredSize.measure();

            if (measuredSize.isDirty()) {
                changed.add(paintableMap.getPid(paintableWidget));
                measuredSize.setDirty(false);
            }
        }

        return changed;
    }
}
