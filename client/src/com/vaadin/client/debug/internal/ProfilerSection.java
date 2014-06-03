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
package com.vaadin.client.debug.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Profiler;
import com.vaadin.client.SimpleTree;
import com.vaadin.client.ValueMap;

/**
 * Debug window section for investigating {@link Profiler} data. This section is
 * only visible if the profiler is enabled ({@link Profiler#isEnabled()}).
 * 
 * @since 7.1
 * @author Vaadin Ltd
 * 
 * @see Profiler
 */
public class ProfilerSection implements Section {
    /**
     * Interface for getting data from the {@link Profiler}.
     * <p>
     * <b>Warning!</b> This interface is most likely to change in the future and
     * is therefore defined in this class in an internal package instead of
     * Profiler where it might seem more logical.
     * 
     * @since 7.1
     * @author Vaadin Ltd
     */
    public interface ProfilerResultConsumer {
        public void addProfilerData(Node rootNode, List<Node> totals);

        public void addBootstrapData(LinkedHashMap<String, Double> timings);
    }

    /**
     * A hierarchical representation of the time spent running a named block of
     * code.
     * <p>
     * <b>Warning!</b> This class is most likely to change in the future and is
     * therefore defined in this class in an internal package instead of
     * Profiler where it might seem more logical.
     */
    public static class Node {
        private final String name;
        private final LinkedHashMap<String, Node> children = new LinkedHashMap<String, Node>();
        private double time = 0;
        private int count = 0;
        private double enterTime = 0;
        private double minTime = 1000000000;
        private double maxTime = 0;

        /**
         * Create a new node with the given name.
         * 
         * @param name
         */
        public Node(String name) {
            this.name = name;
        }

        /**
         * Gets the name of the node
         * 
         * @return the name of the node
         */
        public String getName() {
            return name;
        }

        /**
         * Creates a new child node or retrieves and existing child and updates
         * its total time and hit count.
         * 
         * @param name
         *            the name of the child
         * @param timestamp
         *            the timestamp for when the node is entered
         * @return the child node object
         */
        public Node enterChild(String name, double timestamp) {
            Node child = children.get(name);
            if (child == null) {
                child = new Node(name);
                children.put(name, child);
            }
            child.enterTime = timestamp;
            child.count++;
            return child;
        }

        /**
         * Gets the total time spent in this node, including time spent in sub
         * nodes
         * 
         * @return the total time spent, in milliseconds
         */
        public double getTimeSpent() {
            return time;
        }

        /**
         * Gets the minimum time spent for one invocation of this node,
         * including time spent in sub nodes
         * 
         * @return the time spent for the fastest invocation, in milliseconds
         */
        public double getMinTimeSpent() {
            return minTime;
        }

        /**
         * Gets the maximum time spent for one invocation of this node,
         * including time spent in sub nodes
         * 
         * @return the time spent for the slowest invocation, in milliseconds
         */
        public double getMaxTimeSpent() {
            return maxTime;
        }

        /**
         * Gets the number of times this node has been entered
         * 
         * @return the number of times the node has been entered
         */
        public int getCount() {
            return count;
        }

        /**
         * Gets the total time spent in this node, excluding time spent in sub
         * nodes
         * 
         * @return the total time spent, in milliseconds
         */
        public double getOwnTime() {
            double time = getTimeSpent();
            for (Node node : children.values()) {
                time -= node.getTimeSpent();
            }
            return time;
        }

        /**
         * Gets the child nodes of this node
         * 
         * @return a collection of child nodes
         */
        public Collection<Node> getChildren() {
            return Collections.unmodifiableCollection(children.values());
        }

        private void buildRecursiveString(StringBuilder builder, String prefix) {
            if (getName() != null) {
                String msg = getStringRepresentation(prefix);
                builder.append(msg + '\n');
            }
            String childPrefix = prefix + "*";
            for (Node node : children.values()) {
                node.buildRecursiveString(builder, childPrefix);
            }
        }

        @Override
        public String toString() {
            return getStringRepresentation("");
        }

        public String getStringRepresentation(String prefix) {
            if (getName() == null) {
                return "";
            }
            String msg = prefix + " " + getName() + " in " + getTimeSpent()
                    + " ms.";
            if (getCount() > 1) {
                msg += " Invoked "
                        + getCount()
                        + " times ("
                        + roundToSignificantFigures(getTimeSpent() / getCount())
                        + " ms per time, min "
                        + roundToSignificantFigures(getMinTimeSpent())
                        + " ms, max "
                        + roundToSignificantFigures(getMaxTimeSpent())
                        + " ms).";
            }
            if (!children.isEmpty()) {
                double ownTime = getOwnTime();
                msg += " " + ownTime + " ms spent in own code";
                if (getCount() > 1) {
                    msg += " ("
                            + roundToSignificantFigures(ownTime / getCount())
                            + " ms per time)";
                }
                msg += '.';
            }
            return msg;
        }

        private static double roundToSignificantFigures(double num) {
            // Number of significant digits
            int n = 3;
            if (num == 0) {
                return 0;
            }

            final double d = Math.ceil(Math.log10(num < 0 ? -num : num));
            final int power = n - (int) d;

            final double magnitude = Math.pow(10, power);
            final long shifted = Math.round(num * magnitude);
            return shifted / magnitude;
        }

        public void sumUpTotals(Map<String, Node> totals) {
            String name = getName();
            if (name != null) {
                Node totalNode = totals.get(name);
                if (totalNode == null) {
                    totalNode = new Node(name);
                    totals.put(name, totalNode);
                }

                totalNode.time += getOwnTime();
                totalNode.count += getCount();
                totalNode.minTime = Math.min(totalNode.minTime,
                        getMinTimeSpent());
                totalNode.maxTime = Math.max(totalNode.maxTime,
                        getMaxTimeSpent());
            }
            for (Node node : children.values()) {
                node.sumUpTotals(totals);
            }
        }

        /**
         * @param timestamp
         */
        public void leave(double timestamp) {
            double elapsed = (timestamp - enterTime);
            time += elapsed;
            enterTime = 0;
            if (elapsed < minTime) {
                minTime = elapsed;
            }
            if (elapsed > maxTime) {
                maxTime = elapsed;
            }
        }
    }

    private static final int MAX_ROWS = 10;

    private final DebugButton tabButton = new DebugButton(Icon.RESET_TIMER,
            "Profiler");

    private final HorizontalPanel controls = new HorizontalPanel();
    private final FlowPanel content = new FlowPanel();

    public ProfilerSection() {
        Profiler.setProfilerResultConsumer(new ProfilerResultConsumer() {
            @Override
            public void addProfilerData(Node rootNode, List<Node> totals) {
                double totalTime = 0;
                int eventCount = 0;
                for (Node node : totals) {
                    totalTime += node.getTimeSpent();
                    eventCount += node.getCount();
                }

                SimpleTree drillDownTree = (SimpleTree) buildTree(rootNode);
                drillDownTree.setText("Drill down");

                SimpleTree offendersTree = new SimpleTree("Longest events");
                for (int i = 0; i < totals.size() && i < 20; i++) {
                    Node node = totals.get(i);
                    offendersTree.add(new Label(node
                            .getStringRepresentation("")));
                }

                SimpleTree root = new SimpleTree(eventCount
                        + " profiler events using " + totalTime + " ms");
                root.add(drillDownTree);
                root.add(offendersTree);
                root.open(false);

                content.add(root);
                applyLimit();
            }

            @Override
            public void addBootstrapData(LinkedHashMap<String, Double> timings) {
                SimpleTree tree = new SimpleTree(
                        "Time since window.performance.timing events");
                Set<Entry<String, Double>> entrySet = timings.entrySet();
                for (Entry<String, Double> entry : entrySet) {
                    tree.add(new Label(entry.getValue() + " " + entry.getKey()));
                }

                tree.open(false);
                content.add(tree);
                applyLimit();
            }
        });
    }

    private Widget buildTree(Node node) {
        String message = node.getStringRepresentation("");

        Collection<Node> children = node.getChildren();
        if (node.getName() == null || !children.isEmpty()) {
            SimpleTree tree = new SimpleTree(message);
            for (Node childNode : children) {
                Widget child = buildTree(childNode);
                tree.add(child);
            }
            return tree;
        } else {
            return new Label(message);
        }
    }

    private void applyLimit() {
        while (content.getWidgetCount() > MAX_ROWS) {
            content.remove(0);
        }
    }

    @Override
    public DebugButton getTabButton() {
        return tabButton;
    }

    @Override
    public Widget getControls() {
        return controls;
    }

    @Override
    public Widget getContent() {
        return content;
    }

    @Override
    public void show() {
        // Nothing to do
    }

    @Override
    public void hide() {
        // Nothing to do
    }

    @Override
    public void meta(ApplicationConnection ac, ValueMap meta) {
        // Nothing to do
    }

    @Override
    public void uidl(ApplicationConnection ac, ValueMap uidl) {
        // Nothing to do
    }

}
