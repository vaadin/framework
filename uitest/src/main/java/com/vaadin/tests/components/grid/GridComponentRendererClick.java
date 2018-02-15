package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ComponentRenderer;

public class GridComponentRendererClick extends AbstractTestUIWithLog {

    private static class TestPOJO {

        String customRendererText;
        String buttonRendererText;

        TestPOJO(String customRendererText, String buttonRendererText) {
            this.customRendererText = customRendererText;
            this.buttonRendererText = buttonRendererText;
        }

        public String getButtonRendererText() {
            return buttonRendererText;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Grid<TestPOJO> grid = new Grid<>();

        ComponentRenderer<TestPOJO> renderer = new ComponentRenderer<>();
        renderer.forwardSelection(grid);
        grid.addColumn(this::createLabelComponent, renderer).setId("images").setCaption("ComponentRenderers");

        ButtonRenderer<TestPOJO> buttonRenderer = new ButtonRenderer<>();
        buttonRenderer.setHtmlContentAllowed(true);
        buttonRenderer.addClickListener(event -> grid.select(event.getItem()));
        grid.addColumn(TestPOJO::getButtonRendererText, buttonRenderer)
                .setId("buttonRenderer")
                .setCaption("ButtonRenderers");

        grid.setItems(
                new TestPOJO("Custom render 1", "button 1 text"),
                new TestPOJO("Custom render 2", "button 2 text"));
        addComponent(grid);
    }

    private HorizontalLayout createLabelComponent(TestPOJO pojo) {
        Label label = new Label(pojo.customRendererText);
        label.setId("label_to_click");
        HorizontalLayout layout = new HorizontalLayout(label);
        layout.setMargin(false);
        layout.setSpacing(false);
        return layout;
    }
}
