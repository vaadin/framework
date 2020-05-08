package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class GridWithFullWidthComponents extends AbstractTestUI {
    private String s = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, "
            + "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

    @Override
    protected void setup(VaadinRequest request) {
        getPage().getStyles()
                .add(".v-grid .v-label, .v-grid .v-csslayout:not(:empty) { "
                        + "background-color: yellow; min-width: 300px; }");

        List<Integer> content = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            content.add(i);
        }

        Grid<Integer> grid = new Grid<>(DataProvider.ofCollection(content));
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setBodyRowHeight(70);
        grid.addComponentColumn(this::labelResponse).setCaption("Label");
        grid.addComponentColumn(this::hLayoutResponse)
                .setCaption("HorizontalLayout");
        grid.addComponentColumn(this::cssLayoutResponse)
                .setCaption("CssLayout");

        addComponent(grid);
    }

    private Label labelResponse(Integer item) {
        Label label = new Label(s);
        label.setWidthFull();
        return label;
    }

    private HorizontalLayout hLayoutResponse(Integer ite) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        for (int i = 0; i < 5; ++i) {
            layout.addComponent(new Button("Button" + i));
        }
        return layout;
    }

    private CssLayout cssLayoutResponse(Integer ite) {
        CssLayout layout = new CssLayout();
        layout.setWidthFull();
        for (int i = 0; i < 5; ++i) {
            layout.addComponent(new Button("Button" + i));
        }
        return layout;
    }

    @Override
    protected String getTestDescription() {
        return "All column contents are components with 100% width, "
                + "in first and third column the contents are styled "
                + "to have background color and minimum width of 300px. "
                + "Initial render and browser resize should behave accordingly.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11973;
    }
}
