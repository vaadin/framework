package com.vaadin.terminal.gwt.client;

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
        private boolean isDirty = true;

        private final Map<Element, int[]> dependencySizes = new HashMap<Element, int[]>();

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public void setHeight(int height) {
            if (this.height != height) {
                isDirty = true;
            }
            this.height = height;
        }

        public void setWidth(int width) {
            if (width != this.width) {
                isDirty = true;
            }
            this.width = width;
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

        public int getDependencyWidth(Element e) {
            return getDependencySize(e, 0);
        }

        public int getDependencyHeight(Element e) {
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
            Widget widget = paintableWidget.getWidgetForPaintable();

            MeasureManager.MeasuredSize measuredSize = paintableMap
                    .getMeasuredSize(paintableWidget);

            measuredSize.setWidth(widget.getOffsetWidth());
            measuredSize.setHeight(widget.getOffsetHeight());

            boolean dirtyDependency = false;
            for (Entry<Element, int[]> entry : measuredSize.dependencySizes
                    .entrySet()) {
                Element element = entry.getKey();
                int[] sizes = entry.getValue();

                int offsetWidth = element.getOffsetWidth();
                if (offsetWidth != sizes[0]) {
                    sizes[0] = offsetWidth;
                    dirtyDependency = true;
                }

                int offsetHeight = element.getOffsetHeight();
                if (offsetHeight != sizes[1]) {
                    sizes[1] = offsetHeight;
                    dirtyDependency = true;
                }

            }

            if (dirtyDependency) {
                measuredSize.setDirty(true);
            }

            if (measuredSize.isDirty()) {
                changed.add(paintableMap.getPid(paintableWidget));
                measuredSize.setDirty(false);
            }
        }

        return changed;
    }
}
