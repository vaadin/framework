package com.vaadin.tests.components.gridlayout;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class GridLayoutInForm extends TestBase {

    @Override
    protected void setup() {
        final List<String> propertyIds = new ArrayList<String>();
        for (int i = 0; i < 50; i++) {
            propertyIds.add("property " + i);
        }

        GridLayout gridLayout = new GridLayout();
        gridLayout.setSizeUndefined();
        gridLayout.setColumns(2);
        gridLayout.setSpacing(true);

        PropertysetItem item = new PropertysetItem();
        for (String propertyId : propertyIds) {
            item.addItemProperty(propertyId, new ObjectProperty<String>(
                    propertyId));
        }

        final Form form = new Form(gridLayout);
        form.setItemDataSource(item);

        form.setSizeUndefined();

        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(true);
        Panel panel = new Panel(panelLayout);
        panelLayout.addComponent(form);
        panel.setHeight("500px");

        addComponent(panel);

        addComponent(new Button("Use 15 first fields",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        form.setVisibleItemProperties(propertyIds
                                .subList(0, 15));
                    }
                }));
        addComponent(new Button("Use 15 last fields",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        form.setVisibleItemProperties(propertyIds.subList(35,
                                50));
                    }
                }));

        addComponent(new Button("Use all fields", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                form.setVisibleItemProperties(propertyIds);
            }
        }));
    }

    @Override
    protected String getDescription() {
        return "Changing the number of visible fields in a Form using a GridLayout with spacing should not cause additional empty space in the end of the GridLayout";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8855);
    }

}
