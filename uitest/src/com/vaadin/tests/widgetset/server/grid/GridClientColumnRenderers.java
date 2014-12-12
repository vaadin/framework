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
package com.vaadin.tests.widgetset.server.grid;

import java.util.Arrays;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.client.grid.GridClientColumnRendererConnector.Renderers;
import com.vaadin.tests.widgetset.client.grid.GridClientColumnRendererRpc;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Widgetset(TestingWidgetSet.NAME)
public class GridClientColumnRenderers extends UI {

    /**
     * Controls the grid on the client side
     */
    public static class GridController extends AbstractComponent {

        private GridClientColumnRendererRpc rpc() {
            return getRpcProxy(GridClientColumnRendererRpc.class);
        }

        /**
         * Adds a new column with a renderer to the grid.
         */
        public void addColumn(Renderers renderer) {
            rpc().addColumn(renderer);
        }

        /**
         * Tests detaching and attaching grid
         */
        public void detachAttach() {
            rpc().detachAttach();
        }

        /**
         * @since
         */
        public void triggerClientSorting() {
            rpc().triggerClientSorting();
        }

        /**
         * @since
         */
        public void triggerClientSortingTest() {
            rpc().triggerClientSortingTest();
        }

        /**
         * @since
         */
        public void shuffle() {
            rpc().shuffle();
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        final GridController controller = new GridController();
        final CssLayout controls = new CssLayout();
        final VerticalLayout content = new VerticalLayout();

        content.addComponent(controller);
        content.addComponent(controls);
        setContent(content);

        final NativeSelect select = new NativeSelect(
                "Add Column with Renderer", Arrays.asList(Renderers.values()));
        select.setItemCaptionMode(ItemCaptionMode.EXPLICIT);
        for (Renderers renderer : Renderers.values()) {
            select.setItemCaption(renderer, renderer.toString());
        }
        select.setValue(Renderers.TEXT_RENDERER);
        select.setNullSelectionAllowed(false);
        controls.addComponent(select);

        NativeButton addColumnBtn = new NativeButton("Add");
        addColumnBtn.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Renderers renderer = (Renderers) select.getValue();
                controller.addColumn(renderer);
            }
        });
        controls.addComponent(addColumnBtn);

        NativeButton detachAttachBtn = new NativeButton("DetachAttach");
        detachAttachBtn.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                controller.detachAttach();
            }
        });
        controls.addComponent(detachAttachBtn);

        NativeButton shuffleButton = new NativeButton("Shuffle");
        shuffleButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                controller.shuffle();
            }
        });
        controls.addComponent(shuffleButton);

        NativeButton sortButton = new NativeButton("Trigger sorting event");
        sortButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                controller.triggerClientSorting();
            }
        });
        controls.addComponent(sortButton);

        NativeButton testSortingButton = new NativeButton("Test sorting");
        testSortingButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                controller.triggerClientSortingTest();
            }
        });
        controls.addComponent(testSortingButton);

        Label console = new Label();
        console.setContentMode(ContentMode.HTML);
        console.setId("testDebugConsole");
        content.addComponent(console);
    }
}
