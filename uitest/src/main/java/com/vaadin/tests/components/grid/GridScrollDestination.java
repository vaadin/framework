package com.vaadin.tests.components.grid;

import java.util.Arrays;
import java.util.stream.IntStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

public class GridScrollDestination extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Integer> grid = new Grid<>();
        grid.addColumn(Integer::intValue).setCaption("number");
        grid.setItems(IntStream.range(0, 100).boxed());
        TextField tf = new TextField("row index", "50");
        NativeSelect<ScrollDestination> ns = new NativeSelect<>(
                "scroll destination",
                Arrays.asList(ScrollDestination.values()));
        ns.setValue(ScrollDestination.ANY);
        Button button = new Button("Scroll to above row index", (event) -> {
            int rowIndex = Integer.parseInt(tf.getValue());
            grid.scrollTo(rowIndex, ns.getValue());
        });
        addComponents(tf, ns, button, grid);
    }

}
