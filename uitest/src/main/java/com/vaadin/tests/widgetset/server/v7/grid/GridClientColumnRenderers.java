package com.vaadin.tests.widgetset.server.v7.grid;

import java.util.Arrays;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.client.v7.grid.GridClientColumnRendererConnector.Renderers;
import com.vaadin.tests.widgetset.client.v7.grid.GridClientColumnRendererRpc;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.NativeSelect;

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

        public void triggerClientSorting() {
            rpc().triggerClientSorting();
        }

        public void triggerClientSortingTest() {
            rpc().triggerClientSortingTest();
        }

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

        final NativeSelect select = new NativeSelect("Add Column with Renderer",
                Arrays.asList(Renderers.values()));
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
