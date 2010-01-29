package com.vaadin.event;

import com.vaadin.ui.Component;

/**
 * Implementing component most commonly has also setDropHandler method, but
 * not polluting interface here as component might also have internal
 * AbstractDropHandler implementation.
 * 
 */
public interface HasDropHandler extends Component {
    public DropHandler getDropHandler();
}