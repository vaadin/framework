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
import com.vaadin.terminal.gwt.client.ui.layout.ElementResizeEvent;
import com.vaadin.terminal.gwt.client.ui.layout.ElementResizeListener;
import com.vaadin.terminal.gwt.client.ui.layout.LayoutDependencyTree;
import com.vaadin.terminal.gwt.client.ui.notification.VNotification;

public class LayoutManager {
    private static final String LOOP_ABORT_MESSAGE = "Aborting layout after 100 passes. This would probably be an infinite loop.";

    private static final boolean debugLogging = false;

    private ApplicationConnection connection;
    private final Set<Element> measuredNonConnectorElements = new HashSet<Element>();
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

    /**
     * Gets the layout manager associated with the given
     * {@link ApplicationConnection}.
     * 
     * @param connection
     *            the application connection to get a layout manager for
     * @return the layout manager associated with the provided application
     *         connection
     */
    public static LayoutManager get(ApplicationConnection connection) {
        return connection.getLayoutManager();
    }

    /**
     * Registers that a ManagedLayout is depending on the size of an Element.
     * This causes this layout manager to measure the element in the beginning
     * of every layout phase and call the appropriate layout method of the
     * managed layout if the size of the element has changed.
     * 
     * @param owner
     *            the ManagedLayout that depends on an element
     * @param element
     *            the Element that should be measured
     */
    public void registerDependency(ManagedLayout owner, Element element) {
        MeasuredSize measuredSize = ensureMeasured(element);
        setNeedsLayout(owner);
        measuredSize.addDependent(owner.getConnectorId());
    }

    private MeasuredSize ensureMeasured(Element element) {
        MeasuredSize measuredSize = getMeasuredSize(element, null);
        if (measuredSize == null) {
            measuredSize = new MeasuredSize();

            if (ConnectorMap.get(connection).getConnector(element) == null) {
                measuredNonConnectorElements.add(element);
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

    /**
     * Assigns a measured size to an element. Method defined as protected to
     * allow separate implementation for IE8 in which delete not always works.
     * 
     * @param element
     *            the dom element to attach the measured size to
     * @param measuredSize
     *            the measured size to attach to the element. If
     *            <code>null</code>, any previous measured size is removed.
     */
    protected native void setMeasuredSize(Element element,
            MeasuredSize measuredSize)
    /*-{
        if (measuredSize) {
            element.vMeasuredSize = measuredSize;
        } else {
            delete element.vMeasuredSize;
        }
    }-*/;

    /**
     * Get the measured size of the given element. If no size is set, use the
     * default size instead.
     * 
     * Method defined as protected to allow separate implementation for IE8
     * (performance reason: storing any data in the DOM causes a reflow).
     * 
     * @param element
     *            the dom element whose measured size to get
     * @param defaultSize
     *            a fallback size if the element doesn't have a measured size
     *            stored
     * @return
     */
    protected native MeasuredSize getMeasuredSize(Element element,
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

    /**
     * Registers that a ManagedLayout is no longer depending on the size of an
     * Element.
     * 
     * @see #registerDependency(ManagedLayout, Element)
     * 
     * @param owner
     *            the ManagedLayout no longer depends on an element
     * @param element
     *            the Element that that no longer needs to be measured
     */
    public void unregisterDependency(ManagedLayout owner, Element element) {
        MeasuredSize measuredSize = getMeasuredSize(element, null);
        if (measuredSize == null) {
            return;
        }
        measuredSize.removeDependent(owner.getConnectorId());
        stopMeasuringIfUnnecessary(element);
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

    public void layoutLater() {
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

        measureNonConnectors();

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

            VConsole.log("  Total of " + measureCount
                    + " measurement operations");

            if (!listenersToFire.isEmpty()) {
                for (Element element : listenersToFire) {
                    Collection<ElementResizeListener> listeners = elementResizeListeners
                            .get(element);
                    if (listeners == null) {
                        continue;
                    }
                    ElementResizeListener[] array = listeners
                            .toArray(new ElementResizeListener[listeners.size()]);
                    ElementResizeEvent event = new ElementResizeEvent(this,
                            element);
                    for (ElementResizeListener listener : array) {
                        try {
                            listener.onElementResize(event);
                        } catch (RuntimeException e) {
                            VConsole.error(e);
                        }
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
                        try {
                            cl.layoutHorizontally();
                        } catch (RuntimeException e) {
                            VConsole.log(e);
                        }
                        countLayout(layoutCounts, cl);
                    } else {
                        currentDependencyTree
                                .markAsHorizontallyLayouted(layout);
                        currentDependencyTree.markAsVerticallyLayouted(layout);
                        SimpleManagedLayout rr = (SimpleManagedLayout) layout;
                        try {
                            rr.layout();
                        } catch (RuntimeException e) {
                            VConsole.log(e);
                        }
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
                        try {
                            cl.layoutVertically();
                        } catch (RuntimeException e) {
                            VConsole.log(e);
                        }
                        countLayout(layoutCounts, cl);
                    } else {
                        currentDependencyTree
                                .markAsHorizontallyLayouted(layout);
                        currentDependencyTree.markAsVerticallyLayouted(layout);
                        SimpleManagedLayout rr = (SimpleManagedLayout) layout;
                        try {
                            rr.layout();
                        } catch (RuntimeException e) {
                            VConsole.log(e);
                        }
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
                measureConnector(connector);
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
                measureConnector(connector);
                measureCount++;
            }
            for (ComponentConnector connector : measureTargets) {
                layoutDependencyTree.setNeedsMeasure(connector, false);
            }
        }
        return measureCount;
    }

    private void measureConnector(ComponentConnector connector) {
        MeasuredSize measuredSize = getMeasuredSize(connector);
        if (!isManagedLayout(connector)
                && !isManagedLayout(connector.getParent())
                && elementResizeListeners.get(connector.getWidget()
                        .getElement()) == null && !measuredSize.hasDependents()) {
            return;
        }

        Element element = connector.getWidget().getElement();
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

    private void measureNonConnectors() {
        for (Element element : measuredNonConnectorElements) {
            measuredAndUpdate(element, getMeasuredSize(element, null));
        }
        VConsole.log("Measured " + measuredNonConnectorElements.size()
                + " non connector elements");
    }

    int measureCount = 0;

    private MeasureResult measuredAndUpdate(Element element,
            MeasuredSize measuredSize) {
        measureCount++;
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

    private static boolean isManagedLayout(ComponentConnector connector) {
        return connector instanceof ManagedLayout;
    }

    public void forceLayout() {
        ConnectorMap connectorMap = connection.getConnectorMap();
        ComponentConnector[] componentConnectors = connectorMap
                .getComponentConnectors();
        for (ComponentConnector connector : componentConnectors) {
            if (connector instanceof ManagedLayout) {
                setNeedsLayout((ManagedLayout) connector);
            }
        }
        setEverythingNeedsMeasure();
        layoutNow();
    }

    /**
     * Marks that a ManagedLayout should be layouted in the next layout phase
     * even if none of the elements managed by the layout have been resized.
     * 
     * @param layout
     *            the managed layout that should be layouted
     */
    public final void setNeedsLayout(ManagedLayout layout) {
        setNeedsHorizontalLayout(layout);
        setNeedsVerticalLayout(layout);
    }

    /**
     * Marks that a ManagedLayout should be layouted horizontally in the next
     * layout phase even if none of the elements managed by the layout have been
     * resized horizontally.
     * 
     * For SimpleManagedLayout which is always layouted in both directions, this
     * has the same effect as {@link #setNeedsLayout(ManagedLayout)}.
     * 
     * @param layout
     *            the managed layout that should be layouted
     */
    public final void setNeedsHorizontalLayout(ManagedLayout layout) {
        needsHorizontalLayout.add(layout);
    }

    /**
     * Marks that a ManagedLayout should be layouted vertically in the next
     * layout phase even if none of the elements managed by the layout have been
     * resized vertically.
     * 
     * For SimpleManagedLayout which is always layouted in both directions, this
     * has the same effect as {@link #setNeedsLayout(ManagedLayout)}.
     * 
     * @param layout
     *            the managed layout that should be layouted
     */
    public final void setNeedsVerticalLayout(ManagedLayout layout) {
        needsVerticalLayout.add(layout);
    }

    /**
     * Gets the outer height (including margins, paddings and borders) of the
     * given element, provided that it has been measured. These elements are
     * guaranteed to be measured:
     * <ul>
     * <li>ManagedLayotus and their child Connectors
     * <li>Elements for which there is at least one ElementResizeListener
     * <li>Elements for which at least one ManagedLayout has registered a
     * dependency
     * </ul>
     * 
     * -1 is returned if the element has not been measured. If 0 is returned, it
     * might indicate that the element is not attached to the DOM.
     * 
     * @param element
     *            the element to get the measured size for
     * @return the measured outer height (including margins, paddings and
     *         borders) of the element in pixels.
     */
    public final int getOuterHeight(Element element) {
        return getMeasuredSize(element, nullSize).getOuterHeight();
    }

    /**
     * Gets the outer width (including margins, paddings and borders) of the
     * given element, provided that it has been measured. These elements are
     * guaranteed to be measured:
     * <ul>
     * <li>ManagedLayotus and their child Connectors
     * <li>Elements for which there is at least one ElementResizeListener
     * <li>Elements for which at least one ManagedLayout has registered a
     * dependency
     * </ul>
     * 
     * -1 is returned if the element has not been measured. If 0 is returned, it
     * might indicate that the element is not attached to the DOM.
     * 
     * @param element
     *            the element to get the measured size for
     * @return the measured outer width (including margins, paddings and
     *         borders) of the element in pixels.
     */
    public final int getOuterWidth(Element element) {
        return getMeasuredSize(element, nullSize).getOuterWidth();
    }

    /**
     * Gets the inner height (excluding margins, paddings and borders) of the
     * given element, provided that it has been measured. These elements are
     * guaranteed to be measured:
     * <ul>
     * <li>ManagedLayotus and their child Connectors
     * <li>Elements for which there is at least one ElementResizeListener
     * <li>Elements for which at least one ManagedLayout has registered a
     * dependency
     * </ul>
     * 
     * -1 is returned if the element has not been measured. If 0 is returned, it
     * might indicate that the element is not attached to the DOM.
     * 
     * @param element
     *            the element to get the measured size for
     * @return the measured inner height (excluding margins, paddings and
     *         borders) of the element in pixels.
     */
    public final int getInnerHeight(Element element) {
        return getMeasuredSize(element, nullSize).getInnerHeight();
    }

    /**
     * Gets the inner width (excluding margins, paddings and borders) of the
     * given element, provided that it has been measured. These elements are
     * guaranteed to be measured:
     * <ul>
     * <li>ManagedLayotus and their child Connectors
     * <li>Elements for which there is at least one ElementResizeListener
     * <li>Elements for which at least one ManagedLayout has registered a
     * dependency
     * </ul>
     * 
     * -1 is returned if the element has not been measured. If 0 is returned, it
     * might indicate that the element is not attached to the DOM.
     * 
     * @param element
     *            the element to get the measured size for
     * @return the measured inner width (excluding margins, paddings and
     *         borders) of the element in pixels.
     */
    public final int getInnerWidth(Element element) {
        return getMeasuredSize(element, nullSize).getInnerWidth();
    }

    /**
     * Gets the border height (top border + bottom border) of the given element,
     * provided that it has been measured. These elements are guaranteed to be
     * measured:
     * <ul>
     * <li>ManagedLayotus and their child Connectors
     * <li>Elements for which there is at least one ElementResizeListener
     * <li>Elements for which at least one ManagedLayout has registered a
     * dependency
     * </ul>
     * 
     * A negative number is returned if the element has not been measured. If 0
     * is returned, it might indicate that the element is not attached to the
     * DOM.
     * 
     * @param element
     *            the element to get the measured size for
     * @return the measured border height (top border + bottom border) of the
     *         element in pixels.
     */
    public final int getBorderHeight(Element element) {
        return getMeasuredSize(element, nullSize).getBorderHeight();
    }

    /**
     * Gets the padding height (top padding + bottom padding) of the given
     * element, provided that it has been measured. These elements are
     * guaranteed to be measured:
     * <ul>
     * <li>ManagedLayotus and their child Connectors
     * <li>Elements for which there is at least one ElementResizeListener
     * <li>Elements for which at least one ManagedLayout has registered a
     * dependency
     * </ul>
     * 
     * A negative number is returned if the element has not been measured. If 0
     * is returned, it might indicate that the element is not attached to the
     * DOM.
     * 
     * @param element
     *            the element to get the measured size for
     * @return the measured padding height (top padding + bottom padding) of the
     *         element in pixels.
     */
    public int getPaddingHeight(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingHeight();
    }

    /**
     * Gets the border width (left border + right border) of the given element,
     * provided that it has been measured. These elements are guaranteed to be
     * measured:
     * <ul>
     * <li>ManagedLayotus and their child Connectors
     * <li>Elements for which there is at least one ElementResizeListener
     * <li>Elements for which at least one ManagedLayout has registered a
     * dependency
     * </ul>
     * 
     * A negative number is returned if the element has not been measured. If 0
     * is returned, it might indicate that the element is not attached to the
     * DOM.
     * 
     * @param element
     *            the element to get the measured size for
     * @return the measured border width (left border + right border) of the
     *         element in pixels.
     */
    public int getBorderWidth(Element element) {
        return getMeasuredSize(element, nullSize).getBorderWidth();
    }

    /**
     * Gets the padding width (left padding + right padding) of the given
     * element, provided that it has been measured. These elements are
     * guaranteed to be measured:
     * <ul>
     * <li>ManagedLayotus and their child Connectors
     * <li>Elements for which there is at least one ElementResizeListener
     * <li>Elements for which at least one ManagedLayout has registered a
     * dependency
     * </ul>
     * 
     * A negative number is returned if the element has not been measured. If 0
     * is returned, it might indicate that the element is not attached to the
     * DOM.
     * 
     * @param element
     *            the element to get the measured size for
     * @return the measured padding width (left padding + right padding) of the
     *         element in pixels.
     */
    public int getPaddingWidth(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingWidth();
    }

    /**
     * Gets the top padding of the given element, provided that it has been
     * measured. These elements are guaranteed to be measured:
     * <ul>
     * <li>ManagedLayotus and their child Connectors
     * <li>Elements for which there is at least one ElementResizeListener
     * <li>Elements for which at least one ManagedLayout has registered a
     * dependency
     * </ul>
     * 
     * A negative number is returned if the element has not been measured. If 0
     * is returned, it might indicate that the element is not attached to the
     * DOM.
     * 
     * @param element
     *            the element to get the measured size for
     * @return the measured top padding of the element in pixels.
     */
    public int getPaddingTop(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingTop();
    }

    /**
     * Gets the left padding of the given element, provided that it has been
     * measured. These elements are guaranteed to be measured:
     * <ul>
     * <li>ManagedLayotus and their child Connectors
     * <li>Elements for which there is at least one ElementResizeListener
     * <li>Elements for which at least one ManagedLayout has registered a
     * dependency
     * </ul>
     * 
     * A negative number is returned if the element has not been measured. If 0
     * is returned, it might indicate that the element is not attached to the
     * DOM.
     * 
     * @param element
     *            the element to get the measured size for
     * @return the measured left padding of the element in pixels.
     */
    public int getPaddingLeft(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingLeft();
    }

    /**
     * Gets the bottom padding of the given element, provided that it has been
     * measured. These elements are guaranteed to be measured:
     * <ul>
     * <li>ManagedLayotus and their child Connectors
     * <li>Elements for which there is at least one ElementResizeListener
     * <li>Elements for which at least one ManagedLayout has registered a
     * dependency
     * </ul>
     * 
     * A negative number is returned if the element has not been measured. If 0
     * is returned, it might indicate that the element is not attached to the
     * DOM.
     * 
     * @param element
     *            the element to get the measured size for
     * @return the measured bottom padding of the element in pixels.
     */
    public int getPaddingBottom(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingBottom();
    }

    /**
     * Gets the right padding of the given element, provided that it has been
     * measured. These elements are guaranteed to be measured:
     * <ul>
     * <li>ManagedLayotus and their child Connectors
     * <li>Elements for which there is at least one ElementResizeListener
     * <li>Elements for which at least one ManagedLayout has registered a
     * dependency
     * </ul>
     * 
     * A negative number is returned if the element has not been measured. If 0
     * is returned, it might indicate that the element is not attached to the
     * DOM.
     * 
     * @param element
     *            the element to get the measured size for
     * @return the measured right padding of the element in pixels.
     */
    public int getPaddingRight(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingRight();
    }

    /**
     * Gets the top margin of the given element, provided that it has been
     * measured. These elements are guaranteed to be measured:
     * <ul>
     * <li>ManagedLayotus and their child Connectors
     * <li>Elements for which there is at least one ElementResizeListener
     * <li>Elements for which at least one ManagedLayout has registered a
     * dependency
     * </ul>
     * 
     * A negative number is returned if the element has not been measured. If 0
     * is returned, it might indicate that the element is not attached to the
     * DOM.
     * 
     * @param element
     *            the element to get the measured size for
     * @return the measured top margin of the element in pixels.
     */
    public int getMarginTop(Element element) {
        return getMeasuredSize(element, nullSize).getMarginTop();
    }

    /**
     * Gets the right margin of the given element, provided that it has been
     * measured. These elements are guaranteed to be measured:
     * <ul>
     * <li>ManagedLayotus and their child Connectors
     * <li>Elements for which there is at least one ElementResizeListener
     * <li>Elements for which at least one ManagedLayout has registered a
     * dependency
     * </ul>
     * 
     * A negative number is returned if the element has not been measured. If 0
     * is returned, it might indicate that the element is not attached to the
     * DOM.
     * 
     * @param element
     *            the element to get the measured size for
     * @return the measured right margin of the element in pixels.
     */
    public int getMarginRight(Element element) {
        return getMeasuredSize(element, nullSize).getMarginRight();
    }

    /**
     * Gets the bottom margin of the given element, provided that it has been
     * measured. These elements are guaranteed to be measured:
     * <ul>
     * <li>ManagedLayotus and their child Connectors
     * <li>Elements for which there is at least one ElementResizeListener
     * <li>Elements for which at least one ManagedLayout has registered a
     * dependency
     * </ul>
     * 
     * A negative number is returned if the element has not been measured. If 0
     * is returned, it might indicate that the element is not attached to the
     * DOM.
     * 
     * @param element
     *            the element to get the measured size for
     * @return the measured bottom margin of the element in pixels.
     */
    public int getMarginBottom(Element element) {
        return getMeasuredSize(element, nullSize).getMarginBottom();
    }

    /**
     * Gets the left margin of the given element, provided that it has been
     * measured. These elements are guaranteed to be measured:
     * <ul>
     * <li>ManagedLayotus and their child Connectors
     * <li>Elements for which there is at least one ElementResizeListener
     * <li>Elements for which at least one ManagedLayout has registered a
     * dependency
     * </ul>
     * 
     * A negative number is returned if the element has not been measured. If 0
     * is returned, it might indicate that the element is not attached to the
     * DOM.
     * 
     * @param element
     *            the element to get the measured size for
     * @return the measured left margin of the element in pixels.
     */
    public int getMarginLeft(Element element) {
        return getMeasuredSize(element, nullSize).getMarginLeft();
    }

    public int getMarginWidth(Element element) {
        return getMeasuredSize(element, nullSize).getMarginWidth();
    }

    public int getMarginHeight(Element element) {
        return getMeasuredSize(element, nullSize).getMarginHeight();
    }

    /**
     * Registers the outer height (including margins, borders and paddings) of a
     * component. This can be used as an optimization by ManagedLayouts; by
     * informing the LayoutManager about what size a component will have, the
     * layout propagation can continue directly without first measuring the
     * potentially resized elements.
     * 
     * @param component
     *            the component for which the size is reported
     * @param outerHeight
     *            the new outer height (including margins, borders and paddings)
     *            of the component in pixels
     */
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

    /**
     * Registers the height reserved for a relatively sized component. This can
     * be used as an optimization by ManagedLayouts; by informing the
     * LayoutManager about what size a component will have, the layout
     * propagation can continue directly without first measuring the potentially
     * resized elements.
     * 
     * @param component
     *            the relatively sized component for which the size is reported
     * @param assignedHeight
     *            the inner height of the relatively sized component's parent
     *            element in pixels
     */
    public void reportHeightAssignedToRelative(ComponentConnector component,
            int assignedHeight) {
        assert component.isRelativeHeight();

        float percentSize = parsePercent(component.getState().getHeight());
        int effectiveHeight = Math.round(assignedHeight * (percentSize / 100));

        reportOuterHeight(component, effectiveHeight);
    }

    /**
     * Registers the width reserved for a relatively sized component. This can
     * be used as an optimization by ManagedLayouts; by informing the
     * LayoutManager about what size a component will have, the layout
     * propagation can continue directly without first measuring the potentially
     * resized elements.
     * 
     * @param component
     *            the relatively sized component for which the size is reported
     * @param assignedWidth
     *            the inner width of the relatively sized component's parent
     *            element in pixels
     */
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

    /**
     * Registers the outer width (including margins, borders and paddings) of a
     * component. This can be used as an optimization by ManagedLayouts; by
     * informing the LayoutManager about what size a component will have, the
     * layout propagation can continue directly without first measuring the
     * potentially resized elements.
     * 
     * @param component
     *            the component for which the size is reported
     * @param outerWidth
     *            the new outer width (including margins, borders and paddings)
     *            of the component in pixels
     */
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

    /**
     * Adds a listener that will be notified whenever the size of a specific
     * element changes. Adding a listener to an element also ensures that all
     * sizes for that element will be available starting from the next layout
     * phase.
     * 
     * @param element
     *            the element that should be checked for size changes
     * @param listener
     *            an ElementResizeListener that will be informed whenever the
     *            size of the target element has changed
     */
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

    /**
     * Removes an element resize listener from the provided element. This might
     * cause this LayoutManager to stop tracking the size of the element if no
     * other sources are interested in the size.
     * 
     * @param element
     *            the element to which the element resize listener was
     *            previously added
     * @param listener
     *            the ElementResizeListener that should no longer get informed
     *            about size changes to the target element.
     */
    public void removeElementResizeListener(Element element,
            ElementResizeListener listener) {
        Collection<ElementResizeListener> listeners = elementResizeListeners
                .get(element);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                elementResizeListeners.remove(element);
                stopMeasuringIfUnnecessary(element);
            }
        }
    }

    private void stopMeasuringIfUnnecessary(Element element) {
        if (!needsMeasure(element)) {
            measuredNonConnectorElements.remove(element);
            setMeasuredSize(element, null);
        }
    }

    /**
     * Informs this LayoutManager that the size of a component might have
     * changed. If there is no upcoming layout phase, a new layout phase is
     * scheduled. This method should be used whenever a size might have changed
     * from outside of Vaadin's normal update phase, e.g. when an icon has been
     * loaded or when the user resizes some part of the UI using the mouse.
     * 
     * @param component
     *            the component whose size might have changed.
     */
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
