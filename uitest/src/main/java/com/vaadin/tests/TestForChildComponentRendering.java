package com.vaadin.tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Select;

/**
 *
 * This Component contains some simple test to see that component updates its
 * contents propertly.
 *
 * @author Vaadin Ltd.
 */
public class TestForChildComponentRendering extends CustomComponent {

    private final VerticalLayout main;

    public TestForChildComponentRendering() {

        main = new VerticalLayout();
        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();
        main.addComponent(new Label("SDFGFHFHGJGFDSDSSSGFDD"));

        final Link l = new Link();
        l.setCaption("Siirry Vaadiniin");
        l.setResource(new ExternalResource("http://www.vaadin.com/"));
        l.setTargetHeight(200);
        l.setTargetWidth(500);
        l.setTargetBorder(Link.TARGET_BORDER_MINIMAL);
        main.addComponent(l);

        final Select se = new Select();
        se.setCaption("VALITSET TÄSTÄ");
        se.addItem("valinta1");
        se.addItem("Valinta 2");

        Button b = new Button("refresh view", event -> createNewView());
        main.addComponent(b);

        b = new Button("reorder view", event -> randomReorder());
        main.addComponent(b);

        b = new Button("remove randomly one component",
                event -> removeRandomComponent());
        main.addComponent(b);
    }

    public void randomReorder() {
        final Iterator<Component> it = main.getComponentIterator();
        final List<Component> components = new ArrayList<>();
        while (it.hasNext()) {
            components.add(it.next());
        }

        final VerticalLayout v = main;
        v.removeAllComponents();

        for (int i = components.size(); i > 0; i--) {
            final int index = (int) (Math.random() * i);
            v.addComponent(components.get(index));
            components.remove(index);
        }
    }

    public void removeRandomComponent() {
        final Iterator<Component> it = main.getComponentIterator();
        final List<Component> components = new ArrayList<>();
        while (it.hasNext()) {
            components.add(it.next());
        }
        final int size = components.size();
        final int index = (int) (Math.random() * size);
        main.removeComponent(components.get(index));

    }

}
