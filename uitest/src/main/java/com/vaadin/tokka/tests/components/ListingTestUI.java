package com.vaadin.tokka.tests.components;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tokka.data.DataSource;
import com.vaadin.tokka.data.selection.SingleSelection;
import com.vaadin.tokka.ui.components.fields.TextField;
import com.vaadin.tokka.ui.components.grid.Grid;
import com.vaadin.tokka.ui.components.nativeselect.NativeSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

@Theme("valo")
public class ListingTestUI extends AbstractTestUI {

    static Random r = new Random();

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        NativeSelect<String> select = new NativeSelect<>(
                DataSource.create(createOptions(50)));
        layout.addComponent(select);

        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.addComponent(new Button("Notify", e -> select
                .getSelectionModel().getSelected()
                .forEach(s -> Notification.show(s))));
        hLayout.addComponent(new Button("Random select", e -> {
            DataSource<String, ?> ds = select.getDataSource();
            int skip = r.nextInt((int) ds.apply(null).count());
            ds.apply(null).skip(skip).findFirst()
                    .ifPresent(select.getSelectionModel()::select);
        }));

        TextField textField = new TextField();
        hLayout.addComponent(textField);
        hLayout.addComponent(new Button("Reset options",
                e -> select.setItems(createOptions(Integer.parseInt(textField
                        .getValue())))));

        layout.addComponent(hLayout);

        Grid<Bean> grid = new Grid<Bean>();
        addComponent(layout);
        layout.addComponent(grid);
        grid.addColumn("String Value", Bean::getValue);
        grid.addColumn("Integer Value", Bean::getIntVal);
        grid.addColumn("toString", Bean::toString);
        grid.setDataSource(DataSource.create(Bean.generateRandomBeans())
                .sortingBy(Comparator.comparing(Bean::getValue)));

        addComponent(new Button("Toggle Grid Selection",
                new Button.ClickListener() {

                    private boolean hasSelection = true;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (hasSelection) {
                            grid.setSelectionModel(null);
                        } else {
                            grid.setSelectionModel(new SingleSelection<>());
                        }
                        hasSelection = !hasSelection;
                    }
                }));

    }

    private List<String> createOptions(int max) {
        List<String> options = new ArrayList<>();
        for (int i = 0; i < max; ++i) {
            options.add("Option " + i);
        }
        return options;
    }

}