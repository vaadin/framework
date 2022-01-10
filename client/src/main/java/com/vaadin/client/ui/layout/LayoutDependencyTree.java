/*
 * Copyright 2000-2022 Vaadin Ltd.
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
import java.util.List;
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
                        getLayoutQueue(direction)
                                .remove(connector.getConnectorId());
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
                        getLayoutQueue(direction)
                                .add(connector.getConnectorId());
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
                    getMeasureQueue(direction)
                            .remove(connector.getConnectorId());
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
                for (ComponentConnector child : container
                        .getChildComponents()) {
                    if (!Util.shouldSkipMeasurementOfConnector(child, connector)
                            && isRelativeInDirection(child, direction)) {
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
                    Profiler.enter(
                            "LayoutDependency.markSizeAsChanged setNeedsLayout");
                    layoutDependency.setNeedsLayout(true);
                    Profiler.leave(
                            "LayoutDependency.markSizeAsChanged setNeedsLayout");
                } else {
                    Profiler.enter(
                            "LayoutDependency.markSizeAsChanged propagatePostLayoutMeasure");
                    // Should simulate setNeedsLayout(true) + markAsLayouted ->
                    // propagate needs measure
                    layoutDependency.propagatePostLayoutMeasure();
                    Profiler.leave(
                            "LayoutDependency.markSizeAsChanged propagatePostLayoutMeasure");
                }
            }
            Profiler.leave("LayoutDependency.markSizeAsChanged phase 1");

            Profiler.enter("LayoutDependency.markSizeAsChanged scrollbars");
            // Should also go through the hierarchy to discover appeared or
            // disappeared scrollbars
            ComponentConnector scrollingBoundary = getScrollingBoundary(
                    connector);
            if (scrollingBoundary != null) {
                getDependency(scrollingBoundary.getConnectorId(),
                        getOppositeDirection()).setNeedsMeasure(true);
            }
            Profiler.leave("LayoutDependency.markSizeAsChanged scrollbars");

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
            Profiler.enter(
                    "LayoutDependency.propagatePostLayoutMeasure getResizedByLayout");
            JsArrayString resizedByLayout = getResizedByLayout();
            Profiler.leave(
                    "LayoutDependency.propagatePostLayoutMeasure getResizedByLayout");
            int length = resizedByLayout.length();
            for (int i = 0; i < length; i++) {
                Profiler.enter(
                        "LayoutDependency.propagatePostLayoutMeasure setNeedsMeasure");
                String resizedId = resizedByLayout.get(i);
                LayoutDependency layoutDependency = getDependency(resizedId,
                        direction);
                layoutDependency.setNeedsMeasure(true);
                Profiler.leave(
                        "LayoutDependency.propagatePostLayoutMeasure setNeedsMeasure");
            }

            // Special case for e.g. wrapping texts
            Profiler.enter(
                    "LayoutDependency.propagatePostLayoutMeasure horizontal case");
            if (direction == HORIZONTAL && !connector.isUndefinedWidth()
                    && connector.isUndefinedHeight()) {
                LayoutDependency dependency = getDependency(
                        connector.getConnectorId(), VERTICAL);
                dependency.setNeedsMeasure(true);
            }
            Profiler.leave(
                    "LayoutDependency.propagatePostLayoutMeasure horizontal case");
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
                    + getSizeDefinition(
                            direction == VERTICAL ? state.height : state.width)
                    + "\n";

            if (needsLayout) {
                s += "Needs layout\n";
            }
            if (getLayoutQueue(direction)
                    .contains(connector.getConnectorId())) {
                s += "In layout queue\n";
            }
            s += "Layout blockers: " + blockersToString(layoutBlockers) + "\n";

            if (needsMeasure) {
                s += "Needs measure\n";
            }
            if (getMeasureQueue(direction)
                    .contains(connector.getConnectorId())) {
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

    private final FastStringSet[] measureQueueInDirection = {
            FastStringSet.create(), FastStringSet.create() };

    private final FastStringSet[] layoutQueueInDirection = {
            FastStringSet.create(), FastStringSet.create() };

    private final ApplicationConnection connection;

    /**
     * Constructs a layout dependency helper class.
     *
     * @param connection
     *            the current application connection instance, should not be
     *            {@code null}
     *
     * @see LayoutDependencyTree
     */
    public LayoutDependencyTree(ApplicationConnection connection) {
        this.connection = connection;
    }

    /**
     * Informs this LayoutDependencyTree that the size of a component might have
     * changed and it needs measuring in both directions, or that the measuring
     * is no longer necessary. If there are blockers, measuring will be delayed
     * and cannot be disabled before the blockers have been removed.
     *
     * @param connector
     *            the connector of the component whose size might have changed,
     *            should not be {@code null}
     * @param needsMeasure
     *            {@code true} if measuring should be enabled, {@code false} if
     *            measuring should be disabled (disabling is only effective if
     *            there are no blockers)
     */
    public void setNeedsMeasure(ComponentConnector connector,
            boolean needsMeasure) {
        setNeedsHorizontalMeasure(connector, needsMeasure);
        setNeedsVerticalMeasure(connector, needsMeasure);
    }

    /**
     * @param connectorId
     *            the connector id of the component whose size might have
     *            changed
     * @param needsMeasure
     *            {@code true} if measuring should be enabled, {@code false} if
     *            measuring should be disabled (disabling is only effective if
     *            there are no blockers)
     *
     * @deprecated As of 7.4.2, use
     *             {@link #setNeedsMeasure(ComponentConnector, boolean)} for
     *             improved performance.
     */
    @Deprecated
    public void setNeedsMeasure(String connectorId, boolean needsMeasure) {
        ComponentConnector connector = (ComponentConnector) ConnectorMap
                .get(connection).getConnector(connectorId);
        if (connector == null) {
            return;
        }

        setNeedsMeasure(connector, needsMeasure);
    }

    /**
     * Informs this LayoutDependencyTree that the horizontal size of a component
     * might have changed and it needs measuring, or that the measuring is no
     * longer necessary. If there are blockers, measuring will be delayed and
     * cannot be disabled before the blockers have been removed.
     *
     * @param connector
     *            the connector of the component whose horizontal size might
     *            have changed, should not be {@code null}
     * @param needsMeasure
     *            {@code true} if measuring should be enabled, {@code false} if
     *            measuring should be disabled (disabling is only effective if
     *            there are no blockers)
     */
    public void setNeedsHorizontalMeasure(ComponentConnector connector,
            boolean needsMeasure) {
        LayoutDependency dependency = getDependency(connector, HORIZONTAL);
        dependency.setNeedsMeasure(needsMeasure);
    }

    /**
     * @param connectorId
     *            the connector id of the component whose horizontal size might
     *            have changed
     * @param needsMeasure
     *            {@code true} if measuring should be enabled, {@code false} if
     *            measuring should be disabled (disabling is only effective if
     *            there are no blockers)
     *
     * @deprecated Use
     *             {@link #setNeedsHorizontalMeasure(ComponentConnector, boolean)}
     *             for improved performance.
     */
    @Deprecated
    public void setNeedsHorizontalMeasure(String connectorId,
            boolean needsMeasure) {
        // Ensure connector exists
        ComponentConnector connector = (ComponentConnector) ConnectorMap
                .get(connection).getConnector(connectorId);
        if (connector == null) {
            return;
        }

        setNeedsHorizontalMeasure(connector, needsMeasure);
    }

    /**
     * Informs this LayoutDependencyTree that the vertical size of a component
     * might have changed and it needs measuring, or that the measuring is no
     * longer necessary. If there are blockers, measuring will be delayed and
     * cannot be disabled before the blockers have been removed.
     *
     * @param connector
     *            the connector of the component whose vertical size might have
     *            changed, should not be {@code null}
     * @param needsMeasure
     *            {@code true} if measuring should be enabled, {@code false} if
     *            measuring should be disabled (disabling is only effective if
     *            there are no blockers)
     */
    public void setNeedsVerticalMeasure(ComponentConnector connector,
            boolean needsMeasure) {
        LayoutDependency dependency = getDependency(connector, VERTICAL);
        dependency.setNeedsMeasure(needsMeasure);
    }

    /**
     * @param connectorId
     *            the connector id of the component whose vertical size might
     *            have changed
     * @param needsMeasure
     *            {@code true} if measuring should be enabled, {@code false} if
     *            measuring should be disabled (disabling is only effective if
     *            there are no blockers)
     *
     * @deprecated Use
     *             {@link #setNeedsVerticalMeasure(ComponentConnector, boolean)}
     *             for improved performance.
     */
    @Deprecated
    public void setNeedsVerticalMeasure(String connectorId,
            boolean needsMeasure) {
        // Ensure connector exists
        ComponentConnector connector = (ComponentConnector) ConnectorMap
                .get(connection).getConnector(connectorId);
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
                    getLogger().warning("No connector found for id "
                            + connectorId + " while creating LayoutDependency");
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
     *            the managed layout whose horizontal size might have changed,
     *            should not be {@code null}
     * @param needsLayout
     *            {@code true} if layouting should be enabled, {@code false} if
     *            layouting should be disabled (disabling is only effective if
     *            there are no blockers)
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

    /**
     * Informs this LayoutDependencyTree that the horizontal size of a managed
     * layout might have changed and it needs layouting, or that the layouting
     * is no longer necessary. If there are blockers, layouting will be delayed
     * and cannot be disabled before the blockers have been removed. Logs a
     * warning if no dependency is found.
     *
     * @param connectorId
     *            the connector id of the managed layout whose horizontal size
     *            might have changed
     * @param needsLayout
     *            {@code true} if layouting should be enabled, {@code false} if
     *            layouting should be disabled (disabling is only effective if
     *            there are no blockers)
     */
    public void setNeedsHorizontalLayout(String connectorId,
            boolean needsLayout) {
        LayoutDependency dependency = getDependency(connectorId, HORIZONTAL);
        if (dependency != null) {
            dependency.setNeedsLayout(needsLayout);
        } else {
            getLogger()
                    .warning("No dependency found in setNeedsHorizontalLayout");
        }
    }

    /**
     * @param layout
     *            the managed layout whose vertical size might have changed,
     *            should not be {@code null}
     * @param needsLayout
     *            {@code true} if layouting should be enabled, {@code false} if
     *            layouting should be disabled (disabling is only effective if
     *            there are no blockers)
     *
     * @deprecated As of 7.0.1, use
     *             {@link #setNeedsVerticalLayout(String, boolean)} for improved
     *             performance.
     */
    @Deprecated
    public void setNeedsVerticalLayout(ManagedLayout layout,
            boolean needsLayout) {
        setNeedsVerticalLayout(layout.getConnectorId(), needsLayout);
    }

    /**
     * Informs this LayoutDependencyTree that the vertical size of a managed
     * layout might have changed and it needs layouting, or that the layouting
     * is no longer necessary. If there are blockers, layouting will be delayed
     * and cannot be disabled before the blockers have been removed. Logs a
     * warning if no dependency is found.
     *
     * @param connectorId
     *            the connector id of the managed layout whose vertical size
     *            might have changed
     * @param needsLayout
     *            {@code true} if layouting should be enabled, {@code false} if
     *            layouting should be disabled (disabling is only effective if
     *            there are no blockers)
     */
    public void setNeedsVerticalLayout(String connectorId,
            boolean needsLayout) {
        LayoutDependency dependency = getDependency(connectorId, VERTICAL);
        if (dependency != null) {
            dependency.setNeedsLayout(needsLayout);
        } else {
            getLogger()
                    .warning("No dependency found in setNeedsVerticalLayout");
        }

    }

    /**
     * Marks the managed layout as layouted horizontally and propagates the need
     * of horizontal measuring for any components that might have got their size
     * changed as a result. If there are blockers, nothing is done.
     *
     * @param layout
     *            the managed layout whose horizontal layouting has been done,
     *            should not be {@code null}
     */
    public void markAsHorizontallyLayouted(ManagedLayout layout) {
        LayoutDependency dependency = getDependency(layout.getConnectorId(),
                HORIZONTAL);
        dependency.markAsLayouted();
    }

    /**
     * Marks the managed layout as layouted vertically and propagates the need
     * of vertical measuring for any components that might have got their size
     * changed as a result. If there are blockers, nothing is done.
     *
     * @param layout
     *            the managed layout whose vertical layouting has been done,
     *            should not be {@code null}
     */
    public void markAsVerticallyLayouted(ManagedLayout layout) {
        LayoutDependency dependency = getDependency(layout.getConnectorId(),
                VERTICAL);
        dependency.markAsLayouted();
    }

    /**
     * Marks the component's height as changed. Iterates through all components
     * whose vertical size depends on this component's size. If the dependent is
     * a managed layout triggers need for vertical layouting, otherwise triggers
     * need for vertical measuring for any dependent components of that
     * component in turn. Finally triggers horizontal measuring for the
     * scrolling boundary, in case vertical scrollbar has appeared or
     * disappeared due the height change.
     *
     * @param connector
     *            the connector of the component whose height has changed,
     *            should not be {@code null}
     */
    public void markHeightAsChanged(ComponentConnector connector) {
        LayoutDependency dependency = getDependency(connector.getConnectorId(),
                VERTICAL);
        dependency.markSizeAsChanged();
    }

    /**
     * Marks the component's width as changed. Iterates through all components
     * whose horizontal size depends on this component's size. If the dependent
     * is a managed layout triggers need for horizontal layouting, otherwise
     * triggers need for horizontal measuring for any dependent components of
     * that component in turn. Finally triggers vertical measuring for the
     * scrolling boundary, in case horizontal scrollbar has appeared or
     * disappeared due the width change.
     *
     * @param connector
     *            the connector of the component whose width has changed, should
     *            not be {@code null}
     */
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
        if (size == null || size.isEmpty()) {
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
            ServerConnector blocker = connectorMap
                    .getConnector(blockersDump.get(i));
            if (b.length() != 1) {
                b.append(", ");
            }
            b.append(getCompactConnectorString(blocker));
        }
        b.append(']');
        return b.toString();
    }

    /**
     * Returns whether there are any components waiting for either horizontal or
     * vertical measuring.
     *
     * @return {@code true} if either measure queue contains anything,
     *         {@code false} otherwise
     */
    public boolean hasConnectorsToMeasure() {
        return !measureQueueInDirection[HORIZONTAL].isEmpty()
                || !measureQueueInDirection[VERTICAL].isEmpty();
    }

    /**
     * Returns whether there are any managed layouts waiting for horizontal
     * layouting.
     *
     * @return {@code true} if horizontal layouting queue is not empty,
     *         {@code false} otherwise
     */
    public boolean hasHorizontalConnectorToLayout() {
        return !getLayoutQueue(HORIZONTAL).isEmpty();
    }

    /**
     * Returns whether there are any managed layouts waiting for vertical
     * layouting.
     *
     * @return {@code true} if vertical layouting queue is not empty,
     *         {@code false} otherwise
     */
    public boolean hasVerticaConnectorToLayout() {
        return !getLayoutQueue(VERTICAL).isEmpty();
    }

    /**
     * @return array of managed layouts waiting for horizontal layouting
     * @deprecated As of 7.0.1, use {@link #getHorizontalLayoutTargetsJsArray()}
     *             for improved performance.
     */
    @Deprecated
    public ManagedLayout[] getHorizontalLayoutTargets() {
        return asManagedLayoutArray(getHorizontalLayoutTargetsJsArray());
    }

    /**
     * @return array of managed layouts waiting for vertical layouting
     * @deprecated As of 7.0.1, use {@link #getVerticalLayoutTargetsJsArray()}
     *             for improved performance.
     */
    @Deprecated
    public ManagedLayout[] getVerticalLayoutTargets() {
        return asManagedLayoutArray(getVerticalLayoutTargetsJsArray());
    }

    private ManagedLayout[] asManagedLayoutArray(
            JsArrayString connectorIdArray) {
        int length = connectorIdArray.length();
        ConnectorMap connectorMap = ConnectorMap.get(connection);
        ManagedLayout[] result = new ManagedLayout[length];
        for (int i = 0; i < length; i++) {
            result[i] = (ManagedLayout) connectorMap
                    .getConnector(connectorIdArray.get(i));
        }
        return result;
    }

    /**
     * Returns a JsArrayString array of connectorIds for managed layouts that
     * are waiting for horizontal layouting.
     *
     * @return JsArrayString of connectorIds
     */
    public JsArrayString getHorizontalLayoutTargetsJsArray() {
        return getLayoutQueue(HORIZONTAL).dump();
    }

    /**
     * Returns a JsArrayString array of connectorIds for managed layouts that
     * are waiting for vertical layouting.
     *
     * @return JsArrayString of connectorIds
     */
    public JsArrayString getVerticalLayoutTargetsJsArray() {
        return getLayoutQueue(VERTICAL).dump();
    }

    /**
     * @return connectors that are waiting for measuring
     * @deprecated As of 7.0.1, use {@link #getMeasureTargetsJsArray()} for
     *             improved performance.
     */
    @Deprecated
    public Collection<ComponentConnector> getMeasureTargets() {
        JsArrayString targetIds = getMeasureTargetsJsArray();
        int length = targetIds.length();
        List<ComponentConnector> targets = new ArrayList<>(length);
        ConnectorMap connectorMap = ConnectorMap.get(connection);

        for (int i = 0; i < length; i++) {
            targets.add((ComponentConnector) connectorMap
                    .getConnector(targetIds.get(i)));
        }
        return targets;
    }

    /**
     * Returns a JsArrayString array of connectorIds for components that are
     * waiting for either horizontal or vertical measuring.
     *
     * @return JsArrayString of connectorIds
     */
    public JsArrayString getMeasureTargetsJsArray() {
        FastStringSet allMeasuredTargets = FastStringSet.create();
        allMeasuredTargets.addAll(getMeasureQueue(HORIZONTAL));
        allMeasuredTargets.addAll(getMeasureQueue(VERTICAL));
        return allMeasuredTargets.dump();
    }

    /**
     * Logs horizontal and vertical {@link LayoutDependency} state for the given
     * connector.
     *
     * @param connector
     *            the connector whose state to log, should not be {@code null}
     */
    public void logDependencyStatus(ComponentConnector connector) {
        getLogger().info("====");
        String connectorId = connector.getConnectorId();
        getLogger().info(getDependency(connectorId, HORIZONTAL).toString());
        getLogger().info(getDependency(connectorId, VERTICAL).toString());
    }

    /**
     * Returns whether all required layouting and measuring has been done for
     * this component to both directions and there are no more blockers waiting
     * for handling.
     *
     * @param connector
     *            the connector to check, should not be {@code null}
     * @return {@code true} if nothing is pending, {@code false} otherwise
     */
    public boolean noMoreChangesExpected(ComponentConnector connector) {
        return getDependency(connector.getConnectorId(), HORIZONTAL)
                .noMoreChangesExpected()
                && getDependency(connector.getConnectorId(), VERTICAL)
                        .noMoreChangesExpected();
    }

    /**
     * Returns the scrolling boundary for this component. If a cached value is
     * available, the check isn't performed again. If no cached value exists,
     * iterates through the component hierarchy until the closest parent that
     * implements {@link MayScrollChildren} has been found.
     *
     * @param connector
     *            the connector to check, should not be {@code null}
     * @return the closest scrolling parent or {@code null} if not found
     */
    public ComponentConnector getScrollingBoundary(
            ComponentConnector connector) {
        LayoutDependency dependency = getDependency(connector.getConnectorId(),
                HORIZONTAL);
        if (!dependency.scrollingParentCached) {
            ServerConnector parent = dependency.connector.getParent();
            if (parent instanceof MayScrollChildren) {
                dependency.scrollingBoundary = connector;
            } else if (parent instanceof ComponentConnector) {
                dependency.scrollingBoundary = getScrollingBoundary(
                        (ComponentConnector) parent);
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
