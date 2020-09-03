package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import java.util.stream.IntStream;
import java.util.stream.Stream;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridColumnWidthInsideWindowCompositeContent
        extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        Window compositeWindow = new Window();
        compositeWindow.setId("the-window");
        compositeWindow.setDraggable(true);
        compositeWindow.setHeight(600, Unit.PIXELS);
        compositeWindow.setWidth(400, Unit.PIXELS);
        compositeWindow.setContent(new WindowContent(compositeWindow));
        compositeWindow.setId("composite-itself");
        Button openCompositeWindow = new Button("Open composite Window",
                e -> getUI().addWindow(compositeWindow));
        openCompositeWindow.setId("open-composite");
        addComponent(openCompositeWindow);

        VerticalLayout nonCompositeLayout = new VerticalLayout();
        Window nonCompositeWindow = new Window();
        nonCompositeWindow.setDraggable(true);
        nonCompositeWindow.setHeight(600, Unit.PIXELS);
        nonCompositeWindow.setWidth(400, Unit.PIXELS);
        Grid grid = getGrid();
        VerticalLayout spacingLayout = new VerticalLayout();
        nonCompositeLayout.addComponents(spacingLayout, grid);
        nonCompositeWindow.setContent(nonCompositeLayout);

        Button openNonCompositeWindow = new Button("Open non-composite Window",
                e -> getUI().addWindow(nonCompositeWindow));
        openNonCompositeWindow.setId("open-non-composite");

        addComponent(openNonCompositeWindow);

    }

    private class WindowContent extends Composite {

        WindowContent(Window window) {
            VerticalLayout spacingLayout = new VerticalLayout();
            spacingLayout.setId("spacing-layout");
            VerticalLayout layout = new VerticalLayout(spacingLayout,
                    getGrid());
            layout.setId("composite-root-vl");
            setCompositionRoot(layout);
        }

    }

    private Grid getGrid() {
        Grid grid = new Grid<>();
        IntStream.range(0, 20).forEach(i -> grid.addColumn(model -> i));
        Stream<Object> modelStream = IntStream.range(0, 50)
                .mapToObj(i -> new Object());
        grid.setItems(modelStream);
        grid.setSizeFull();
        return grid;
    }

    @Override
    protected String getTestDescription() {
        return "Test column resizing after expanding a Grid which is a part of a Composite inside a Window";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12079;
    }
}
