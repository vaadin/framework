/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.vaadin.terminal.gwt.client.ui.ManagedLayout;
import com.vaadin.terminal.gwt.client.ui.PostLayoutListener;
import com.vaadin.terminal.gwt.client.ui.SimpleManagedLayout;

public class LayoutManager {
    private final ApplicationConnection connection;
    private final Set<Element> nonPaintableElements = new HashSet<Element>();
    private final MeasuredSize nullSize = new MeasuredSize();

    public LayoutManager(ApplicationConnection connection) {
        this.connection = connection;
    }

    public static LayoutManager get(ApplicationConnection connection) {
        return connection.getLayoutManager();
    }

    public void registerDependency(ManagedLayout owner, Element element) {
        MeasuredSize measuredSize = ensureMeasured(element);

        MeasuredSize ownerSize = getMeasuredSize(owner);
        if (measuredSize.isHeightNeedsUpdate()) {
            ownerSize.setHeightNeedsUpdate();
        }
        if (measuredSize.isWidthNeedsUpdate()) {
            ownerSize.setWidthNeedsUpdate();
        }
        measuredSize.addDependent(owner.getId());
    }

    private MeasuredSize ensureMeasured(Element element) {
        MeasuredSize measuredSize = getMeasuredSize(element, null);
        if (measuredSize == null) {
            measuredSize = new MeasuredSize();

            if (VPaintableMap.get(connection).getPaintable(element) == null) {
                nonPaintableElements.add(element);
            }
            setMeasuredSize(element, measuredSize);
        }
        return measuredSize;
    }

    private boolean needsMeasure(Element e) {
        if (connection.getPaintableMap().getPid(e) != null) {
            return true;
        } else if (getMeasuredSize(e, nullSize).hasDependents()) {
            return true;
        } else {
            return false;
        }
    }

    private static native void setMeasuredSize(Element element,
            MeasuredSize measuredSize)
    /*-{
        if (measuredSize) {
            element.vMeasuredSize = measuredSize;
        } else {
            delete element.vMeasuredSize;
        }
    }-*/;

    private static native final MeasuredSize getMeasuredSize(Element element,
            MeasuredSize defaultSize)
    /*-{
        return element.vMeasuredSize || defaultSize;
    }-*/;

    private static final MeasuredSize getMeasuredSize(VPaintableWidget paintable) {
        Element element = paintable.getWidgetForPaintable().getElement();
        MeasuredSize measuredSize = getMeasuredSize(element, null);
        if (measuredSize == null) {
            measuredSize = new MeasuredSize();
            setMeasuredSize(element, measuredSize);
        }
        return measuredSize;
    }

    public void unregisterDependency(ManagedLayout owner, Element element) {
        MeasuredSize measuredSize = getMeasuredSize(element, null);
        if (measuredSize == null) {
            return;
        }
        measuredSize.removeDependent(owner.getId());
        if (!needsMeasure(element)) {
            nonPaintableElements.remove(element);
            setMeasuredSize(element, null);
        }
    }

    public void doLayout() {
        VPaintableMap paintableMap = connection.getPaintableMap();
        VPaintableWidget[] paintableWidgets = paintableMap
                .getRegisteredPaintableWidgets();

        int passes = 0;
        Duration totalDuration = new Duration();

        while (true) {
            Duration passDuration = new Duration();
            passes++;
            measureElements(paintableWidgets);

            FastStringSet needsHeightUpdate = FastStringSet.create();
            FastStringSet needsWidthUpdate = FastStringSet.create();

            for (VPaintableWidget paintable : paintableWidgets) {
                MeasuredSize measuredSize = getMeasuredSize(paintable);
                boolean managed = isManagedLayout(paintable);

                VPaintableWidgetContainer parent = paintable.getParent();
                boolean managedParent = parent != null
                        && isManagedLayout(parent);

                if (measuredSize.isHeightNeedsUpdate()) {
                    if (managed) {
                        needsHeightUpdate.add(paintable.getId());
                    }
                    if (!paintable.isRelativeHeight() && managedParent) {
                        needsHeightUpdate.add(parent.getId());
                    }
                }
                if (measuredSize.isWidthNeedsUpdate()) {
                    if (managed) {
                        needsWidthUpdate.add(paintable.getId());
                    }
                    if (!paintable.isRelativeWidth() && managedParent) {
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
                if (paintable instanceof DirectionalManagedLayout) {
                    DirectionalManagedLayout cl = (DirectionalManagedLayout) paintable;
                    cl.layoutHorizontally();
                } else if (paintable instanceof SimpleManagedLayout) {
                    SimpleManagedLayout rr = (SimpleManagedLayout) paintable;
                    rr.layout();
                    needsHeightUpdate.remove(pid);
                }
                updatedSet.add(pid);
            }

            JsArrayString needsHeightUpdateArray = needsHeightUpdate.dump();
            for (int i = 0; i < needsHeightUpdateArray.length(); i++) {
                String pid = needsHeightUpdateArray.get(i);

                VPaintableWidget paintable = (VPaintableWidget) paintableMap
                        .getPaintable(pid);
                if (paintable instanceof DirectionalManagedLayout) {
                    DirectionalManagedLayout cl = (DirectionalManagedLayout) paintable;
                    cl.layoutVertically();
                } else if (paintable instanceof SimpleManagedLayout) {
                    SimpleManagedLayout rr = (SimpleManagedLayout) paintable;
                    rr.layout();
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
            if (vPaintableWidget instanceof PostLayoutListener) {
                ((PostLayoutListener) vPaintableWidget).postLayout();
            }
        }

        VConsole.log("Total layout time: " + totalDuration.elapsedMillis()
                + "ms");
    }

    private void measureElements(VPaintableWidget[] paintableWidgets) {

        for (VPaintableWidget paintableWidget : paintableWidgets) {
            Element element = paintableWidget.getWidgetForPaintable()
                    .getElement();
            MeasuredSize measuredSize = getMeasuredSize(paintableWidget);
            measuredAndUpdate(element, measuredSize);
        }

        for (Element element : nonPaintableElements) {
            MeasuredSize measuredSize = getMeasuredSize(element, null);
            measuredAndUpdate(element, measuredSize);
        }
    }

    private void measuredAndUpdate(Element element, MeasuredSize measuredSize) {
        if (measuredSize.measure(element)) {
            JsArrayString dependents = measuredSize.getDependents();
            for (int i = 0; i < dependents.length(); i++) {
                String pid = dependents.get(i);
                VPaintableWidget dependent = (VPaintableWidget) connection
                        .getPaintableMap().getPaintable(pid);
                if (dependent != null) {
                    MeasuredSize dependentSize = getMeasuredSize(dependent);
                    if (measuredSize.isHeightNeedsUpdate()) {
                        dependentSize.setHeightNeedsUpdate();
                    }
                    if (measuredSize.isWidthNeedsUpdate()) {
                        dependentSize.setWidthNeedsUpdate();
                    }
                }
            }
        }
    }

    private static boolean isManagedLayout(VPaintableWidget paintable) {
        return paintable instanceof ManagedLayout;
    }

    public void foceLayout() {
        VPaintableMap paintableMap = connection.getPaintableMap();
        VPaintableWidget[] paintableWidgets = paintableMap
                .getRegisteredPaintableWidgets();
        for (VPaintableWidget vPaintableWidget : paintableWidgets) {
            MeasuredSize measuredSize = getMeasuredSize(vPaintableWidget);
            measuredSize.setHeightNeedsUpdate();
            measuredSize.setWidthNeedsUpdate();
        }
        doLayout();
    }

    public final void setNeedsUpdate(ManagedLayout layout) {
        setWidthNeedsUpdate(layout);
        setHeightNeedsUpdate(layout);
    }

    public final void setWidthNeedsUpdate(ManagedLayout layout) {
        getMeasuredSize(layout).setWidthNeedsUpdate();
    }

    public final void setHeightNeedsUpdate(ManagedLayout layout) {
        getMeasuredSize(layout).setHeightNeedsUpdate();
    }

    public boolean isMeasured(Element element) {
        return getMeasuredSize(element, nullSize) != nullSize;
    }

    public final int getOuterHeight(Element element) {
        return getMeasuredSize(element, nullSize).getOuterHeight();
    }

    public final int getOuterWidth(Element element) {
        return getMeasuredSize(element, nullSize).getOuterWidth();
    }

    public final int getInnerHeight(Element element) {
        return getMeasuredSize(element, nullSize).getInnerHeight();
    }

    public final int getInnerWidth(Element element) {
        return getMeasuredSize(element, nullSize).getInnerWidth();
    }

    public final int getBorderHeight(Element element) {
        return getMeasuredSize(element, nullSize).getBorderHeight();
    }

    public int getPaddingHeight(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingHeight();
    }

    public int getBorderWidth(Element element) {
        return getMeasuredSize(element, nullSize).getBorderWidth();
    }

    public int getPaddingWidth(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingWidth();
    }

    public int getPaddingTop(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingTop();
    }

    public int getPaddingLeft(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingLeft();
    }
}
