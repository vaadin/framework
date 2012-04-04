/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.vaadin.terminal.gwt.client.MeasuredSize.MeasureResult;
import com.vaadin.terminal.gwt.client.ui.ManagedLayout;
import com.vaadin.terminal.gwt.client.ui.PostLayoutListener;
import com.vaadin.terminal.gwt.client.ui.SimpleManagedLayout;
import com.vaadin.terminal.gwt.client.ui.VNotification;
import com.vaadin.terminal.gwt.client.ui.layout.LayoutDependencyTree;
import com.vaadin.terminal.gwt.client.ui.layout.RequiresOverflowAutoFix;

public class LayoutManager {
    private static final String LOOP_ABORT_MESSAGE = "Aborting layout after 100 passes. This would probably be an infinite loop.";
    private ApplicationConnection connection;
    private final Set<Element> nonPaintableElements = new HashSet<Element>();
    private final MeasuredSize nullSize = new MeasuredSize();

    private LayoutDependencyTree currentDependencyTree;

    private final Collection<ManagedLayout> needsHorizontalLayout = new HashSet<ManagedLayout>();
    private final Collection<ManagedLayout> needsVerticalLayout = new HashSet<ManagedLayout>();

    private final Collection<ComponentConnector> pendingOverflowFixes = new HashSet<ComponentConnector>();

    public void setConnection(ApplicationConnection connection) {
        if (this.connection != null) {
            throw new RuntimeException(
                    "LayoutManager connection can never be changed");
        }
        this.connection = connection;
    }

    public static LayoutManager get(ApplicationConnection connection) {
        return connection.getLayoutManager();
    }

    public void registerDependency(ManagedLayout owner, Element element) {
        MeasuredSize measuredSize = ensureMeasured(element);
        setNeedsUpdate(owner);
        measuredSize.addDependent(owner.getConnectorId());
    }

    private MeasuredSize ensureMeasured(Element element) {
        MeasuredSize measuredSize = getMeasuredSize(element, null);
        if (measuredSize == null) {
            measuredSize = new MeasuredSize();

            if (ConnectorMap.get(connection).getConnector(element) == null) {
                nonPaintableElements.add(element);
            }
            setMeasuredSize(element, measuredSize);
        }
        return measuredSize;
    }

    private boolean needsMeasure(Element e) {
        if (connection.getConnectorMap().getConnectorId(e) != null) {
            return true;
        } else if (getMeasuredSize(e, nullSize).hasDependents()) {
            return true;
        } else {
            return false;
        }
    }

    protected native void setMeasuredSize(Element element,
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

    private final MeasuredSize getMeasuredSize(ComponentConnector paintable) {
        Element element = paintable.getWidget().getElement();
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
        measuredSize.removeDependent(owner.getConnectorId());
        if (!needsMeasure(element)) {
            nonPaintableElements.remove(element);
            setMeasuredSize(element, null);
        }
    }

    public boolean isLayoutRunning() {
        return currentDependencyTree != null;
    }

    private void countLayout(Map<ManagedLayout, Integer> layoutCounts,
            ManagedLayout layout) {
        Integer count = layoutCounts.get(layout);
        if (count == null) {
            count = Integer.valueOf(0);
        } else {
            count = Integer.valueOf(count.intValue() + 1);
        }
        layoutCounts.put(layout, count);
        if (count.intValue() > 2) {
            VConsole.error(Util.getConnectorString(layout)
                    + " has been layouted " + count.intValue() + " times");
        }
    }

    public void doLayout() {
        if (isLayoutRunning()) {
            throw new IllegalStateException(
                    "Can't start a new layout phase before the previous layout phase ends.");
        }
        VConsole.log("Starting layout phase");

        Map<ManagedLayout, Integer> layoutCounts = new HashMap<ManagedLayout, Integer>();

        int passes = 0;
        Duration totalDuration = new Duration();

        currentDependencyTree = new LayoutDependencyTree();

        for (ManagedLayout layout : needsHorizontalLayout) {
            currentDependencyTree.setNeedsHorizontalLayout(layout, true);
        }
        for (ManagedLayout layout : needsVerticalLayout) {
            currentDependencyTree.setNeedsVerticalLayout(layout, true);
        }
        needsHorizontalLayout.clear();
        needsVerticalLayout.clear();
        measureNonPaintables(currentDependencyTree);

        VConsole.log("Layout init in " + totalDuration.elapsedMillis() + " ms");

        while (true) {
            Duration passDuration = new Duration();
            passes++;

            int measuredConnectorCount = measureConnectors(
                    currentDependencyTree, passes == 1);

            int measureTime = passDuration.elapsedMillis();
            VConsole.log("  Measured " + measuredConnectorCount
                    + " elements in " + measureTime + " ms");

            FastStringSet updatedSet = FastStringSet.create();

            while (currentDependencyTree.hasHorizontalConnectorToLayout()
                    || currentDependencyTree.hasVerticaConnectorToLayout()) {
                for (ManagedLayout layout : currentDependencyTree
                        .getHorizontalLayoutTargets()) {
                    if (layout instanceof DirectionalManagedLayout) {
                        currentDependencyTree
                                .markAsHorizontallyLayouted(layout);
                        DirectionalManagedLayout cl = (DirectionalManagedLayout) layout;
                        cl.layoutHorizontally();
                        countLayout(layoutCounts, cl);
                    } else {
                        currentDependencyTree
                                .markAsHorizontallyLayouted(layout);
                        currentDependencyTree.markAsVerticallyLayouted(layout);
                        SimpleManagedLayout rr = (SimpleManagedLayout) layout;
                        rr.layout();
                        countLayout(layoutCounts, rr);
                    }
                    updatedSet.add(layout.getConnectorId());
                }

                for (ManagedLayout layout : currentDependencyTree
                        .getVerticalLayoutTargets()) {
                    if (layout instanceof DirectionalManagedLayout) {
                        currentDependencyTree.markAsVerticallyLayouted(layout);
                        DirectionalManagedLayout cl = (DirectionalManagedLayout) layout;
                        cl.layoutVertically();
                        countLayout(layoutCounts, cl);
                    } else {
                        currentDependencyTree
                                .markAsHorizontallyLayouted(layout);
                        currentDependencyTree.markAsVerticallyLayouted(layout);
                        SimpleManagedLayout rr = (SimpleManagedLayout) layout;
                        rr.layout();
                        countLayout(layoutCounts, rr);
                    }
                    updatedSet.add(layout.getConnectorId());
                }
            }

            JsArrayString changed = updatedSet.dump();

            StringBuilder b = new StringBuilder("  ");
            b.append(changed.length());
            b.append(" requestLayout invocations in ");
            b.append(passDuration.elapsedMillis() - measureTime);
            b.append(" ms");
            if (changed.length() < 30) {
                for (int i = 0; i < changed.length(); i++) {
                    if (i != 0) {
                        b.append(", ");
                    } else {
                        b.append(": ");
                    }
                    String connectorString = changed.get(i);
                    if (changed.length() < 10) {
                        ServerConnector connector = ConnectorMap
                                .get(connection).getConnector(connectorString);
                        connectorString = Util.getConnectorString(connector);
                    }
                    b.append(connectorString);
                }
            }
            VConsole.log(b.toString());

            if (changed.length() == 0) {
                VConsole.log("No more changes in pass " + passes);
                break;
            }

            VConsole.log("Pass " + passes + " completed in "
                    + passDuration.elapsedMillis() + " ms");

            if (passes > 100) {
                VConsole.log(LOOP_ABORT_MESSAGE);
                VNotification.createNotification(VNotification.DELAY_FOREVER)
                        .show(LOOP_ABORT_MESSAGE, VNotification.CENTERED,
                                "error");
                break;
            }
        }

        int postLayoutStart = totalDuration.elapsedMillis();
        for (ComponentConnector connector : connection.getConnectorMap()
                .getComponentConnectors()) {
            if (connector instanceof PostLayoutListener) {
                ((PostLayoutListener) connector).postLayout();
            }
        }
        VConsole.log("Invoke post layout listeners in "
                + (totalDuration.elapsedMillis() - postLayoutStart) + " ms");

        currentDependencyTree = null;
        VConsole.log("Total layout phase time: "
                + totalDuration.elapsedMillis() + "ms");
    }

    private void logConnectorStatus(int connectorId) {
        currentDependencyTree
                .logDependencyStatus((ComponentConnector) ConnectorMap.get(
                        connection).getConnector(Integer.toString(connectorId)));
    }

    private int measureConnectors(LayoutDependencyTree layoutDependencyTree,
            boolean measureAll) {
        if (!pendingOverflowFixes.isEmpty()) {
            Duration duration = new Duration();
            for (ComponentConnector componentConnector : pendingOverflowFixes) {
                componentConnector.getWidget().getElement().getParentElement()
                        .getStyle().setTop(1, Unit.PX);
            }
            for (ComponentConnector componentConnector : pendingOverflowFixes) {
                componentConnector.getWidget().getElement().getParentElement()
                        .getOffsetHeight();
            }
            for (ComponentConnector componentConnector : pendingOverflowFixes) {
                componentConnector.getWidget().getElement().getParentElement()
                        .getStyle().setTop(0, Unit.PX);
                layoutDependencyTree.setNeedsMeasure(componentConnector, true);
                ComponentContainerConnector parent = componentConnector
                        .getParent();
                if (parent instanceof ManagedLayout) {
                    ManagedLayout managedParent = (ManagedLayout) parent;
                    layoutDependencyTree.setNeedsHorizontalLayout(
                            managedParent, true);
                    layoutDependencyTree.setNeedsVerticalLayout(managedParent,
                            true);
                }
            }
            VConsole.log("Did overflow fix for " + pendingOverflowFixes.size()
                    + " elements  in " + duration.elapsedMillis() + " ms");
            pendingOverflowFixes.clear();
        }

        int measureCount = 0;
        if (measureAll) {
            ComponentConnector[] connectors = ConnectorMap.get(connection)
                    .getComponentConnectors();
            for (ComponentConnector connector : connectors) {
                measueConnector(layoutDependencyTree, connector);
            }
            for (ComponentConnector connector : connectors) {
                layoutDependencyTree.setNeedsMeasure(connector, false);
            }
            measureCount += connectors.length;
        }

        while (layoutDependencyTree.hasConnectorsToMeasure()) {
            Collection<ComponentConnector> measureTargets = layoutDependencyTree
                    .getMeasureTargets();
            for (ComponentConnector connector : measureTargets) {
                measueConnector(layoutDependencyTree, connector);
                measureCount++;
            }
            for (ComponentConnector connector : measureTargets) {
                layoutDependencyTree.setNeedsMeasure(connector, false);
            }
        }
        return measureCount;
    }

    private void measueConnector(LayoutDependencyTree layoutDependencyTree,
            ComponentConnector connector) {
        Element element = connector.getWidget().getElement();
        MeasuredSize measuredSize = getMeasuredSize(connector);
        MeasureResult measureResult = measuredAndUpdate(element, measuredSize,
                layoutDependencyTree);

        if (measureResult.isChanged()) {
            doOverflowAutoFix(connector);
        }
        if (measureResult.isHeightChanged()) {
            layoutDependencyTree.markHeightAsChanged(connector);
        }
        if (measureResult.isWidthChanged()) {
            layoutDependencyTree.markWidthAsChanged(connector);
        }
    }

    private void doOverflowAutoFix(ComponentConnector connector) {
        if (connector.getParent() instanceof RequiresOverflowAutoFix
                && BrowserInfo.get().requiresOverflowAutoFix()
                && !"absolute".equals(connector.getWidget().getElement()
                        .getStyle().getPosition())) {
            pendingOverflowFixes.add(connector);
        }
    }

    private void measureNonPaintables(LayoutDependencyTree layoutDependencyTree) {
        for (Element element : nonPaintableElements) {
            MeasuredSize measuredSize = getMeasuredSize(element, null);
            measuredAndUpdate(element, measuredSize, layoutDependencyTree);
        }
        VConsole.log("Measured " + nonPaintableElements.size()
                + " non paintable elements");
    }

    private MeasureResult measuredAndUpdate(Element element,
            MeasuredSize measuredSize, LayoutDependencyTree layoutDependencyTree) {
        MeasureResult measureResult = measuredSize.measure(element);
        if (measureResult.isChanged()) {
            JsArrayString dependents = measuredSize.getDependents();
            for (int i = 0; i < dependents.length(); i++) {
                String pid = dependents.get(i);
                ManagedLayout dependent = (ManagedLayout) connection
                        .getConnectorMap().getConnector(pid);
                if (dependent != null) {
                    if (measureResult.isHeightChanged()) {
                        layoutDependencyTree.setNeedsVerticalLayout(dependent,
                                true);
                    }
                    if (measureResult.isWidthChanged()) {
                        layoutDependencyTree.setNeedsHorizontalLayout(
                                dependent, true);
                    }
                }
            }
        }
        return measureResult;
    }

    private static boolean isManagedLayout(ComponentConnector paintable) {
        return paintable instanceof ManagedLayout;
    }

    public void forceLayout() {
        ConnectorMap paintableMap = connection.getConnectorMap();
        ComponentConnector[] paintableWidgets = paintableMap
                .getComponentConnectors();
        for (ComponentConnector connector : paintableWidgets) {
            if (connector instanceof ManagedLayout) {
                setNeedsUpdate((ManagedLayout) connector);
            }
        }
        doLayout();
    }

    // TODO Rename to setNeedsLayout
    public final void setNeedsUpdate(ManagedLayout layout) {
        setWidthNeedsUpdate(layout);
        setHeightNeedsUpdate(layout);
    }

    public final void setWidthNeedsUpdate(ManagedLayout layout) {
        needsHorizontalLayout.add(layout);
    }

    public final void setHeightNeedsUpdate(ManagedLayout layout) {
        needsVerticalLayout.add(layout);
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

    public int getPaddingBottom(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingBottom();
    }

    public int getPaddingRight(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingRight();
    }

    public int getMarginTop(Element element) {
        return getMeasuredSize(element, nullSize).getMarginTop();
    }

    public int getMarginRight(Element element) {
        return getMeasuredSize(element, nullSize).getMarginRight();
    }

    public int getMarginBottom(Element element) {
        return getMeasuredSize(element, nullSize).getMarginBottom();
    }

    public int getMarginLeft(Element element) {
        return getMeasuredSize(element, nullSize).getMarginLeft();
    }

    public void reportOuterHeight(ComponentConnector component, int outerHeight) {
        if (!isLayoutRunning()) {
            throw new IllegalStateException(
                    "Can only report sizes when layout is running");
        }
        MeasuredSize measuredSize = getMeasuredSize(component);
        boolean heightChanged = measuredSize.setOuterHeight(outerHeight);

        if (heightChanged) {
            currentDependencyTree.markHeightAsChanged(component);
            doOverflowAutoFix(component);
        }
        currentDependencyTree.setNeedsVerticalMeasure(component, false);
    }

    public void reportHeightAssignedToRelative(ComponentConnector component,
            int assignedHeight) {
        assert component.isRelativeHeight();

        float percentSize = parsePercent(component.getState().getHeight());
        int effectiveHeight = Math.round(assignedHeight * (percentSize / 100));

        reportOuterHeight(component, effectiveHeight);
    }

    public void reportWidthAssignedToRelative(ComponentConnector component,
            int assignedWidth) {
        assert component.isRelativeWidth();

        float percentSize = parsePercent(component.getState().getWidth());
        int effectiveWidth = Math.round(assignedWidth * (percentSize / 100));

        reportOuterWidth(component, effectiveWidth);
    }

    private static float parsePercent(String size) {
        return Float.parseFloat(size.substring(0, size.length() - 1));
    }

    public void reportOuterWidth(ComponentConnector component, int outerWidth) {
        if (!isLayoutRunning()) {
            throw new IllegalStateException(
                    "Can only report sizes when layout is running");
        }

        MeasuredSize measuredSize = getMeasuredSize(component);
        boolean widthChanged = measuredSize.setOuterWidth(outerWidth);

        if (widthChanged) {
            currentDependencyTree.markWidthAsChanged(component);
            doOverflowAutoFix(component);
        }
        currentDependencyTree.setNeedsHorizontalMeasure(component, false);
    }
}
