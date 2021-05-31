package com.vaadin.tests.integration;

import java.io.Serializable;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ServletIntegrationUI extends UI {

    public static class Country implements Serializable {
        private final String name;
        private final String id;
        private final Resource icon;

        public Country(String name, String id, Resource icon) {
            this.name = name;
            this.id = id;
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public Resource getIcon() {
            return icon;
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        final Grid<Country> grid = new Grid<>();
        // TODO ImageRenderer does not support ClassResource
        grid.addComponentColumn(country -> new Image(null, country.getIcon()))
                .setWidth(50).setCaption("");
        grid.addColumn(country -> country.getName()).setWidth(100)
                .setCaption("Country");
        grid.setItems(new Country("Finland", "FI", new ClassResource("fi.gif")),
                new Country("Sweden", "SE", new FlagSeResource()));
        grid.setHeight("200px");
        grid.setWidth("200px");
        layout.addComponent(grid);

        final Label selectedLabel = new LabelFromDesign();
        grid.addSelectionListener(event -> selectedLabel.setValue(
                event.getFirstSelectedItem().map(c -> c.getId()).orElse("")));
        layout.addComponent(selectedLabel);
    }

    @DesignRoot
    public static class LabelFromDesign extends Label {
        public LabelFromDesign() {
            Design.read(this);
        }
    }
}
