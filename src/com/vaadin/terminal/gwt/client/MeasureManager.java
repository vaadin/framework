package com.vaadin.terminal.gwt.client;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class MeasureManager {

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
            FastStringSet changedSet = findChangedWidgets(paintableWidgets);
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
                    affectedContainers.add(parentPaintable.getId());
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

    private FastStringSet findChangedWidgets(VPaintableWidget[] paintableWidgets) {

        FastStringSet changed = FastStringSet.create();
        for (VPaintableWidget paintableWidget : paintableWidgets) {
            MeasuredSize measuredSize = paintableWidget.getMeasuredSize();
            measuredSize.measure();

            if (measuredSize.isDirty()) {
                changed.add(paintableWidget.getId());
                measuredSize.setDirty(false);
            }
        }

        return changed;
    }
}
