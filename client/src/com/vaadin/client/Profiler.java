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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.shared.GWT;

/**
 * Lightweight profiling tool that can be used to collect profiling data with
 * zero overhead unless enabled. To enable profiling, add
 * <code>&lt;set-property name="vaadin.profiler" value="true" /&gt;</code> to
 * your .gwt.xml file.
 *
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class Profiler {
    /**
     * Class to include using deferred binding to enable the profiling.
     *
     * @author Vaadin Ltd
     * @since 7.0.0
     */
    public static class EnabledProfiler extends Profiler {
        @Override
        protected boolean isImplEnabled() {
            return true;
        }
    }

    /**
     * Interface for getting data from the {@link Profiler}.
     * <p>
     * <b>Warning!</b> This interface is most likely to change in the future
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

    private static final String evtGroup = "VaadinProfiler";

    private static final class GwtStatsEvent extends JavaScriptObject {
        protected GwtStatsEvent() {
            // JSO constructor
        }

        private native String getEvtGroup()
        /*-{
            return this.evtGroup;
        }-*/;

        private native double getMillis()
        /*-{
            return this.millis;
        }-*/;

        private native String getSubSystem()
        /*-{
            return this.subSystem;
        }-*/;

        private native String getType()
        /*-{
            return this.type;
        }-*/;

        private native String getModuleName()
        /*-{
            return this.moduleName;
        }-*/;

        public final String getEventName() {
            String group = getEvtGroup();
            if (evtGroup.equals(group)) {
                return getSubSystem();
            } else {
                return group + "." + getSubSystem();
            }
        }
    }

    private static ProfilerResultConsumer consumer;

    /**
     * Checks whether the profiling gathering is enabled.
     *
     * @return <code>true</code> if the profiling is enabled, else
     *         <code>false</code>
     */
    public static boolean isEnabled() {
        // This will be fully inlined by the compiler
        Profiler create = GWT.create(Profiler.class);
        return create.isImplEnabled();
    }

    /**
     * Enters a named block. There should always be a matching invocation of
     * {@link #leave(String)} when leaving the block. Calls to this method will
     * be removed by the compiler unless profiling is enabled.
     *
     * @param name
     *            the name of the entered block
     */
    public static void enter(String name) {
        if (isEnabled()) {
            logGwtEvent(name, "begin");
        }
    }

    /**
     * Leaves a named block. There should always be a matching invocation of
     * {@link #enter(String)} when entering the block. Calls to this method will
     * be removed by the compiler unless profiling is enabled.
     *
     * @param name
     *            the name of the left block
     */
    public static void leave(String name) {
        if (isEnabled()) {
            logGwtEvent(name, "end");
        }
    }

    private static native final void logGwtEvent(String name, String type)
    /*-{
        $wnd.__gwtStatsEvent({
            evtGroup: @com.vaadin.client.Profiler::evtGroup,
            moduleName: @com.google.gwt.core.client.GWT::getModuleName()(),
            millis: (new Date).getTime(),
            sessionId: undefined,
            subSystem: name,
            type: type
        });
    }-*/;

    /**
     * Resets the collected profiler data. Calls to this method will be removed
     * by the compiler unless profiling is enabled.
     */
    public static void reset() {
        if (isEnabled()) {
            /*
             * Old implementations might call reset for initialization, so
             * ensure it is initialized here as well. Initialization has no side
             * effects if already done.
             */
            initialize();

            clearEventsList();
        }
    }

    /**
     * Initializes the profiler. This should be done before calling any other
     * function in this class. Failing to do so might cause undesired behavior.
     * This method has no side effects if the initialization has already been
     * done.
     * <p>
     * Please note that this method should be called even if the profiler is not
     * enabled because it will then remove a logger function that might have
     * been included in the HTML page and that would leak memory unless removed.
     * </p>
     *
     * @since 7.0.2
     */
    public static void initialize() {
        if (isEnabled()) {
            ensureLogger();
        } else {
            ensureNoLogger();
        }
    }

    /**
     * Outputs the gathered profiling data to the debug console.
     */
    public static void logTimings() {
        if (!isEnabled()) {
            getLogger().warning(
                    "Profiler is not enabled, no data has been collected.");
            return;
        }

        LinkedList<Node> stack = new LinkedList<Node>();
        Node rootNode = new Node(null);
        stack.add(rootNode);
        JsArray<GwtStatsEvent> gwtStatsEvents = getGwtStatsEvents();
        if (gwtStatsEvents.length() == 0) {
            getLogger()
                    .warning(
                            "No profiling events recorded, this might happen if another __gwtStatsEvent handler is installed.");
            return;
        }

        for (int i = 0; i < gwtStatsEvents.length(); i++) {
            GwtStatsEvent gwtStatsEvent = gwtStatsEvents.get(i);
            String eventName = gwtStatsEvent.getEventName();
            String type = gwtStatsEvent.getType();
            boolean isBeginEvent = "begin".equals(type);

            Node stackTop = stack.getLast();
            boolean inEvent = eventName.equals(stackTop.getName())
                    && !isBeginEvent;

            if (!inEvent && stack.size() >= 2
                    && eventName.equals(stack.get(stack.size() - 2).getName())
                    && !isBeginEvent) {
                // back out of sub event
                stackTop.leave(gwtStatsEvent.getMillis());
                stack.removeLast();
                stackTop = stack.getLast();

                inEvent = true;
            }

            if (type.equals("end")) {
                if (!inEvent) {
                    getLogger().severe(
                            "Got end event for " + eventName
                                    + " but is currently in "
                                    + stackTop.getName());
                    return;
                }
                Node previousStackTop = stack.removeLast();
                previousStackTop.leave(gwtStatsEvent.getMillis());
            } else {
                if (!inEvent) {
                    stackTop = stackTop.enterChild(eventName,
                            gwtStatsEvent.getMillis());
                    stack.add(stackTop);
                }
                if (!isBeginEvent) {
                    // Create sub event
                    stack.add(stackTop.enterChild(eventName + "." + type,
                            gwtStatsEvent.getMillis()));
                }
            }
        }

        if (stack.size() != 1) {
            getLogger().warning(
                    "Not all nodes are left, the last node is "
                            + stack.getLast().getName());
            return;
        }

        Map<String, Node> totals = new HashMap<String, Node>();
        rootNode.sumUpTotals(totals);

        ArrayList<Node> totalList = new ArrayList<Node>(totals.values());
        Collections.sort(totalList, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return (int) (o2.getTimeSpent() - o1.getTimeSpent());
            }
        });

        if (getConsumer() != null) {
            getConsumer().addProfilerData(stack.getFirst(), totalList);
        }
    }

    /**
     * Overridden in {@link EnabledProfiler} to make {@link #isEnabled()} return
     * true if GWT.create returns that class.
     *
     * @return <code>true</code> if the profiling is enabled, else
     *         <code>false</code>
     */
    protected boolean isImplEnabled() {
        return false;
    }

    /**
     * Outputs the time passed since various events recored in
     * performance.timing if supported by the browser.
     */
    public static void logBootstrapTimings() {
        if (isEnabled()) {
            double now = Duration.currentTimeMillis();

            String[] keys = new String[] { "navigationStart",
                    "unloadEventStart", "unloadEventEnd", "redirectStart",
                    "redirectEnd", "fetchStart", "domainLookupStart",
                    "domainLookupEnd", "connectStart", "connectEnd",
                    "requestStart", "responseStart", "responseEnd",
                    "domLoading", "domInteractive",
                    "domContentLoadedEventStart", "domContentLoadedEventEnd",
                    "domComplete", "loadEventStart", "loadEventEnd" };

            LinkedHashMap<String, Double> timings = new LinkedHashMap<String, Double>();

            for (String key : keys) {
                double value = getPerformanceTiming(key);
                if (value == 0) {
                    // Ignore missing value
                    continue;
                }
                timings.put(key, Double.valueOf(now - value));
            }

            if (timings.isEmpty()) {
                getLogger()
                        .info("Bootstrap timings not supported, please ensure your browser supports performance.timing");
                return;
            }

            if (getConsumer() != null) {
                getConsumer().addBootstrapData(timings);
            }
        }
    }

    private static final native double getPerformanceTiming(String name)
    /*-{
        if ($wnd.performance && $wnd.performance.timing && $wnd.performance.timing[name]) {
            return $wnd.performance.timing[name];
        } else {
            return 0;
        }
    }-*/;

    private static native JsArray<GwtStatsEvent> getGwtStatsEvents()
    /*-{
        return $wnd.vaadin.gwtStatsEvents || [];
    }-*/;

    /**
     * Add logger if it's not already there, also initializing the event array
     * if needed.
     */
    private static native void ensureLogger()
    /*-{
        if (typeof $wnd.__gwtStatsEvent != 'function') {
            if (typeof $wnd.vaadin.gwtStatsEvents != 'object') {
                $wnd.vaadin.gwtStatsEvents = [];
            }
            $wnd.__gwtStatsEvent = function(event) {
                $wnd.vaadin.gwtStatsEvents.push(event);
                return true;
            }
        }
    }-*/;

    /**
     * Remove logger function and event array if it seems like the function has
     * been added by us.
     */
    private static native void ensureNoLogger()
    /*-{
        if (typeof $wnd.vaadin.gwtStatsEvents == 'object') {
            delete $wnd.vaadin.gwtStatsEvents;
            if (typeof $wnd.__gwtStatsEvent == 'function') {
                $wnd.__gwtStatsEvent = function() { return true; };
            }
        }
    }-*/;

    private static native JsArray<GwtStatsEvent> clearEventsList()
    /*-{
        $wnd.vaadin.gwtStatsEvents = [];
    }-*/;

    /**
     * Sets the profiler result consumer that is used to output the profiler
     * data to the user.
     * <p>
     * <b>Warning!</b> This is internal API and should not be used by
     * applications or add-ons.
     *
     * @since 7.1.4
     * @param profilerResultConsumer
     *            the consumer that gets profiler data
     */
    public static void setProfilerResultConsumer(
            ProfilerResultConsumer profilerResultConsumer) {
        if (consumer != null) {
            throw new IllegalStateException("The consumer has already been set");
        }
        consumer = profilerResultConsumer;
    }

    private static ProfilerResultConsumer getConsumer() {
        return consumer;
    }

    private static Logger getLogger() {
        return Logger.getLogger(Profiler.class.getName());
    }

}
