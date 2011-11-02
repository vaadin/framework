package com.vaadin.ui;

import java.util.Collection;

import com.vaadin.Application;
import com.vaadin.terminal.Terminal;

public interface Root extends Component, com.vaadin.ui.Component.Focusable {

    /**
     * Sets the application this root is attached to.
     * 
     * <p>
     * This method is called by the framework and should not be called directly
     * from application code.
     * </p>
     * 
     * @param application
     *            the application the root is attached to
     */
    public void setApplication(Application application);

    // TODO is this required?
    public String getName();

    public Terminal getTerminal();

    public void setTerminal(Terminal terminal);

    public void addWindow(Window window);

    public boolean removeWindow(Window window);

    public Collection<Window> getWindows();

    public void setFocusedComponent(Focusable focusable);

}
