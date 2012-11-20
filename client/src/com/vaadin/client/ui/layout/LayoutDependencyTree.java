/*
 * Copyright 2011 Vaadin Ltd.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ComponentContainerConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.ManagedLayout;
import com.vaadin.shared.AbstractComponentState;

public class LayoutDependencyTree {
    private class LayoutDependency {
        private final ComponentConnector connector;
        private final int direction;

        private boolean needsLayout = false;
        private boolean needsMeasure = false;

        private boolean scrollingParentCached = false;
        private ComponentConnector scrollingBoundary = null;

        private Set<ComponentConnector> measureBlockers = new HashSet<ComponentConnector>();
        private Set<ComponentConnector> layoutBlockers = new HashSet<ComponentConnector>();

        public LayoutDependency(ComponentConnector connector, int direction) {
            this.connector = connector;
            this.direction = direction;
        }

        private void addLayoutBlocker(ComponentConnector blocker) {
            boolean blockerAdded = layoutBlockers.add(blocker);
            if (blockerAdded && layoutBlockers.size() == 1) {
                if (needsLayout) {
                    getLayoutQueue(direction).remove(connector);
                } else {
                    // Propagation already done if needsLayout is set
                    propagatePotentialLayout();
                }
            }
        }

        private void removeLayoutBlocker(ComponentConnector blocker) {
            boolean removed = layoutBlockers.remove(blocker);
            if (removed && layoutBlockers.isEmpty()) {
                if (needsLayout) {
                    getLayoutQueue(direction).add((ManagedLayout) connector);
                } else {
                    propagateNoUpcomingLayout();
                }
            }
        }

        private void addMeasureBlocker(ComponentConnector blocker) {
            boolean blockerAdded = measureBlockers.add(blocker);
            if (blockerAdded && measureBlockers.size() == 1) {
                if (needsMeasure) {
                    getMeasureQueue(direction).remove(connector);
                } else {
                    propagatePotentialResize();
                }
            }
        }

        private void removeMeasureBlocker(ComponentConnector blocker) {
            boolean removed = measureBlockers.remove(blocker);
            if (removed && measureBlockers.isEmpty()) {
                if (needsMeasure) {
                    getMeasureQueue(direction).add(connector);
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
                    getMeasureQueue(direction).add(connector);
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
                getMeasureQueue(direction).remove(connector);
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
                    getLayoutQueue(direction).add((ManagedLayout) connector);
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
                getLayoutQueue(direction).remove(connector);
                propagateNoUpcomingLayout();
            }
        }

        private void propagatePotentialResize() {
            for (ComponentConnector needsSize : getNeedsSizeForLayout()) {
                LayoutDependency layoutDependency = getDependency(needsSize,
                        direction);
                layoutDependency.addLayoutBlocker(connector);
            }
        }

        private Collection<ComponentConnector> getNeedsSizeForLayout() {
            // Find all connectors that need the size of this connector for
            // layouting

            // Parent needs size if it isn't relative?
            // Connector itself needs size if it isn't undefined?
            // Children doesn't care?

            ArrayList<ComponentConnector> needsSize = new ArrayList<ComponentConnector>();

            if (!isUndefinedInDirection(connector, direction)) {
                needsSize.add(connector);
            }
            if (!isRelativeInDirection(connector, direction)) {
                ServerConnector parent = connector.getParent();
                if (parent instanceof ComponentConnector) {
                    needsSize.add((ComponentConnector) parent);
                }
            }

            return needsSize;
        }

        private void propagateNoUpcomingResize() {
            for (ComponentConnector mightNeedLayout : getNeedsSizeForLayout()) {
                LayoutDependency layoutDependency = getDependency(
                        mightNeedLayout, direction);
                layoutDependency.removeLayoutBlocker(connector);
            }
        }

        private void propagatePotentialLayout() {
            for (ComponentConnector sizeMightChange : getResizedByLayout()) {
                LayoutDependency layoutDependency = getDependency(
                        sizeMightChange, direction);
                layoutDependency.addMeasureBlocker(connector);
            }
        }

        private Collection<ComponentConnector> getResizedByLayout() {
            // Components that might get resized by a layout of this component

            // Parent never resized
            // Connector itself resized if undefined
            // Children resized if relative

            ArrayList<ComponentConnector> resized = new ArrayList<ComponentConnector>();
            if (isUndefinedInDirection(connector, direction)) {
                resized.add(connector);
            }

            if (connector instanceof ComponentContainerConnector) {
                ComponentContainerConnector container = (ComponentContainerConnector) connector;
                for (ComponentConnector child : container.getChildComponents()) {
                    if (isRelativeInDirection(child, direction)) {
                        resized.add(child);
                    }
                }
            }

            return resized;
        }

        private void propagateNoUpcomingLayout() {
            for (ComponentConnector sizeMightChange : getResizedByLayout()) {
                LayoutDependency layoutDependency = getDependency(
                        sizeMightChange, direction);
                layoutDependency.removeMeasureBlocker(connector);
            }
        }

        public void markSizeAsChanged() {
            // When the size has changed, all that use that size should be
            // layouted
            for (ComponentConnector connector : getNeedsSizeForLayout()) {
                LayoutDependency layoutDependency = getDependency(connector,
                        direction);
                if (connector instanceof ManagedLayout) {
                    layoutDependency.setNeedsLayout(true);
                } else {
                    // Should simulate setNeedsLayout(true) + markAsLayouted ->
                    // propagate needs measure
                    layoutDependency.propagatePostLayoutMeasure();
                }
            }

            // Should also go through the hierarchy to discover appeared or
            // disappeared scrollbars
            ComponentConnector scrollingBoundary = getScrollingBoundary(connector);
            if (scrollingBoundary != null) {
                getDependency(scrollingBoundary, getOppositeDirection())
                        .setNeedsMeasure(true);
            }

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
                    return getDependency(currentConnector,
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
            for (ComponentConnector resized : getResizedByLayout()) {
                LayoutDependency layoutDependency = getDependency(resized,
                        direction);
                layoutDependency.setNeedsMeasure(true);
            }

            // Special case for e.g. wrapping texts
            if (direction == HORIZONTAL && !connector.isUndefinedWidth()
                    && connector.isUndefinedHeight()) {
                LayoutDependency dependency = getDependency(connector, VERTICAL);
                dependency.setNeedsMeasure(true);
            }
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
            if (getLayoutQueue(direction).contains(connector)) {
                s += "In layout queue\n";
            }
            s += "Layout blockers: " + blockersToString(layoutBlockers) + "\n";

            if (needsMeasure) {
                s += "Needs measure\n";
            }
            if (getMeasureQueue(direction).contains(connector)) {
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

    private final Map<?, ?>[] dependenciesInDirection = new Map<?, ?>[] {
            new HashMap<ComponentConnector, LayoutDependency>(),
            new HashMap<ComponentConnector, LayoutDependency>() };

    private final Collection<?>[] measureQueueInDirection = new HashSet<?>[] {
            new HashSet<ComponentConnector>(),
            new HashSet<ComponentConnector>() };

    private final Collection<?>[] layoutQueueInDirection = new HashSet<?>[] {
            new HashSet<ComponentConnector>(),
            new HashSet<ComponentConnector>() };

    public void setNeedsMeasure(ComponentConnector connector,
            boolean needsMeasure) {
        setNeedsHorizontalMeasure(connector, needsMeasure);
        setNeedsVerticalMeasure(connector, needsMeasure);
    }

    public void setNeedsHorizontalMeasure(ComponentConnector connector,
            boolean needsMeasure) {
        LayoutDependency dependency = getDependency(connector, HORIZONTAL);
        dependency.setNeedsMeasure(needsMeasure);
    }

    public void setNeedsVerticalMeasure(ComponentConnector connector,
            boolean needsMeasure) {
        LayoutDependency dependency = getDependency(connector, VERTICAL);
        dependency.setNeedsMeasure(needsMeasure);
    }

    private LayoutDependency getDependency(ComponentConnector connector,
            int direction) {
        @SuppressWarnings("unchecked")
        Map<ComponentConnector, LayoutDependency> dependencies = (Map<ComponentConnector, LayoutDependency>) dependenciesInDirection[direction];
        LayoutDependency dependency = dependencies.get(connector);
        if (dependency == null) {
            dependency = new LayoutDependency(connector, direction);
            dependencies.put(connector, dependency);
        }
        return dependency;
    }

    @SuppressWarnings("unchecked")
    private Collection<ManagedLayout> getLayoutQueue(int direction) {
        return (Collection<ManagedLayout>) layoutQueueInDirection[direction];
    }

    @SuppressWarnings("unchecked")
    private Collection<ComponentConnector> getMeasureQueue(int direction) {
        return (Collection<ComponentConnector>) measureQueueInDirection[direction];
    }

    public void setNeedsHorizontalLayout(ManagedLayout layout,
            boolean needsLayout) {
        LayoutDependency dependency = getDependency(layout, HORIZONTAL);
        dependency.setNeedsLayout(needsLayout);
    }

    public void setNeedsVerticalLayout(ManagedLayout layout, boolean needsLayout) {
        LayoutDependency dependency = getDependency(layout, VERTICAL);
        dependency.setNeedsLayout(needsLayout);
    }

    public void markAsHorizontallyLayouted(ManagedLayout layout) {
        LayoutDependency dependency = getDependency(layout, HORIZONTAL);
        dependency.markAsLayouted();
    }

    public void markAsVerticallyLayouted(ManagedLayout layout) {
        LayoutDependency dependency = getDependency(layout, VERTICAL);
        dependency.markAsLayouted();
    }

    public void markHeightAsChanged(ComponentConnector connector) {
        LayoutDependency dependency = getDependency(connector, VERTICAL);
        dependency.markSizeAsChanged();
    }

    public void markWidthAsChanged(ComponentConnector connector) {
        LayoutDependency dependency = getDependency(connector, HORIZONTAL);
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

    private static String getCompactConnectorString(ComponentConnector connector) {
        return Util.getSimpleName(connector) + " ("
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

    private static String blockersToString(
            Collection<ComponentConnector> blockers) {
        StringBuilder b = new StringBuilder("[");
        for (ComponentConnector blocker : blockers) {
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

    public ManagedLayout[] getHorizontalLayoutTargets() {
        Collection<ManagedLayout> queue = getLayoutQueue(HORIZONTAL);
        return queue.toArray(new ManagedLayout[queue.size()]);
    }

    public ManagedLayout[] getVerticalLayoutTargets() {
        Collection<ManagedLayout> queue = getLayoutQueue(VERTICAL);
        return queue.toArray(new ManagedLayout[queue.size()]);
    }

    public Collection<ComponentConnector> getMeasureTargets() {
        Collection<ComponentConnector> measureTargets = new HashSet<ComponentConnector>(
                getMeasureQueue(HORIZONTAL));
        measureTargets.addAll(getMeasureQueue(VERTICAL));
        return measureTargets;
    }

    public void logDependencyStatus(ComponentConnector connector) {
        VConsole.log("====");
        VConsole.log(getDependency(connector, HORIZONTAL).toString());
        VConsole.log(getDependency(connector, VERTICAL).toString());
    }

    public boolean noMoreChangesExpected(ComponentConnector connector) {
        return getDependency(connector, HORIZONTAL).noMoreChangesExpected()
                && getDependency(connector, VERTICAL).noMoreChangesExpected();
    }

    public ComponentConnector getScrollingBoundary(ComponentConnector connector) {
        LayoutDependency dependency = getDependency(connector, HORIZONTAL);
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
}
