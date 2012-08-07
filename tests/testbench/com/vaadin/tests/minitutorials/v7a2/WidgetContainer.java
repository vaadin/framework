package com.vaadin.tests.minitutorials.v7a2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;

public class WidgetContainer extends AbstractComponentContainer {

    List<Component> children = new ArrayList<Component>();

    @Override
    public void addComponent(Component c) {
        children.add(c);
        super.addComponent(c);
        requestRepaint();
    }

    @Override
    public void removeComponent(Component c) {
        children.remove(c);
        super.removeComponent(c);
        requestRepaint();
    }

    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {
        int index = children.indexOf(oldComponent);
        if (index != -1) {
            children.remove(index);
            children.add(index, newComponent);
            fireComponentDetachEvent(oldComponent);
            fireComponentAttachEvent(newComponent);
            requestRepaint();
        }
    }

    @Override
    public int getComponentCount() {
        return children.size();
    }

    @Override
    public Iterator<Component> getComponentIterator() {
        return children.iterator();
    }
}