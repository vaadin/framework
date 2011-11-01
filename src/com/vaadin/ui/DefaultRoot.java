package com.vaadin.ui;

import java.util.Collections;
import java.util.Iterator;

import com.vaadin.Application;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.gwt.client.ui.VView;

@ClientWidget(VView.class)
public class DefaultRoot extends AbstractComponentContainer implements Root {
    private final Component content;
    private Terminal terminal;
    private final Application application;

    public DefaultRoot(Application application, Component content) {
        this.application = application;
        this.content = content;
        addComponent(content);
    }

    @Override
    public Root getRoot() {
        return this;
    }

    public void replaceComponent(Component oldComponent, Component newComponent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        content.paint(target);
    }

    public Iterator<Component> getComponentIterator() {
        return Collections.singleton(content).iterator();
    }

    public String getName() {
        return "";
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }
}
