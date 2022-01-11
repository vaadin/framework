package com.vaadin.tests.integration;

import java.io.Serializable;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.renderers.ImageRenderer;

import java.util.Iterator;

@Widgetset("com.vaadin.DefaultWidgetSet")
@Theme("valo")
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

        final BeanItemContainer<Country> container = new BeanItemContainer<Country>(Country.class);
        final Grid grid = new Grid(container);
        // TODO ImageRenderer does not support ClassResource
        grid.getColumn("icon").setWidth(50).setHeaderCaption("").setRenderer(new ImageRenderer());
        grid.getColumn("name").setWidth(100).setHeaderCaption("Country");
        grid.setColumns("icon", "name");
        container.addBean(new Country("Finland", "FI", new ThemeResource("fi.gif")));
        container.addBean(new Country("Sweden", "SE", new ThemeResource("se.gif")));
        grid.setHeight("200px");
        grid.setWidth("200px");
        layout.addComponent(grid);

        final Label selectedLabel = new LabelFromDesign();
        grid.addSelectionListener(new SelectionEvent.SelectionListener() {
            public void select(SelectionEvent selectionEvent) {
                Iterator<Object> iterator = selectionEvent.getSelected().iterator();
                if (iterator.hasNext()) {
                    selectedLabel.setValue(container.getItem(iterator.next()).getBean().getId());
                } else {
                    selectedLabel.setValue("");
                }
            }
        });
        layout.addComponent(selectedLabel);
    }

    @DesignRoot
    public static class LabelFromDesign extends Label {
        public LabelFromDesign() {
            Design.read(this);
        }
    }
}
