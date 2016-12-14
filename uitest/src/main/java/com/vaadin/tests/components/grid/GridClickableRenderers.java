package com.vaadin.tests.components.grid;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.CheckBoxRenderer;
import com.vaadin.ui.renderers.ImageRenderer;

public class GridClickableRenderers extends AbstractReindeerTestUI {

    private static class TestPOJO {
        String testText;
        String imageUrl;
        String buttonText;
        boolean truthValue;

        TestPOJO(String intValue, String imageUrl, String buttonText,
                boolean truthValue) {
            testText = intValue;
            this.imageUrl = imageUrl;
            this.buttonText = buttonText;
            this.truthValue = truthValue;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Label valueDisplayLabel = new Label("button click label");
        Label checkBoxValueLabel = new Label("checkbox click label");
        Grid<TestPOJO> grid = new Grid<>();

        grid.addColumn(pojo -> new ExternalResource(pojo.imageUrl),
                new ImageRenderer<>()).setId("images").setCaption("Images");
        grid.addColumn(pojo -> pojo.buttonText,
                new ButtonRenderer<>(event -> valueDisplayLabel
                        .setValue(event.getItem().testText + " clicked")))
                .setId("buttons").setCaption("Buttons");

        CheckBoxRenderer<TestPOJO> checkBoxRenderer = new CheckBoxRenderer<>(
                pojo -> pojo.truthValue,
                (pojo, newTruthValue) -> pojo.truthValue = newTruthValue);
        checkBoxRenderer.addClickListener(click -> checkBoxValueLabel.setValue(
                click.getItem().testText + " " + click.getItem().truthValue));
        grid.addColumn(pojo -> pojo.truthValue, checkBoxRenderer)
                .setId("checkboxes").setCaption("Checkboxes");

        grid.setItems(new TestPOJO("first row", "", "button 1 text", true),
                new TestPOJO("second row", "", "button 2 text", false));
        addComponents(valueDisplayLabel, checkBoxValueLabel, grid);
    }
}
