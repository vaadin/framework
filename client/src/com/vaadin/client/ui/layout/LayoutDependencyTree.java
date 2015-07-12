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
package com.vaadin.client.ui.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import com.google.gwt.core.client.JsArrayString;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.FastStringMap;
import com.vaadin.client.FastStringSet;
import com.vaadin.client.HasComponentsConnector;
import com.vaadin.client.JsArrayObject;
import com.vaadin.client.Profiler;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.ManagedLayout;
import com.vaadin.shared.AbstractComponentState;

/**
 * Internal class used to keep track of layout dependencies during one layout
 * run. This class is not intended to be used directly by applications.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class LayoutDependencyTree {
    private class LayoutDependency {
        private final ComponentConnector connector;
        private final int direction;

        private boolean needsLayout = false;
        private boolean needsMeasure = false;

        private boolean scrollingParentCached = false;
        private ComponentConnector scrollingBoundary = null;

        private FastStringSet measureBlockers = FastStringSet.create();
        private FastStringSet layoutBlockers = FastStringSet.create();

        public LayoutDependency(ComponentConnector connector, int direction) {
            this.connector = connector;
            this.direction = direction;
        }

        private void addLayoutBlocker(ComponentConnector blocker) {
            String blockerId = blocker.getConnectorId();
            if (!layoutBlockers.contains(blockerId)) {
                boolean wasEmpty = layoutBlockers.isEmpty();
                layoutBlockers.add(blockerId);
                if (wasEmpty) {
                    if (needsLayout) {
                        getLayoutQueue(direction).remove(
                                connector.getConnectorId());
                    } else {
                        // Propagation already done if needsLayout is set
                        propagatePotentialLayout();
                    }
                }
            }
        }

        private void removeLayoutBlocker(ComponentConnector blocker) {
            String blockerId = blocker.getConnectorId();
            if (layoutBlockers.contains(blockerId)) {
                layoutBlockers.remove(blockerId);
                if (layoutBlockers.isEmpty()) {
                    if (needsLayout) {
                        getLayoutQueue(direction).add(
                                connector.getConnectorId());
                    } else {
                        propagateNoUpcomingLayout();
                    }
                }
            }
        }

        private void addMeasureBlocker(ComponentConnector blocker) {
            String blockerId = blocker.getConnectorId();
            boolean alreadyAdded = measureBlockers.contains(blockerId);
            if (alreadyAdded) {
                return;
            }
            boolean wasEmpty = measureBlockers.isEmpty();
            measureBlockers.add(blockerId);
            if (wasEmpty) {
                if (needsMeasure) {
                    getMeasureQueue(direction).remove(
                            connector.getConnectorId());
                } else {
                    propagatePotentialResize();
                }
            }
        }

        private void removeMeasureBlocker(ComponentConnector blocker) {
            String blockerId = blocker.getConnectorId();
            boolean alreadyRemoved = !measureBlockers.contains(blockerId);
            if (alreadyRemoved) {
                return;
            }
            measureBlockers.remove(blockerId);
            if (measureBlockers.isEmpty()) {
                if (needsMeasure) {
                    getMeasureQueue(direction).add(connector.getConnectorId());
                } else {
                    propagateNoUpcomingResize();
                }
            }
        }

        public void setNeedsMeasure(boolean needsMeasure) {
            if (needsMeasure && !this.needsMeasure) {
                // If enabling needsMeasure
                this.needsMeasure = needsMeasure;

                if (measureBlockers.isEmpty()) {
                    // Add to queue if there are no blockers
                    getMeasureQueue(direction).add(connector.getConnectorId());
                    // Only need to propagate if not already propagated when
                    // setting blockers
                    propagatePotentialResize();
                }
            } else if (!needsMeasure && this.needsMeasure
                    && measureBlockers.isEmpty()) {
                // Only disable if there are no blockers (elements gets measured
                // in both directions even if there is a blocker in one
                // direction)
                this.needsMeasure = needsMeasure;
                getMeasureQueue(direction).remove(connector.getConnectorId());
                propagateNoUpcomingResize();
            }
        }

        public void setNeedsLayout(boolean needsLayout) {
            if (!(connector instanceof ManagedLayout)) {
                throw new IllegalStateException(
                        "Only managed layouts can need layout, layout attempted for "
                                + Util.getConnectorString(connector));
            }
            if (needsLayout && !this.needsLayout) {
                // If enabling needsLayout
                this.needsLayout = needsLayout;

                if (layoutBlockers.isEmpty()) {
                    // Add to queue if there are no blockers
                    getLayoutQueue(direction).add(connector.getConnectorId());
                    // Only need to propagate if not already propagated when
                    // setting blockers
                    propagatePotentialLayout();
                }
            } else if (!needsLayout && this.needsLayout
                    && layoutBlockers.isEmpty()) {
                // Only disable if there are no layout blockers
                // (SimpleManagedLayout gets layouted in both directions
                // even if there is a blocker in one direction)
                this.needsLayout = needsLayout;
                getLayoutQueue(direction).remove(connector.getConnectorId());
                propagateNoUpcomingLayout();
            }
        }

        private void propagatePotentialResize() {
            JsArrayString needsSizeForLayout = getNeedsSizeForLayout();
            int length = needsSizeForLayout.length();
            for (int i = 0; i < length; i++) {
                String needsSizeId = needsSizeForLayout.get(i);
                LayoutDependency layoutDependency = getDependency(needsSizeId,
                        direction);
                layoutDependency.addLayoutBlocker(connector);
            }
        }

        private JsArrayString getNeedsSizeForLayout() {
            // Find all connectors that need the size of this connector for
            // layouting

            // Parent needs size if it isn't relative?
            // Connector itself needs size if it isn't undefined?
            // Children doesn't care?

            JsArrayString needsSize = JsArrayObject.createArray().cast();

            if (!isUndefinedInDirection(connector, direction)) {
                needsSize.push(connector.getConnectorId());
            }
            if (!isRelativeInDirection(connector, direction)) {
                ServerConnector parent = connector.getParent();
                if (parent instanceof ComponentConnector) {
                    needsSize.push(parent.getConnectorId());
                }
            }

            return needsSize;
        }

        private void propagateNoUpcomingResize() {
            JsArrayString needsSizeForLayout = getNeedsSizeForLayout();
            int length = needsSizeForLayout.length();
            for (int i = 0; i < length; i++) {
                String mightNeedLayoutId = needsSizeForLayout.get(i);
                LayoutDependency layoutDependency = getDependency(
                        mightNeedLayoutId, direction);
                layoutDependency.removeLayoutBlocker(connector);
            }
        }

        private void propagatePotentialLayout() {
            JsArrayString resizedByLayout = getResizedByLayout();
            int length = resizedByLayout.length();
            for (int i = 0; i < length; i++) {
                String sizeMightChangeId = resizedByLayout.get(i);
                LayoutDependency layoutDependency = getDependency(
                        sizeMightChangeId, direction);
                layoutDependency.addMeasureBlocker(connector);
            }
        }

        private JsArrayString getResizedByLayout() {
            // Components that might get resized by a layout of this component

            // Parent never resized
            // Connector itself resized if undefined
            // Children resized if relative

            JsArrayString resized = JsArrayObject.createArray().cast();
            if (isUndefinedInDirection(connector, direction)) {
                resized.push(connector.getConnectorId());
            }

            if (connector instanceof HasComponentsConnector) {
                HasComponentsConnector container = (HasComponentsConnector) connector;
                for (ComponentConnector child : container.getChildComponents()) {
                    if (isRelativeInDirection(child, direction)) {
                        resized.push(child.getConnectorId());
                    }
                }
            }

            return resized;
        }

        private void propagateNoUpcomingLayout() {
            JsArrayString resizedByLayout = getResizedByLayout();
            int length = resizedByLayout.length();
            for (int i = 0; i < length; i++) {
                String sizeMightChangeId = resizedByLayout.get(i);
                LayoutDependency layoutDependency = getDependency(
                        sizeMightChangeId, direction);
                layoutDependency.removeMeasureBlocker(connector);
            }
        }

        public void markSizeAsChanged() {
            Profiler.enter("LayoutDependency.markSizeAsChanged phase 1");
            // When the size has changed, all that use that size should be
            // layouted
            JsArrayString needsSizeForLayout = getNeedsSizeForLayout();
            int length = needsSizeForLayout.length();
            for (int i = 0; i < length; i++) {
                String connectorId = needsSizeForLayout.get(i);
                LayoutDependency layoutDependency = getDependency(connectorId,
                        direction);
                if (layoutDependency.connector instanceof ManagedLayout) {
                    Profiler.enter("LayoutDependency.markSizeAsChanged setNeedsLayout");
                    layoutDependency.setNeedsLayout(true);
                    Profiler.leave("LayoutDependency.markSizeAsChanged setNeedsLayout");
                } else {
                    Profiler.enter("LayoutDependency.markSizeAsChanged propagatePostLayoutMeasure");
                    // Should simulate setNeedsLayout(true) + markAsLayouted ->
                    // propagate needs measure
                    layoutDependency.propagatePostLayoutMeasure();
                    Profiler.leave("LayoutDependency.markSizeAsChanged propagatePostLayoutMeasure");
                }
            }
            Profiler.leave("LayoutDependency.markSizeAsChanged phase 1");

            Profiler.enter("LayoutDependency.markSizeAsChanged scrollbars");
            // Should also go through the hierarchy to discover appeared or
            // disappeared scrollbars
            ComponentConnector scrollingBoundary = getScrollingBoundary(connector);
            if (scrollingBoundary != null) {
                getDependency(scrollingBoundary.getConnectorId(),
                        getOppositeDirection()).setNeedsMeasure(true);
            }
            Profiler.leave("LayoutDependency.markSizeAsChanged scrollbars");

        }

        /**
         * Go up the hierarchy to find a component whose size might have changed
         * in the other direction because changes to this component causes
         * scrollbars to appear or disappear.
         * 
         * @return
         */
        private LayoutDependency findPotentiallyChangedScrollbar() {
            ComponentConnector currentConnector = connector;
            while (true) {
                ServerConnector parent = currentConnector.getParent();
                if (!(parent instanceof ComponentConnector)) {
                    return null;
                }
                if (parent instanceof MayScrollChildren) {
                    return getDependency(currentConnector.getConnectorId(),
                            getOppositeDirection());
                }
                currentConnector = (ComponentConnector) parent;
            }
        }

        private int getOppositeDirection() {
            return direction == HORIZONTAL ? VERTICAL : HORIZONTAL;
        }

        public void markAsLayouted() {
            if (!layoutBlockers.isEmpty()) {
                // Don't do anything if there are layout blockers (SimpleLayout
                // gets layouted in both directions even if one direction is
                // blocked)
                return;
            }
            setNeedsLayout(false);
            propagatePostLayoutMeasure();
        }

        private void propagatePostLayoutMeasure() {
            Profiler.enter("LayoutDependency.propagatePostLayoutMeasure getResizedByLayout");
            JsArrayString resizedByLayout = getResizedByLayout();
            Profiler.leave("LayoutDependency.propagatePostLayoutMeasure getResizedByLayout");
            int length = resizedByLayout.length();
            for (int i = 0; i < length; i++) {
                Profiler.enter("LayoutDependency.propagatePostLayoutMeasure setNeedsMeasure");
                String resizedId = resizedByLayout.get(i);
                LayoutDependency layoutDependency = getDependency(resizedId,
                        direction);
                layoutDependency.setNeedsMeasure(true);
                Profiler.leave("LayoutDependency.propagatePostLayoutMeasure setNeedsMeasure");
            }

            // Special case for e.g. wrapping texts
            Profiler.enter("LayoutDependency.propagatePostLayoutMeasure horizontal case");
            if (direction == HORIZONTAL && !connector.isUndefinedWidth()
                    && connector.isUndefinedHeight()) {
                LayoutDependency dependency = getDependency(
                        connector.getConnectorId(), VERTICAL);
                dependency.setNeedsMeasure(true);
            }
            Profiler.leave("LayoutDependency.propagatePostLayoutMeasure horizontal case");
        }

        @Override
        public String toString() {
            String s = getCompactConnectorString(connector) + "\n";
            if (direction == VERTICAL) {
                s += "Vertical";
            } else {
                s += "Horizontal";
            }
            AbstractComponentState state = connector.getState();
            s += " sizing: "
                    + getSizeDefinition(direction == VERTICAL ? state.height
                            : state.width) + "\n";

            if (needsLayout) {
                s += "Needs layout\n";
            }
            if (getLayoutQueue(direction).contains(connector.getConnectorId())) {
                s += "In layout queue\n";
            }
            s += "Layout blockers: " + blockersToString(layoutBlockers) + "\n";

            if (needsMeasure) {
                s += "Needs measure\n";
            }
            if (getMeasureQueue(direction).contains(connector.getConnectorId())) {
                s += "In measure queue\n";
            }
            s += "Measure blockers: " + blockersToString(measureBlockers);

            return s;
        }

        public boolean noMoreChangesExpected() {
            return !needsLayout && !needsMeasure && layoutBlockers.isEmpty()
                    && measureBlockers.isEmpty();
        }

    }

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;

    @SuppressWarnings("unchecked")
    private final FastStringMap<LayoutDependency>[] dependenciesInDirection = new FastStringMap[] {
            FastStringMap.create(), FastStringMap.create() };

    private final FastStringSet[] measureQueueInDirection = new FastStringSet[] {
            FastStringSet.create(), FastStringSet.create() };

    private final FastStringSet[] layoutQueueInDirection = new FastStringSet[] {
            FastStringSet.create(), FastStringSet.create() };

    private final ApplicationConnection connection;

    public LayoutDependencyTree(ApplicationConnection connection) {
        this.connection = connection;
    }

    public void setNeedsMeasure(ComponentConnector connector,
            boolean needsMeasure) {
        setNeedsHorizontalMeasure(connector, needsMeasure);
        setNeedsVerticalMeasure(connector, needsMeasure);
    }

    /**
     * @param connectorId
     * @param needsMeasure
     * 
     * @deprecated As of 7.4.2, use
     *             {@link #setNeedsMeasure(ComponentConnector, boolean)} for
     *             improved performance.
     */
    @Deprecated
    public void setNeedsMeasure(String connectorId, boolean needsMeasure) {
        ComponentConnector connector = (ComponentConnector) ConnectorMap.get(
                connection).getConnector(connectorId);
        if (connector == null) {
            return;
        }

        setNeedsMeasure(connector, needsMeasure);
    }

    public void setNeedsHorizontalMeasure(ComponentConnector connector,
            boolean needsMeasure) {
        LayoutDependency dependency = getDependency(connector, HORIZONTAL);
        dependency.setNeedsMeasure(needsMeasure);
    }

    public void setNeedsHorizontalMeasure(String connectorId,
            boolean needsMeasure) {
        // Ensure connector exists
        ComponentConnector connector = (ComponentConnector) ConnectorMap.get(
                connection).getConnector(connectorId);
        if (connector == null) {
            return;
        }

        setNeedsHorizontalMeasure(connector, needsMeasure);
    }

    public void setNeedsVerticalMeasure(ComponentConnector connector,
            boolean needsMeasure) {
        LayoutDependency dependency = getDependency(connector, VERTICAL);
        dependency.setNeedsMeasure(needsMeasure);
    }

    public void setNeedsVerticalMeasure(String connectorId, boolean needsMeasure) {
        // Ensure connector exists
        ComponentConnector connector = (ComponentConnector) ConnectorMap.get(
                connection).getConnector(connectorId);
        if (connector == null) {
            return;
        }

        setNeedsVerticalMeasure(connector, needsMeasure);
    }

    private LayoutDependency getDependency(ComponentConnector connector,
            int direction) {
        return getDependency(connector.getConnectorId(), connector, direction);
    }

    private LayoutDependency getDependency(String connectorId, int direction) {
        return getDependency(connectorId, null, direction);
    }

    private LayoutDependency getDependency(String connectorId,
            ComponentConnector connector, int direction) {
        FastStringMap<LayoutDependency> dependencies = dependenciesInDirection[direction];
        LayoutDependency dependency = dependencies.get(connectorId);
        if (dependency == null) {
            if (connector == null) {
                connector = (ComponentConnector) ConnectorMap.get(connection)
                        .getConnector(connectorId);
                if (connector == null) {
                    getLogger().warning(
                            "No connector found for id " + connectorId
                                    + " while creating LayoutDependency");
                    return null;
                }
            }
            dependency = new LayoutDependency(connector, direction);
            dependencies.put(connectorId, dependency);
        }
        return dependency;
    }

    private FastStringSet getLayoutQueue(int direction) {
        return layoutQueueInDirection[direction];
    }

    private FastStringSet getMeasureQueue(int direction) {
        return measureQueueInDirection[direction];
    }

    /**
     * @param layout
     * @param needsLayout
     * 
     * @deprecated As of 7.0.1, use
     *             {@link #setNeedsHorizontalLayout(String, boolean)} for
     *             improved performance.
     */
    @Deprecated
    public void setNeedsHorizontalLayout(ManagedLayout layout,
            boolean needsLayout) {
        setNeedsHorizontalLayout(layout.getConnectorId(), needsLayout);
    }

    public void setNeedsHorizontalLayout(String connectorId, boolean needsLayout) {
        LayoutDependency dependency = getDependency(connectorId, HORIZONTAL);
        if (dependency != null) {
            dependency.setNeedsLayout(needsLayout);
        } else {
            getLogger().warning(
                    "No dependency found in setNeedsHorizontalLayout");
        }
    }

    /**
     * @param layout
     * @param needsLayout
     * 
     * @deprecated As of 7.0.1, use
     *             {@link #setNeedsVerticalLayout(String, boolean)} for improved
     *             performance.
     */
    @Deprecated
    public void setNeedsVerticalLayout(ManagedLayout layout, boolean needsLayout) {
        setNeedsVerticalLayout(layout.getConnectorId(), needsLayout);
    }

    public void setNeedsVerticalLayout(String connectorId, boolean needsLayout) {
        LayoutDependency dependency = getDependency(connectorId, VERTICAL);
        if (dependency != null) {
            dependency.setNeedsLayout(needsLayout);
        } else {
            getLogger()
                    .warning("No dependency found in setNeedsVerticalLayout");
        }

    }

    public void markAsHorizontallyLayouted(ManagedLayout layout) {
        LayoutDependency dependency = getDependency(layout.getConnectorId(),
                HORIZONTAL);
        dependency.markAsLayouted();
    }

    public void markAsVerticallyLayouted(ManagedLayout layout) {
        LayoutDependency dependency = getDependency(layout.getConnectorId(),
                VERTICAL);
        dependency.markAsLayouted();
    }

    public void markHeightAsChanged(ComponentConnector connector) {
        LayoutDependency dependency = getDependency(connector.getConnectorId(),
                VERTICAL);
        dependency.markSizeAsChanged();
    }

    public void markWidthAsChanged(ComponentConnector connector) {
        LayoutDependency dependency = getDependency(connector.getConnectorId(),
                HORIZONTAL);
        dependency.markSizeAsChanged();
    }

    private static boolean isRelativeInDirection(ComponentConnector connector,
            int direction) {
        if (direction == HORIZONTAL) {
            return connector.isRelativeWidth();
        } else {
            return connector.isRelativeHeight();
        }
    }

    private static boolean isUndefinedInDirection(ComponentConnector connector,
            int direction) {
        if (direction == VERTICAL) {
            return connector.isUndefinedHeight();
        } else {
            return connector.isUndefinedWidth();
        }
    }

    private static String getCompactConnectorString(ServerConnector connector) {
        return connector.getClass().getSimpleName() + " ("
                + connector.getConnectorId() + ")";
    }

    private static String getSizeDefinition(String size) {
        if (size == null || size.length() == 0) {
            return "undefined";
        } else if (size.endsWith("%")) {
            return "relative";
        } else {
            return "fixed";
        }
    }

    private String blockersToString(FastStringSet blockers) {
        StringBuilder b = new StringBuilder("[");

        ConnectorMap connectorMap = ConnectorMap.get(connection);
        JsArrayString blockersDump = blockers.dump();
        for (int i = 0; i < blockersDump.length(); i++) {
            ServerConnector blocker = connectorMap.getConnector(blockersDump
                    .get(i));
            if (b.length() != 1) {
                b.append(", ");
            }
            b.append(getCompactConnectorString(blocker));
        }
        b.append(']');
        return b.toString();
    }

    public boolean hasConnectorsToMeasure() {
        return !measureQueueInDirection[HORIZONTAL].isEmpty()
                || !measureQueueInDirection[VERTICAL].isEmpty();
    }

    public boolean hasHorizontalConnectorToLayout() {
        return !getLayoutQueue(HORIZONTAL).isEmpty();
    }

    public boolean hasVerticaConnectorToLayout() {
        return !getLayoutQueue(VERTICAL).isEmpty();
    }

    /**
     * @return
     * @deprecated As of 7.0.1, use {@link #getHorizontalLayoutTargetsJsArray()}
     *             for improved performance.
     */
    @Deprecated
    public ManagedLayout[] getHorizontalLayoutTargets() {
        return asManagedLayoutArray(getHorizontalLayoutTargetsJsArray());
    }

    /**
     * @return
     * @deprecated As of 7.0.1, use {@link #getVerticalLayoutTargetsJsArray()}
     *             for improved performance.
     */
    @Deprecated
    public ManagedLayout[] getVerticalLayoutTargets() {
        return asManagedLayoutArray(getVerticalLayoutTargetsJsArray());
    }

    private ManagedLayout[] asManagedLayoutArray(JsArrayString connectorIdArray) {
        int length = connectorIdArray.length();
        ConnectorMap connectorMap = ConnectorMap.get(connection);
        ManagedLayout[] result = new ManagedLayout[length];
        for (int i = 0; i < length; i++) {
            result[i] = (ManagedLayout) connectorMap
                    .getConnector(connectorIdArray.get(i));
        }
        return result;
    }

    public JsArrayString getHorizontalLayoutTargetsJsArray() {
        return getLayoutQueue(HORIZONTAL).dump();
    }

    public JsArrayString getVerticalLayoutTargetsJsArray() {
        return getLayoutQueue(VERTICAL).dump();
    }

    /**
     * @return
     * @deprecated As of 7.0.1, use {@link #getMeasureTargetsJsArray()} for
     *             improved performance.
     */
    @Deprecated
    public Collection<ComponentConnector> getMeasureTargets() {
        JsArrayString targetIds = getMeasureTargetsJsArray();
        int length = targetIds.length();
        ArrayList<ComponentConnector> targets = new ArrayList<ComponentConnector>(
                length);
        ConnectorMap connectorMap = ConnectorMap.get(connection);

        for (int i = 0; i < length; i++) {
            targets.add((ComponentConnector) connectorMap
                    .getConnector(targetIds.get(i)));
        }
        return targets;
    }

    public JsArrayString getMeasureTargetsJsArray() {
        FastStringSet allMeasuredTargets = FastStringSet.create();
        allMeasuredTargets.addAll(getMeasureQueue(HORIZONTAL));
        allMeasuredTargets.addAll(getMeasureQueue(VERTICAL));
        return allMeasuredTargets.dump();
    }

    public void logDependencyStatus(ComponentConnector connector) {
        VConsole.log("====");
        String connectorId = connector.getConnectorId();
        VConsole.log(getDependency(connectorId, HORIZONTAL).toString());
        VConsole.log(getDependency(connectorId, VERTICAL).toString());
    }

    public boolean noMoreChangesExpected(ComponentConnector connector) {
        return getDependency(connector.getConnectorId(), HORIZONTAL)
                .noMoreChangesExpected()
                && getDependency(connector.getConnectorId(), VERTICAL)
                        .noMoreChangesExpected();
    }

    public ComponentConnector getScrollingBoundary(ComponentConnector connector) {
        LayoutDependency dependency = getDependency(connector.getConnectorId(),
                HORIZONTAL);
        if (!dependency.scrollingParentCached) {
            ServerConnector parent = dependency.connector.getParent();
            if (parent instanceof MayScrollChildren) {
                dependency.scrollingBoundary = connector;
            } else if (parent instanceof ComponentConnector) {
                dependency.scrollingBoundary = getScrollingBoundary((ComponentConnector) parent);
            } else {
                // No scrolling parent
            }

            dependency.scrollingParentCached = true;
        }
        return dependency.scrollingBoundary;
    }

    private static Logger getLogger() {
        return Logger.getLogger(LayoutDependencyTree.class.getName());
    }

}
