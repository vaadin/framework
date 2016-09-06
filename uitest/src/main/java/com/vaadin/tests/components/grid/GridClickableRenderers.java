package com.vaadin.tests.components.grid;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ImageRenderer;

public class GridClickableRenderers extends AbstractTestUI {

    private static class TestPOJO {
        String testText;
        String imageUrl;
        String buttonText;

        TestPOJO(String intValue, String imageUrl, String buttonText) {
            testText = intValue;
            this.imageUrl = imageUrl;
            this.buttonText = buttonText;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Label valueDisplayLabel = new Label();
        Grid<TestPOJO> grid = new Grid<>();
        grid.addColumn("images", pojo -> new ExternalResource(pojo.imageUrl),
                new ImageRenderer<>());
        grid.addColumn("buttons", pojo -> pojo.buttonText, new ButtonRenderer<>(
                event -> valueDisplayLabel.setValue(event.getItem().testText)));
        grid.setItems(new TestPOJO("first button clicked", "", "button 1 text"),
                new TestPOJO("second button clicked", "", "button 2 text"));
        addComponents(valueDisplayLabel, grid);
    }
}
