package com.vaadin.terminal.gwt.client;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.JsArrayString;
import com.vaadin.terminal.gwt.client.ui.LayoutPhaseListener;
import com.vaadin.terminal.gwt.client.ui.ResizeRequired;

public class MeasureManager {

    public void doLayout(ApplicationConnection client) {
        VPaintableMap paintableMap = client.getPaintableMap();
        VPaintableWidget[] paintableWidgets = paintableMap
                .getRegisteredPaintableWidgets();

        for (VPaintableWidget vPaintableWidget : paintableWidgets) {
            if (vPaintableWidget instanceof LayoutPhaseListener) {
                ((LayoutPhaseListener) vPaintableWidget).beforeLayout();
            }
        }

        int passes = 0;
        Duration totalDuration = new Duration();

        while (true) {
            Duration passDuration = new Duration();
            passes++;
            measureWidgets(paintableWidgets);

            FastStringSet needsHeightUpdate = FastStringSet.create();
            FastStringSet needsWidthUpdate = FastStringSet.create();

            for (VPaintableWidget paintable : paintableWidgets) {
                MeasuredSize measuredSize = paintable.getMeasuredSize();
                boolean notifiableType = isNotifiableType(paintable);

                VPaintableWidgetContainer parent = paintable.getParent();
                boolean parentNotifiable = parent != null
                        && isNotifiableType(parent);

                if (measuredSize.isHeightNeedsUpdate()) {
                    if (notifiableType) {
                        needsHeightUpdate.add(paintable.getId());
                    }
                    if (!paintable.isRelativeHeight() && parentNotifiable) {
                        needsHeightUpdate.add(parent.getId());
                    }
                }
                if (measuredSize.isWidthNeedsUpdate()) {
                    if (notifiableType) {
                        needsWidthUpdate.add(paintable.getId());
                    }
                    if (!paintable.isRelativeWidth() && parentNotifiable) {
                        needsWidthUpdate.add(parent.getId());
                    }
                }
                measuredSize.clearDirtyState();
            }

            int measureTime = passDuration.elapsedMillis();
            VConsole.log("Measure in " + measureTime + " ms");

            FastStringSet updatedSet = FastStringSet.create();

            JsArrayString needsWidthUpdateArray = needsWidthUpdate.dump();

            for (int i = 0; i < needsWidthUpdateArray.length(); i++) {
                String pid = needsWidthUpdateArray.get(i);

                VPaintable paintable = paintableMap.getPaintable(pid);
                if (paintable instanceof CalculatingLayout) {
                    CalculatingLayout cl = (CalculatingLayout) paintable;
                    cl.updateHorizontalSizes();
                } else if (paintable instanceof ResizeRequired) {
                    ResizeRequired rr = (ResizeRequired) paintable;
                    rr.onResize();
                    needsHeightUpdate.remove(pid);
                }
                updatedSet.add(pid);
            }

            JsArrayString needsHeightUpdateArray = needsHeightUpdate.dump();
            for (int i = 0; i < needsHeightUpdateArray.length(); i++) {
                String pid = needsHeightUpdateArray.get(i);

                VPaintableWidget paintable = (VPaintableWidget) paintableMap
                        .getPaintable(pid);
                if (paintable instanceof CalculatingLayout) {
                    CalculatingLayout cl = (CalculatingLayout) paintable;
                    cl.updateVerticalSizes();
                } else if (paintable instanceof ResizeRequired) {
                    ResizeRequired rr = (ResizeRequired) paintable;
                    rr.onResize();
                }
                updatedSet.add(pid);
            }

            JsArrayString changed = updatedSet.dump();
            VConsole.log(changed.length() + " requestLayout invocations in "
                    + (passDuration.elapsedMillis() - measureTime) + "ms");

            StringBuilder b = new StringBuilder();
            b.append(changed.length());
            b.append(" changed widgets in pass ");
            b.append(passes);
            b.append(" in ");
            b.append(passDuration.elapsedMillis());
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

            if (changed.length() == 0) {
                VConsole.log("No more changes in pass " + passes);
                break;
            }

            if (passes > 100) {
                VConsole.log("Aborting layout");
                break;
            }
        }

        for (VPaintableWidget vPaintableWidget : paintableWidgets) {
            if (vPaintableWidget instanceof LayoutPhaseListener) {
                ((LayoutPhaseListener) vPaintableWidget).afterLayout();
            }
        }

        VConsole.log("Total layout time: " + totalDuration.elapsedMillis()
                + "ms");
    }

    private void measureWidgets(VPaintableWidget[] paintableWidgets) {

        for (VPaintableWidget paintableWidget : paintableWidgets) {
            MeasuredSize measuredSize = paintableWidget.getMeasuredSize();
            measuredSize.measure();
        }
    }

    private static boolean isNotifiableType(VPaintableWidget paintable) {
        return paintable instanceof ResizeRequired
                || paintable instanceof CalculatingLayout;
    }
}
