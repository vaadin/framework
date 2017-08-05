/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.client.ui.tree;

import java.util.Optional;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ui.composite.CompositeConnector;
import com.vaadin.client.ui.layout.ElementResizeEvent;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.client.widget.treegrid.TreeGrid;
import com.vaadin.shared.ui.Connect;
import com.vaadin.ui.Tree;

import elemental.util.Timer;

/**
 * Client side counterpart of the {@link Tree} component.
 *
 * @author Vaadin Ltd
 * @since
 */
@Connect(Tree.class)
public class TreeConnector extends CompositeConnector {

    /**
     * Delay in milliseconds after last element resize event.
     */
    private static final int RECALCULATION_DELAY = 300;

    /**
     * Tree grid widget.
     */
    private TreeGrid treeGrid;

    /**
     * Listener to handle tree grid's element resize events.
     */
    private ElementResizeListener elementResizeListener = this::onElementResize;

    /**
     * Timer to throttle column with recalculation on element resize event.
     */
    private Timer recalculationTrigger = new Timer() {

        @Override
        public void run() {
            treeGrid.recalculateColumnWidths();
        }
    };

    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent event) {
        super.onConnectorHierarchyChange(event);

        // Add or remove element resize listener when connector hierarchy
        // changes, since child connector isn't available on init

        // The composite connector's only child is tree grid connector
        Optional<ComponentConnector> treeGridConnector = getChildComponents()
                .stream().findFirst();
        if (treeGridConnector.isPresent()) {

            // If present, keep reference to tree grid widget and add element
            // resize listener
            treeGrid = (TreeGrid) treeGridConnector.get().getWidget();
            treeGridConnector.get().getLayoutManager()
                    .addElementResizeListener(treeGrid.getElement(),
                            elementResizeListener);
        } else {

            // If tree grid removed, remove element resize listener. This will
            // also execute before connector is unregistered
            event.getOldChildren().stream().findFirst().ifPresent(
                    oldConnector -> oldConnector.getLayoutManager()
                            .removeElementResizeListener(treeGrid.getElement(),
                                    elementResizeListener));
        }
    }

    /**
     * Method to be executed on tree grid's element resize event.
     *
     * @param event
     *         the element resize event
     */
    private void onElementResize(ElementResizeEvent event) {
        recalculationTrigger.schedule(RECALCULATION_DELAY);
    }
}
