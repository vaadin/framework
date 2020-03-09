package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.Slider;

public class GridWithRendererWorkaround extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        List<Pojo> content = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            content.add(new Pojo(i % 3 == 0, i));
        }

        Grid<Pojo> grid = new Grid<>(DataProvider.ofCollection(content));
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setBodyRowHeight(70);
        grid.addComponentColumn(this::radioButtonResponse)
                .setCaption("Boolean");
        grid.addComponentColumn(this::sliderResponse).setCaption("Integer");

        addComponent(grid);
        getLayout().setSizeFull();
        getLayout().getParent().setSizeFull();
    }

    private RadioButtonGroup<Boolean> radioButtonResponse(Pojo item) {
        RadioButtonGroup<Boolean> yesNoRadioButtonGroup = new RadioButtonGroup<>();
        yesNoRadioButtonGroup.setItems(Boolean.TRUE, Boolean.FALSE);
        yesNoRadioButtonGroup.setSelectedItem(item.isBoolean());
        return yesNoRadioButtonGroup;
    }

    private Slider sliderResponse(Pojo item) {
        Slider s = new Slider(0, 4);
        s.setValue((double) ((item.getInt() % 4) + 1));
        return s;
    }

    private class Pojo {
        boolean b;
        int i;

        public Pojo(boolean b, int i) {
            this.b = b;
            this.i = i;
        }

        public boolean isBoolean() {
            return b;
        }

        public int getInt() {
            return i;
        }
    }

    @Override
    protected String getTestDescription() {
        return "Resizing should not hide RadioButtonGroup selections, "
                + "scrolling should not reveal Sliders with zero value.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11911;
    }
}
