package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;

public class ComboBoxNewItemAdd2ndTime extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout mainLayout = new VerticalLayout();

        List<String> items = new ArrayList<>(
                Arrays.asList("blue", "red", "green"));
        ListDataProvider<String> dataProvider = new ListDataProvider<>(items);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setDataProvider(dataProvider);
        comboBox.setNewItemProvider(value -> {
            items.add(value);
            dataProvider.refreshItem(value);
            return Optional.ofNullable(value);
        });
        mainLayout.addComponent(comboBox);

        Button reloadDataProviderButton = new Button("Reload Data Provider");
        reloadDataProviderButton.addClickListener(event1 -> {
            items.clear();
            items.addAll(Arrays.asList("blue", "red", "green"));
            dataProvider.refreshAll();
        });
        mainLayout.addComponent(reloadDataProviderButton);

        Button getValueButton = new Button("Get Value");
        getValueButton.addClickListener(
                event1 -> event1.getButton().setCaption(comboBox.getValue()));
        mainLayout.addComponent(getValueButton);
        addComponent(mainLayout);
    }

    @Override
    protected Integer getTicketNumber() {
        return 11317;
    }
}
