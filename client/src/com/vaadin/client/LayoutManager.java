/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.MeasuredSize.MeasureResult;
import com.vaadin.client.ui.ManagedLayout;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.VNotification;
import com.vaadin.client.ui.layout.ElementResizeEvent;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.client.ui.layout.LayoutDependencyTree;

public class LayoutManager {
    private static final String LOOP_ABORT_MESSAGE = "Aborting layout after 100 passes. This would probably be an infinite loop.";

    private static final boolean debugLogging = false;

    private ApplicationConnection connection;
    private final Set<Element> measuredNonConnectorElements = new HashSet<Element>();
    private final MeasuredSize nullSize = new MeasuredSize();

    private LayoutDependencyTree currentDependencyTree;

    private FastStringSet needsHorizontalLayout = FastStringSet.create();
    private FastStringSet needsVerticalLayout = FastStringSet.create();

    private FastStringSet needsMeasure = FastStringSet.create();

    private FastStringSet pendingOverflowFixes = FastStringSet.create();

    private final Map<Element, Collection<ElementResizeListener>> elementResizeListeners = new HashMap<Element, Collection<ElementResizeListener>>();
    private final Set<Element> listenersToFire = new HashSet<Element>();

    private boolean layoutPending = false;
    private Timer layoutTimer = new Timer() {
        @Override
        public void run() {
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
     * Returns the application connection for this layout manager.
     * 
     * @return connection
     */
    protected ApplicationConnection getConnection() {
        return connection;
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
        ComponentConnector connector = connection.getConnectorMap()
                .getConnector(e);
        if (connector != null && needsMeasureForManagedLayout(connector)) {
            return true;
        } else if (elementResizeListeners.containsKey(e)) {
            return true;
        } else if (getMeasuredSize(e, nullSize).hasDependents()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean needsMeasureForManagedLayout(ComponentConnector connector) {
        if (connector instanceof ManagedLayout) {
            return true;
        } else if (connector.getParent() instanceof ManagedLayout) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Assigns a measured size to an element. Method defined as protected to
     * allow separate implementation for IE8.
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
     * Gets the measured size for an element. Method defined as protected to
     * allow separate implementation for IE8.
     * 
     * @param element
     *            The element to get measured size for
     * @param defaultSize
     *            The size to return if no measured size could be found
     * @return The measured size for the element or {@literal defaultSize}
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
        stopMeasuringIfUnecessary(element);
    }

    public boolean isLayoutRunning() {
        return currentDependencyTree != null;
    }

    private void countLayout(FastStringMap<Integer> layoutCounts,
            ManagedLayout layout) {
        Integer count = layoutCounts.get(layout.getConnectorId());
        if (count == null) {
            count = Integer.valueOf(0);
        } else {
            count = Integer.valueOf(count.intValue() + 1);
        }
        layoutCounts.put(layout.getConnectorId(), count);
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
        layoutTimer.cancel();
        try {
            currentDependencyTree = new LayoutDependencyTree(connection);
            doLayout();
        } finally {
            currentDependencyTree = null;
        }
    }

    /**
     * Called once per iteration in the layout loop before size calculations so
     * different browsers quirks can be handled. Mainly this is currently for
     * the IE8 permutation.
     */
    protected void performBrowserLayoutHacks() {
        // Permutations implement this
    }

    private void doLayout() {
        VConsole.log("Starting layout phase");
        Profiler.enter("LayoutManager phase init");

        FastStringMap<Integer> layoutCounts = FastStringMap.create();

        int passes = 0;
        Duration totalDuration = new Duration();

        ConnectorMap connectorMap = ConnectorMap.get(connection);

        JsArrayString dump = needsHorizontalLayout.dump();
        int dumpLength = dump.length();
        for (int i = 0; i < dumpLength; i++) {
            String layoutId = dump.get(i);
            currentDependencyTree.setNeedsHorizontalLayout(layoutId, true);
        }

        dump = needsVerticalLayout.dump();
        dumpLength = dump.length();
        for (int i = 0; i < dumpLength; i++) {
            String layoutId = dump.get(i);
            currentDependencyTree.setNeedsVerticalLayout(layoutId, true);
        }
        needsHorizontalLayout = FastStringSet.create();
        needsVerticalLayout = FastStringSet.create();

        dump = needsMeasure.dump();
        dumpLength = dump.length();
        for (int i = 0; i < dumpLength; i++) {
            String layoutId = dump.get(i);
            currentDependencyTree.setNeedsMeasure(layoutId, true);
        }
        needsMeasure = FastStringSet.create();

        measureNonConnectors();

        Profiler.leave("LayoutManager phase init");

        while (true) {
            Profiler.enter("Layout pass");
            passes++;

            performBrowserLayoutHacks();

            Profiler.enter("Layout measure connectors");
            int measuredConnectorCount = measureConnectors(
                    currentDependencyTree, everythingNeedsMeasure);
            Profiler.leave("Layout measure connectors");

            everythingNeedsMeasure = false;
            if (measuredConnectorCount == 0) {
                VConsole.log("No more changes in pass " + passes);
                Profiler.leave("Layout pass");
                break;
            }

            int firedListeners = 0;
            if (!listenersToFire.isEmpty()) {
                firedListeners = listenersToFire.size();
                Profiler.enter("Layout fire resize events");
                for (Element element : listenersToFire) {
                    Collection<ElementResizeListener> listeners = elementResizeListeners
                            .get(element);
                    if (listeners != null) {
                        Profiler.enter("Layout fire resize events - listeners not null");
                        Profiler.enter("ElementResizeListener.onElementResize copy list");
                        ElementResizeListener[] array = listeners
                                .toArray(new ElementResizeListener[listeners
                                        .size()]);
                        Profiler.leave("ElementResizeListener.onElementResize copy list");
                        ElementResizeEvent event = new ElementResizeEvent(this,
                                element);
                        for (ElementResizeListener listener : array) {
                            try {
                                String key = null;
                                if (Profiler.isEnabled()) {
                                    Profiler.enter("ElementResizeListener.onElementResize construct profiler key");
                                    key = "ElementResizeListener.onElementResize for "
                                            + Util.getSimpleName(listener);
                                    Profiler.leave("ElementResizeListener.onElementResize construct profiler key");
                                    Profiler.enter(key);
                                }

                                listener.onElementResize(event);
                                if (Profiler.isEnabled()) {
                                    Profiler.leave(key);
                                }
                            } catch (RuntimeException e) {
                                VConsole.error(e);
                            }
                        }
                        Profiler.leave("Layout fire resize events - listeners not null");
                    }
                }
                listenersToFire.clear();

                Profiler.leave("Layout fire resize events");
            }

            Profiler.enter("LayoutManager handle ManagedLayout");

            FastStringSet updatedSet = FastStringSet.create();

            int layoutCount = 0;
            while (currentDependencyTree.hasHorizontalConnectorToLayout()
                    || currentDependencyTree.hasVerticaConnectorToLayout()) {

                JsArrayString layoutTargets = currentDependencyTree
                        .getHorizontalLayoutTargetsJsArray();
                int length = layoutTargets.length();
                for (int i = 0; i < length; i++) {
                    ManagedLayout layout = (ManagedLayout) connectorMap
                            .getConnector(layoutTargets.get(i));
                    if (layout instanceof DirectionalManagedLayout) {
                        currentDependencyTree
                                .markAsHorizontallyLayouted(layout);
                        DirectionalManagedLayout cl = (DirectionalManagedLayout) layout;
                        try {
                            String key = null;
                            if (Profiler.isEnabled()) {
                                key = "layoutHorizontally() for "
                                        + Util.getSimpleName(cl);
                                Profiler.enter(key);
                            }

                            cl.layoutHorizontally();
                            layoutCount++;

                            if (Profiler.isEnabled()) {
                                Profiler.leave(key);
                            }
                        } catch (RuntimeException e) {
                            VConsole.error(e);
                        }
                        countLayout(layoutCounts, cl);
                    } else {
                        currentDependencyTree
                                .markAsHorizontallyLayouted(layout);
                        currentDependencyTree.markAsVerticallyLayouted(layout);
                        SimpleManagedLayout rr = (SimpleManagedLayout) layout;
                        try {
                            String key = null;
                            if (Profiler.isEnabled()) {
                                key = "layout() for " + Util.getSimpleName(rr);
                                Profiler.enter(key);
                            }

                            rr.layout();
                            layoutCount++;

                            if (Profiler.isEnabled()) {
                                Profiler.leave(key);
                            }
                        } catch (RuntimeException e) {
                            VConsole.error(e);
                        }
                        countLayout(layoutCounts, rr);
                    }
                    if (debugLogging) {
                        updatedSet.add(layout.getConnectorId());
                    }
                }

                layoutTargets = currentDependencyTree
                        .getVerticalLayoutTargetsJsArray();
                length = layoutTargets.length();
                for (int i = 0; i < length; i++) {
                    ManagedLayout layout = (ManagedLayout) connectorMap
                            .getConnector(layoutTargets.get(i));
                    if (layout instanceof DirectionalManagedLayout) {
                        currentDependencyTree.markAsVerticallyLayouted(layout);
                        DirectionalManagedLayout cl = (DirectionalManagedLayout) layout;
                        try {
                            String key = null;
                            if (Profiler.isEnabled()) {
                                key = "layoutVertically() for "
                                        + Util.getSimpleName(cl);
                                Profiler.enter(key);
                            }

                            cl.layoutVertically();
                            layoutCount++;

                            if (Profiler.isEnabled()) {
                                Profiler.leave(key);
                            }
                        } catch (RuntimeException e) {
                            VConsole.error(e);
                        }
                        countLayout(layoutCounts, cl);
                    } else {
                        currentDependencyTree
                                .markAsHorizontallyLayouted(layout);
                        currentDependencyTree.markAsVerticallyLayouted(layout);
                        SimpleManagedLayout rr = (SimpleManagedLayout) layout;
                        try {
                            String key = null;
                            if (Profiler.isEnabled()) {
                                key = "layout() for " + Util.getSimpleName(rr);
                                Profiler.enter(key);
                            }

                            rr.layout();
                            layoutCount++;

                            if (Profiler.isEnabled()) {
                                Profiler.leave(key);
                            }
                        } catch (RuntimeException e) {
                            VConsole.error(e);
                        }
                        countLayout(layoutCounts, rr);
                    }
                    if (debugLogging) {
                        updatedSet.add(layout.getConnectorId());
                    }
                }
            }

            Profiler.leave("LayoutManager handle ManagedLayout");

            if (debugLogging) {
                JsArrayString changedCids = updatedSet.dump();

                StringBuilder b = new StringBuilder("  ");
                b.append(changedCids.length());
                b.append(" requestLayout invocations ");
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

            Profiler.leave("Layout pass");

            VConsole.log("Pass " + passes + " measured "
                    + measuredConnectorCount + " elements, fired "
                    + firedListeners + " listeners and did " + layoutCount
                    + " layouts.");

            if (passes > 100) {
                VConsole.log(LOOP_ABORT_MESSAGE);
                if (ApplicationConfiguration.isDebugMode()) {
                    VNotification.createNotification(
                            VNotification.DELAY_FOREVER,
                            connection.getUIConnector().getWidget())
                            .show(LOOP_ABORT_MESSAGE, VNotification.CENTERED,
                                    "error");
                }
                break;
            }
        }

        Profiler.enter("layout PostLayoutListener");
        JsArrayObject<ComponentConnector> componentConnectors = connectorMap
                .getComponentConnectorsAsJsArray();
        int size = componentConnectors.size();
        for (int i = 0; i < size; i++) {
            ComponentConnector connector = componentConnectors.get(i);
            if (connector instanceof PostLayoutListener) {
                String key = null;
                if (Profiler.isEnabled()) {
                    key = "layout PostLayoutListener for "
                            + Util.getSimpleName(connector);
                    Profiler.enter(key);
                }

                ((PostLayoutListener) connector).postLayout();

                if (Profiler.isEnabled()) {
                    Profiler.leave(key);
                }
            }
        }
        Profiler.leave("layout PostLayoutListener");

        cleanMeasuredSizes();

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
        Profiler.enter("Layout overflow fix handling");
        JsArrayString pendingOverflowConnectorsIds = pendingOverflowFixes
                .dump();
        int pendingOverflowCount = pendingOverflowConnectorsIds.length();
        ConnectorMap connectorMap = ConnectorMap.get(connection);
        if (pendingOverflowCount > 0) {
            HashMap<Element, String> originalOverflows = new HashMap<Element, String>();

            FastStringSet delayedOverflowFixes = FastStringSet.create();

            // First set overflow to hidden (and save previous value so it can
            // be restored later)
            for (int i = 0; i < pendingOverflowCount; i++) {
                String connectorId = pendingOverflowConnectorsIds.get(i);
                ComponentConnector componentConnector = (ComponentConnector) connectorMap
                        .getConnector(connectorId);

                // Delay the overflow fix if the involved connectors might still
                // change
                boolean connectorChangesExpected = !currentDependencyTree
                        .noMoreChangesExpected(componentConnector);
                boolean parentChangesExcpected = componentConnector.getParent() instanceof ComponentConnector
                        && !currentDependencyTree
                                .noMoreChangesExpected((ComponentConnector) componentConnector
                                        .getParent());
                if (connectorChangesExpected || parentChangesExcpected) {
                    delayedOverflowFixes.add(connectorId);
                    continue;
                }

                if (debugLogging) {
                    VConsole.log("Doing overflow fix for "
                            + Util.getConnectorString(componentConnector)
                            + " in "
                            + Util.getConnectorString(componentConnector
                                    .getParent()));
                }
                Profiler.enter("Overflow fix apply");

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
                Profiler.leave("Overflow fix apply");
            }

            pendingOverflowFixes.removeAll(delayedOverflowFixes);

            JsArrayString remainingOverflowFixIds = pendingOverflowFixes.dump();
            int remainingCount = remainingOverflowFixIds.length();

            Profiler.enter("Overflow fix reflow");
            // Then ensure all scrolling elements are reflowed by measuring
            for (int i = 0; i < remainingCount; i++) {
                ComponentConnector componentConnector = (ComponentConnector) connectorMap
                        .getConnector(remainingOverflowFixIds.get(i));
                componentConnector.getWidget().getElement().getParentElement()
                        .getOffsetHeight();
            }
            Profiler.leave("Overflow fix reflow");

            Profiler.enter("Overflow fix restore");
            // Finally restore old overflow value and update bookkeeping
            for (int i = 0; i < remainingCount; i++) {
                String connectorId = remainingOverflowFixIds.get(i);
                ComponentConnector componentConnector = (ComponentConnector) connectorMap
                        .getConnector(connectorId);
                Element parentElement = componentConnector.getWidget()
                        .getElement().getParentElement();
                parentElement.getStyle().setProperty("overflow",
                        originalOverflows.get(parentElement));

                layoutDependencyTree.setNeedsMeasure(connectorId, true);
            }
            Profiler.leave("Overflow fix restore");

            if (!pendingOverflowFixes.isEmpty()) {
                VConsole.log("Did overflow fix for " + remainingCount
                        + " elements");
            }
            pendingOverflowFixes = delayedOverflowFixes;
        }
        Profiler.leave("Layout overflow fix handling");

        int measureCount = 0;
        if (measureAll) {
            Profiler.enter("Layout measureAll");
            JsArrayObject<ComponentConnector> allConnectors = connectorMap
                    .getComponentConnectorsAsJsArray();
            int size = allConnectors.size();

            // Find connectors that should actually be measured
            JsArrayObject<ComponentConnector> connectors = JsArrayObject
                    .createArray().cast();
            for (int i = 0; i < size; i++) {
                ComponentConnector candidate = allConnectors.get(i);
                if (needsMeasure(candidate.getWidget().getElement())) {
                    connectors.add(candidate);
                }
            }

            int connectorCount = connectors.size();
            for (int i = 0; i < connectorCount; i++) {
                measureConnector(connectors.get(i));
            }
            for (int i = 0; i < connectorCount; i++) {
                layoutDependencyTree.setNeedsMeasure(connectors.get(i)
                        .getConnectorId(), false);
            }
            measureCount += connectorCount;

            Profiler.leave("Layout measureAll");
        }

        Profiler.enter("Layout measure from tree");
        while (layoutDependencyTree.hasConnectorsToMeasure()) {
            JsArrayString measureTargets = layoutDependencyTree
                    .getMeasureTargetsJsArray();
            int length = measureTargets.length();
            for (int i = 0; i < length; i++) {
                ComponentConnector connector = (ComponentConnector) connectorMap
                        .getConnector(measureTargets.get(i));
                measureConnector(connector);
                measureCount++;
            }
            for (int i = 0; i < length; i++) {
                String connectorId = measureTargets.get(i);
                layoutDependencyTree.setNeedsMeasure(connectorId, false);
            }
        }
        Profiler.leave("Layout measure from tree");

        return measureCount;
    }

    private void measureConnector(ComponentConnector connector) {
        Profiler.enter("LayoutManager.measureConnector");
        Element element = connector.getWidget().getElement();
        MeasuredSize measuredSize = getMeasuredSize(connector);
        MeasureResult measureResult = measuredAndUpdate(element, measuredSize);

        if (measureResult.isChanged()) {
            onConnectorChange(connector, measureResult.isWidthChanged(),
                    measureResult.isHeightChanged());
        }
        Profiler.leave("LayoutManager.measureConnector");
    }

    private void onConnectorChange(ComponentConnector connector,
            boolean widthChanged, boolean heightChanged) {
        Profiler.enter("LayoutManager.onConnectorChange");
        Profiler.enter("LayoutManager.onConnectorChange setNeedsOverflowFix");
        setNeedsOverflowFix(connector);
        Profiler.leave("LayoutManager.onConnectorChange setNeedsOverflowFix");
        Profiler.enter("LayoutManager.onConnectorChange heightChanged");
        if (heightChanged) {
            currentDependencyTree.markHeightAsChanged(connector);
        }
        Profiler.leave("LayoutManager.onConnectorChange heightChanged");
        Profiler.enter("LayoutManager.onConnectorChange widthChanged");
        if (widthChanged) {
            currentDependencyTree.markWidthAsChanged(connector);
        }
        Profiler.leave("LayoutManager.onConnectorChange widthChanged");
        Profiler.leave("LayoutManager.onConnectorChange");
    }

    private void setNeedsOverflowFix(ComponentConnector connector) {
        // IE9 doesn't need the original fix, but for some reason it needs this
        if (BrowserInfo.get().requiresOverflowAutoFix()
                || BrowserInfo.get().isIE9()) {
            ComponentConnector scrollingBoundary = currentDependencyTree
                    .getScrollingBoundary(connector);
            if (scrollingBoundary != null) {
                pendingOverflowFixes.add(scrollingBoundary.getConnectorId());
            }
        }
    }

    private void measureNonConnectors() {
        Profiler.enter("LayoutManager.measureNonConenctors");
        for (Element element : measuredNonConnectorElements) {
            measuredAndUpdate(element, getMeasuredSize(element, null));
        }
        Profiler.leave("LayoutManager.measureNonConenctors");
        VConsole.log("Measured " + measuredNonConnectorElements.size()
                + " non connector elements");
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

        Profiler.enter("LayoutManager.notifyListenersAndDepdendents");

        MeasuredSize measuredSize = getMeasuredSize(element, nullSize);
        JsArrayString dependents = measuredSize.getDependents();
        for (int i = 0; i < dependents.length(); i++) {
            String pid = dependents.get(i);
            if (pid != null) {
                if (heightChanged) {
                    currentDependencyTree.setNeedsVerticalLayout(pid, true);
                }
                if (widthChanged) {
                    currentDependencyTree.setNeedsHorizontalLayout(pid, true);
                }
            }
        }
        if (elementResizeListeners.containsKey(element)) {
            listenersToFire.add(element);
        }
        Profiler.leave("LayoutManager.notifyListenersAndDepdendents");
    }

    private static boolean isManagedLayout(ComponentConnector connector) {
        return connector instanceof ManagedLayout;
    }

    public void forceLayout() {
        ConnectorMap connectorMap = connection.getConnectorMap();
        JsArrayObject<ComponentConnector> componentConnectors = connectorMap
                .getComponentConnectorsAsJsArray();
        int size = componentConnectors.size();
        for (int i = 0; i < size; i++) {
            ComponentConnector connector = componentConnectors.get(i);
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
     * <p>
     * This method should not be invoked during a layout phase since it only
     * controls what will happen in the beginning of the next phase. If you want
     * to explicitly cause some layout to be considered in an ongoing layout
     * phase, you should use {@link #setNeedsMeasure(ComponentConnector)}
     * instead.
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
     * <p>
     * For SimpleManagedLayout which is always layouted in both directions, this
     * has the same effect as {@link #setNeedsLayout(ManagedLayout)}.
     * <p>
     * This method should not be invoked during a layout phase since it only
     * controls what will happen in the beginning of the next phase. If you want
     * to explicitly cause some layout to be considered in an ongoing layout
     * phase, you should use {@link #setNeedsMeasure(ComponentConnector)}
     * instead.
     * 
     * @param layout
     *            the managed layout that should be layouted
     */
    public final void setNeedsHorizontalLayout(ManagedLayout layout) {
        if (isLayoutRunning()) {
            getLogger()
                    .warning(
                            "setNeedsHorizontalLayout should not be run while a layout phase is in progress.");
        }
        needsHorizontalLayout.add(layout.getConnectorId());
    }

    /**
     * Marks that a ManagedLayout should be layouted vertically in the next
     * layout phase even if none of the elements managed by the layout have been
     * resized vertically.
     * <p>
     * For SimpleManagedLayout which is always layouted in both directions, this
     * has the same effect as {@link #setNeedsLayout(ManagedLayout)}.
     * <p>
     * This method should not be invoked during a layout phase since it only
     * controls what will happen in the beginning of the next phase. If you want
     * to explicitly cause some layout to be considered in an ongoing layout
     * phase, you should use {@link #setNeedsMeasure(ComponentConnector)}
     * instead.
     * 
     * @param layout
     *            the managed layout that should be layouted
     */
    public final void setNeedsVerticalLayout(ManagedLayout layout) {
        if (isLayoutRunning()) {
            getLogger()
                    .warning(
                            "setNeedsVerticalLayout should not be run while a layout phase is in progress.");
        }
        needsVerticalLayout.add(layout.getConnectorId());
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
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
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
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
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
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
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
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
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
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
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
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
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
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
        return getMeasuredSize(element, nullSize).getBorderWidth();
    }

    /**
     * Gets the top border of the given element, provided that it has been
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
     * @return the measured top border of the element in pixels.
     */
    public int getBorderTop(Element element) {
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
        return getMeasuredSize(element, nullSize).getBorderTop();
    }

    /**
     * Gets the left border of the given element, provided that it has been
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
     * @return the measured left border of the element in pixels.
     */
    public int getBorderLeft(Element element) {
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
        return getMeasuredSize(element, nullSize).getBorderLeft();
    }

    /**
     * Gets the bottom border of the given element, provided that it has been
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
     * @return the measured bottom border of the element in pixels.
     */
    public int getBorderBottom(Element element) {
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
        return getMeasuredSize(element, nullSize).getBorderBottom();
    }

    /**
     * Gets the right border of the given element, provided that it has been
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
     * @return the measured right border of the element in pixels.
     */
    public int getBorderRight(Element element) {
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
        return getMeasuredSize(element, nullSize).getBorderRight();
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
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
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
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
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
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
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
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
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
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
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
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
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
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
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
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
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
        assert needsMeasure(element) : "Getting measurement for element that is not measured";
        return getMeasuredSize(element, nullSize).getMarginLeft();
    }

    /**
     * Gets the combined top & bottom margin of the given element, provided that
     * they have been measured. These elements are guaranteed to be measured:
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
     *            the element to get the measured margin for
     * @return the measured top+bottom margin of the element in pixels.
     */
    public int getMarginHeight(Element element) {
        return getMarginTop(element) + getMarginBottom(element);
    }

    /**
     * Gets the combined left & right margin of the given element, provided that
     * they have been measured. These elements are guaranteed to be measured:
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
     *            the element to get the measured margin for
     * @return the measured left+right margin of the element in pixels.
     */
    public int getMarginWidth(Element element) {
        return getMarginLeft(element) + getMarginRight(element);
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

        float percentSize = parsePercent(component.getState().height == null ? ""
                : component.getState().height);
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

        float percentSize = parsePercent(component.getState().width == null ? ""
                : component.getState().width);
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
                stopMeasuringIfUnecessary(element);
            }
        }
    }

    private void stopMeasuringIfUnecessary(Element element) {
        if (!needsMeasure(element)) {
            measuredNonConnectorElements.remove(element);
            setMeasuredSize(element, null);
        }
    }

    /**
     * Informs this LayoutManager that the size of a component might have
     * changed. This method should be used whenever the size of an individual
     * component might have changed from outside of Vaadin's normal update
     * phase, e.g. when an icon has been loaded or when the user resizes some
     * part of the UI using the mouse.
     * <p>
     * To set an entire component hierarchy to be measured, use
     * {@link #setNeedsMeasureRecursively(ComponentConnector)} instead.
     * <p>
     * If there is no upcoming layout phase, a new layout phase is scheduled.
     * 
     * @param component
     *            the component whose size might have changed.
     */
    public void setNeedsMeasure(ComponentConnector component) {
        if (isLayoutRunning()) {
            currentDependencyTree.setNeedsMeasure(component, true);
        } else {
            needsMeasure.add(component.getConnectorId());
            layoutLater();
        }
    }

    /**
     * Informs this LayoutManager that some sizes in a component hierarchy might
     * have changed. This method should be used whenever the size of any child
     * component might have changed from outside of Vaadin's normal update
     * phase, e.g. when a CSS class name related to sizing has been changed.
     * <p>
     * To set a single component to be measured, use
     * {@link #setNeedsMeasure(ComponentConnector)} instead.
     * <p>
     * If there is no upcoming layout phase, a new layout phase is scheduled.
     * 
     * @since 7.2
     * @param component
     *            the component at the root of the component hierarchy to
     *            measure
     */
    public void setNeedsMeasureRecursively(ComponentConnector component) {
        setNeedsMeasure(component);

        if (component instanceof HasComponentsConnector) {
            HasComponentsConnector hasComponents = (HasComponentsConnector) component;
            for (ComponentConnector child : hasComponents.getChildComponents()) {
                setNeedsMeasureRecursively(child);
            }
        }
    }

    public void setEverythingNeedsMeasure() {
        everythingNeedsMeasure = true;
    }

    /**
     * Clean measured sizes which are no longer needed. Only for IE8.
     */
    protected void cleanMeasuredSizes() {
    }

    private static Logger getLogger() {
        return Logger.getLogger(LayoutManager.class.getName());
    }

}
