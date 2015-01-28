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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Profiler;
import com.vaadin.client.Profiler.Node;
import com.vaadin.client.Profiler.ProfilerResultConsumer;
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
