package com.vaadin.terminal.gwt.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ui.VMeasuringOrderedLayout;

public class MeasureManager {

    public static final class MeasuredSize {
        private int width = -1;
        private int height = -1;
        private boolean isDirty = true;
        private int captionWidth = 0;
        private int captionHeight = 0;

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

        public void setCaptionHeight(int captionHeight) {
            if (captionHeight != this.captionHeight) {
                isDirty = true;
            }
            this.captionHeight = captionHeight;
        }

        public void setCaptionWidth(int captionWidth) {
            if (captionWidth != this.captionWidth) {
                isDirty = true;
            }
            this.captionWidth = captionWidth;
        }

        public int getCaptionHeight() {
            return captionHeight;
        }

        public int getCaptionWidth() {
            return captionWidth;
        }
    }

    private static MeasureManager instance = new MeasureManager();

    public static Collection<VPaintableWidget> getChildren(
            VPaintableWidget paintable, ApplicationConnection client) {
        if (!(paintable instanceof Container)) {
            return Collections.emptySet();
        }
        Widget widget = paintable.getWidgetForPaintable();
        Collection<VPaintableWidget> children = new ArrayList<VPaintableWidget>();

        addDescendantPaintables(widget, children, client);

        return children;
    }

    private static void addDescendantPaintables(Widget widget,
            Collection<VPaintableWidget> paintables,
            ApplicationConnection client) {
        if (widget instanceof HasWidgets) {
            VPaintableMap paintableMap = client.getPaintableMap();
            for (Widget child : (HasWidgets) widget) {
                VPaintableWidget paintable = paintableMap.getPaintable(child);
                if (paintable != null) {
                    paintables.add(paintable);
                } else {
                    addDescendantPaintables(child, paintables, client);
                }
            }
        }
    }

    private static VPaintableWidget getParentPaintable(
            VPaintableWidget paintable, VPaintableMap paintableMap) {
        Widget widget = paintable.getWidgetForPaintable();
        while (true) {
            widget = widget.getParent();
            if (widget == null) {
                return null;
            }
            VPaintableWidget parentPaintable = paintableMap
                    .getPaintable(widget);
            if (parentPaintable != null) {
                return parentPaintable;
            }
            // Else continue with the parent
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
                VPaintableWidget parentPaintable = getParentPaintable(
                        paintable, paintableMap);
                if (parentPaintable instanceof Container) {
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
                    }
                }
            }

            JsArrayString affectedPids = affectedContainers.dump();
            for (int i = 0; i < affectedPids.length(); i++) {
                // Find all changed children
                String containerPid = affectedPids.get(i);
                VPaintableWidget container = (VPaintableWidget) paintableMap
                        .getPaintable(containerPid);
                Collection<VPaintableWidget> children = getChildren(container,
                        client);
                HashSet<Widget> changedChildren = new HashSet<Widget>();

                for (VPaintableWidget child : children) {
                    if (changedSet.contains(paintableMap.getPid(child))) {
                        changedChildren.add(child.getWidgetForPaintable());
                    }
                }

                ((Container) container).requestLayout(changedChildren);
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
            if (paintableWidget instanceof VMeasuringOrderedLayout) {
                VMeasuringOrderedLayout hasCaptions = (VMeasuringOrderedLayout) paintableWidget;
                Collection<VCaption> childCaptions = hasCaptions
                        .getChildCaptions();
                for (VCaption vCaption : childCaptions) {
                    VPaintableWidget captionOwner = vCaption.getOwner();
                    MeasureManager.MeasuredSize measuredSize = paintableMap
                            .getMeasuredSize(captionOwner);

                    int captionHeight = vCaption.getOffsetHeight();
                    int captionWidth = vCaption.getOffsetWidth();
                    if (captionHeight == 0 || captionWidth == 0) {
                        // Empty caption is probably detached
                        if (!Util.isAttachedAndDisplayed(vCaption)) {
                            // Ignore if it is detached
                            continue;
                        }
                    }
                    measuredSize.setCaptionHeight(captionHeight);
                    measuredSize.setCaptionWidth(captionWidth);
                    if (measuredSize.isDirty()) {
                        changed.add(paintableMap.getPid(captionOwner));
                        measuredSize.setDirty(false);
                    }
                }
            }

            MeasureManager.MeasuredSize measuredSize = paintableMap
                    .getMeasuredSize(paintableWidget);

            measuredSize.setWidth(widget.getOffsetWidth());
            measuredSize.setHeight(widget.getOffsetHeight());

            if (measuredSize.isDirty()) {
                changed.add(paintableMap.getPid(paintableWidget));
                measuredSize.setDirty(false);
            }
        }

        return changed;
    }

    private MeasureManager() {
        // Singleton constructor
    }

    public static MeasureManager get() {
        return instance;
    }
}
