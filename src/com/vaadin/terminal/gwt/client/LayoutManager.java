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
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.Timer;
import com.vaadin.terminal.gwt.client.MeasuredSize.MeasureResult;
import com.vaadin.terminal.gwt.client.ui.ManagedLayout;
import com.vaadin.terminal.gwt.client.ui.PostLayoutListener;
import com.vaadin.terminal.gwt.client.ui.SimpleManagedLayout;
import com.vaadin.terminal.gwt.client.ui.VNotification;
import com.vaadin.terminal.gwt.client.ui.layout.ElementResizeEvent;
import com.vaadin.terminal.gwt.client.ui.layout.ElementResizeListener;
import com.vaadin.terminal.gwt.client.ui.layout.LayoutDependencyTree;

public class LayoutManager {
    private static final String LOOP_ABORT_MESSAGE = "Aborting layout after 100 passes. This would probably be an infinite loop.";

    private static final boolean debugLogging = false;

    private ApplicationConnection connection;
    private final Set<Element> measuredNonPaintableElements = new HashSet<Element>();
    private final MeasuredSize nullSize = new MeasuredSize();

    private LayoutDependencyTree currentDependencyTree;

    private final Collection<ManagedLayout> needsHorizontalLayout = new HashSet<ManagedLayout>();
    private final Collection<ManagedLayout> needsVerticalLayout = new HashSet<ManagedLayout>();

    private final Collection<ComponentConnector> needsMeasure = new HashSet<ComponentConnector>();

    private Collection<ComponentConnector> pendingOverflowFixes = new HashSet<ComponentConnector>();

    private final Map<Element, Collection<ElementResizeListener>> elementResizeListeners = new HashMap<Element, Collection<ElementResizeListener>>();
    private final Set<Element> listenersToFire = new HashSet<Element>();

    private boolean layoutPending = false;
    private Timer layoutTimer = new Timer() {
        @Override
        public void run() {
            cancel();
            layoutNow();
        }
    };
    private boolean everythingNeedsMeasure = false;

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
                measuredNonPaintableElements.add(element);
            }
            setMeasuredSize(element, measuredSize);
        }
        return measuredSize;
    }

    private boolean needsMeasure(Element e) {
        if (connection.getConnectorMap().getConnectorId(e) != null) {
            return true;
        } else if (elementResizeListeners.containsKey(e)) {
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

    private final MeasuredSize getMeasuredSize(ComponentConnector connector) {
        Element element = connector.getWidget().getElement();
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
        stopMeasuringIfUnecessary(element);
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

    private void layoutLater() {
        if (!layoutPending) {
            layoutPending = true;
            layoutTimer.schedule(100);
        }
    }

    public void layoutNow() {
        if (isLayoutRunning()) {
            throw new IllegalStateException(
                    "Can't start a new layout phase before the previous layout phase ends.");
        }
        layoutPending = false;
        try {
            currentDependencyTree = new LayoutDependencyTree();
            doLayout();
        } finally {
            currentDependencyTree = null;
        }
    }

    private void doLayout() {
        VConsole.log("Starting layout phase");

        Map<ManagedLayout, Integer> layoutCounts = new HashMap<ManagedLayout, Integer>();

        int passes = 0;
        Duration totalDuration = new Duration();

        for (ManagedLayout layout : needsHorizontalLayout) {
            currentDependencyTree.setNeedsHorizontalLayout(layout, true);
        }
        for (ManagedLayout layout : needsVerticalLayout) {
            currentDependencyTree.setNeedsVerticalLayout(layout, true);
        }
        needsHorizontalLayout.clear();
        needsVerticalLayout.clear();

        for (ComponentConnector connector : needsMeasure) {
            currentDependencyTree.setNeedsMeasure(connector, true);
        }
        needsMeasure.clear();

        measureNonPaintables();

        VConsole.log("Layout init in " + totalDuration.elapsedMillis() + " ms");

        while (true) {
            Duration passDuration = new Duration();
            passes++;

            int measuredConnectorCount = measureConnectors(
                    currentDependencyTree, everythingNeedsMeasure);
            everythingNeedsMeasure = false;
            if (measuredConnectorCount == 0) {
                VConsole.log("No more changes in pass " + passes);
                break;
            }

            int measureTime = passDuration.elapsedMillis();
            VConsole.log("  Measured " + measuredConnectorCount
                    + " elements in " + measureTime + " ms");

            if (!listenersToFire.isEmpty()) {
                for (Element element : listenersToFire) {
                    Collection<ElementResizeListener> listeners = elementResizeListeners
                            .get(element);
                    ElementResizeListener[] array = listeners
                            .toArray(new ElementResizeListener[listeners.size()]);
                    ElementResizeEvent event = new ElementResizeEvent(this,
                            element);
                    for (ElementResizeListener listener : array) {
                        listener.onElementResize(event);
                    }
                }
                int measureListenerTime = passDuration.elapsedMillis();
                VConsole.log("  Fired resize listeners for  "
                        + listenersToFire.size() + " elements in "
                        + (measureListenerTime - measureTime) + " ms");
                measureTime = measuredConnectorCount;
                listenersToFire.clear();
            }

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
                    if (debugLogging) {
                        updatedSet.add(layout.getConnectorId());
                    }
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
                    if (debugLogging) {
                        updatedSet.add(layout.getConnectorId());
                    }
                }
            }

            if (debugLogging) {
                JsArrayString changedCids = updatedSet.dump();

                StringBuilder b = new StringBuilder("  ");
                b.append(changedCids.length());
                b.append(" requestLayout invocations in ");
                b.append(passDuration.elapsedMillis() - measureTime);
                b.append(" ms");
                if (changedCids.length() < 30) {
                    for (int i = 0; i < changedCids.length(); i++) {
                        if (i != 0) {
                            b.append(", ");
                        } else {
                            b.append(": ");
                        }
                        String connectorString = changedCids.get(i);
                        if (changedCids.length() < 10) {
                            ServerConnector connector = ConnectorMap.get(
                                    connection).getConnector(connectorString);
                            connectorString = Util
                                    .getConnectorString(connector);
                        }
                        b.append(connectorString);
                    }
                }
                VConsole.log(b.toString());
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

            HashMap<Element, String> originalOverflows = new HashMap<Element, String>();

            HashSet<ComponentConnector> delayedOverflowFixes = new HashSet<ComponentConnector>();

            // First set overflow to hidden (and save previous value so it can
            // be restored later)
            for (ComponentConnector componentConnector : pendingOverflowFixes) {
                // Delay the overflow fix if the involved connectors might still
                // change
                if (!currentDependencyTree
                        .noMoreChangesExpected(componentConnector)
                        || !currentDependencyTree
                                .noMoreChangesExpected(componentConnector
                                        .getParent())) {
                    delayedOverflowFixes.add(componentConnector);
                    continue;
                }

                if (debugLogging) {
                    VConsole.log("Doing overflow fix for "
                            + Util.getConnectorString(componentConnector)
                            + " in "
                            + Util.getConnectorString(componentConnector
                                    .getParent()));
                }

                Element parentElement = componentConnector.getWidget()
                        .getElement().getParentElement();
                Style style = parentElement.getStyle();
                String originalOverflow = style.getOverflow();

                if (originalOverflow != null
                        && !originalOverflows.containsKey(parentElement)) {
                    // Store original value for restore, but only the first time
                    // the value is changed
                    originalOverflows.put(parentElement, originalOverflow);
                }

                style.setOverflow(Overflow.HIDDEN);
            }

            pendingOverflowFixes.removeAll(delayedOverflowFixes);

            // Then ensure all scrolling elements are reflowed by measuring
            for (ComponentConnector componentConnector : pendingOverflowFixes) {
                componentConnector.getWidget().getElement().getParentElement()
                        .getOffsetHeight();
            }

            // Finally restore old overflow value and update bookkeeping
            for (ComponentConnector componentConnector : pendingOverflowFixes) {
                Element parentElement = componentConnector.getWidget()
                        .getElement().getParentElement();
                parentElement.getStyle().setProperty("overflow",
                        originalOverflows.get(parentElement));

                layoutDependencyTree.setNeedsMeasure(componentConnector, true);
            }
            if (!pendingOverflowFixes.isEmpty()) {
                VConsole.log("Did overflow fix for "
                        + pendingOverflowFixes.size() + " elements  in "
                        + duration.elapsedMillis() + " ms");
            }
            pendingOverflowFixes = delayedOverflowFixes;
        }

        int measureCount = 0;
        if (measureAll) {
            ComponentConnector[] connectors = ConnectorMap.get(connection)
                    .getComponentConnectors();
            for (ComponentConnector connector : connectors) {
                measueConnector(connector);
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
                measueConnector(connector);
                measureCount++;
            }
            for (ComponentConnector connector : measureTargets) {
                layoutDependencyTree.setNeedsMeasure(connector, false);
            }
        }
        return measureCount;
    }

    private void measueConnector(ComponentConnector connector) {
        Element element = connector.getWidget().getElement();
        MeasuredSize measuredSize = getMeasuredSize(connector);
        MeasureResult measureResult = measuredAndUpdate(element, measuredSize);

        if (measureResult.isChanged()) {
            onConnectorChange(connector, measureResult.isWidthChanged(),
                    measureResult.isHeightChanged());
        }
    }

    private void onConnectorChange(ComponentConnector connector,
            boolean widthChanged, boolean heightChanged) {
        setNeedsOverflowFix(connector);
        if (heightChanged) {
            currentDependencyTree.markHeightAsChanged(connector);
        }
        if (widthChanged) {
            currentDependencyTree.markWidthAsChanged(connector);
        }
    }

    private void setNeedsOverflowFix(ComponentConnector connector) {
        // IE9 doesn't need the original fix, but for some reason it needs this
        if (BrowserInfo.get().requiresOverflowAutoFix()
                || BrowserInfo.get().isIE9()) {
            ComponentConnector scrollingBoundary = currentDependencyTree
                    .getScrollingBoundary(connector);
            if (scrollingBoundary != null) {
                pendingOverflowFixes.add(scrollingBoundary);
            }
        }
    }

    private void measureNonPaintables() {
        for (Element element : measuredNonPaintableElements) {
            measuredAndUpdate(element, getMeasuredSize(element, null));
        }
        VConsole.log("Measured " + measuredNonPaintableElements.size()
                + " non paintable elements");
    }

    private MeasureResult measuredAndUpdate(Element element,
            MeasuredSize measuredSize) {
        MeasureResult measureResult = measuredSize.measure(element);
        if (measureResult.isChanged()) {
            notifyListenersAndDepdendents(element,
                    measureResult.isWidthChanged(),
                    measureResult.isHeightChanged());
        }
        return measureResult;
    }

    private void notifyListenersAndDepdendents(Element element,
            boolean widthChanged, boolean heightChanged) {
        assert widthChanged || heightChanged;

        MeasuredSize measuredSize = getMeasuredSize(element, nullSize);
        JsArrayString dependents = measuredSize.getDependents();
        for (int i = 0; i < dependents.length(); i++) {
            String pid = dependents.get(i);
            ManagedLayout dependent = (ManagedLayout) connection
                    .getConnectorMap().getConnector(pid);
            if (dependent != null) {
                if (heightChanged) {
                    currentDependencyTree.setNeedsVerticalLayout(dependent,
                            true);
                }
                if (widthChanged) {
                    currentDependencyTree.setNeedsHorizontalLayout(dependent,
                            true);
                }
            }
        }
        if (elementResizeListeners.containsKey(element)) {
            listenersToFire.add(element);
        }
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
        setEverythingNeedsMeasure();
        layoutNow();
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
        MeasuredSize measuredSize = getMeasuredSize(component);
        if (isLayoutRunning()) {
            boolean heightChanged = measuredSize.setOuterHeight(outerHeight);

            if (heightChanged) {
                onConnectorChange(component, false, true);
                notifyListenersAndDepdendents(component.getWidget()
                        .getElement(), false, true);
            }
            currentDependencyTree.setNeedsVerticalMeasure(component, false);
        } else if (measuredSize.getOuterHeight() != outerHeight) {
            setNeedsMeasure(component);
        }
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
        MeasuredSize measuredSize = getMeasuredSize(component);
        if (isLayoutRunning()) {
            boolean widthChanged = measuredSize.setOuterWidth(outerWidth);

            if (widthChanged) {
                onConnectorChange(component, true, false);
                notifyListenersAndDepdendents(component.getWidget()
                        .getElement(), true, false);
            }
            currentDependencyTree.setNeedsHorizontalMeasure(component, false);
        } else if (measuredSize.getOuterWidth() != outerWidth) {
            setNeedsMeasure(component);
        }
    }

    public void addElementResizeListener(Element element,
            ElementResizeListener listener) {
        Collection<ElementResizeListener> listeners = elementResizeListeners
                .get(element);
        if (listeners == null) {
            listeners = new HashSet<ElementResizeListener>();
            elementResizeListeners.put(element, listeners);
            ensureMeasured(element);
        }
        listeners.add(listener);
    }

    public void removeElementResizeListener(Element element,
            ElementResizeListener listener) {
        Collection<ElementResizeListener> listeners = elementResizeListeners
                .get(element);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                elementResizeListeners.remove(element);
                stopMeasuringIfUnecessary(element);
            }
        }
    }

    private void stopMeasuringIfUnecessary(Element element) {
        if (!needsMeasure(element)) {
            measuredNonPaintableElements.remove(element);
            setMeasuredSize(element, null);
        }
    }

    public void setNeedsMeasure(ComponentConnector component) {
        if (isLayoutRunning()) {
            currentDependencyTree.setNeedsMeasure(component, true);
        } else {
            needsMeasure.add(component);
            layoutLater();
        }
    }

    public void setEverythingNeedsMeasure() {
        everythingNeedsMeasure = true;
    }
}
