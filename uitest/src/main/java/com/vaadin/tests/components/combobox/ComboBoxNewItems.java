package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ComboBoxNewItems extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        List<String> streets = new ArrayList<>(
                Arrays.asList("My Way", "Highway"));
        ListDataProvider<String> dataProvider = new ListDataProvider<>(streets);

        final ComboBox<String> streetField = new ComboBox<>();
        streetField.setDataProvider(dataProvider);
        streetField.setNewItemHandler(item -> {
            streets.add(item);
            dataProvider.refreshAll();
            streetField.setSelectedItem(item);
            streetField.markAsDirty();
        });

        Button assign = new Button("assign");
        assign.addClickListener(event -> {
            Notification.show("Street: " + streetField.getValue());
        });

        addComponents(streetField, assign);
    }

}
