/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.gwt.client.ui.dd.VAcceptAll;

/**
 * Criterion that accepts all drops anywhere on the component.
 * <p>
 * Note! Class is singleton, use {@link #get()} method to get the instance.
 * 
 * 
 * @since 6.3
 * 
 */
@ClientCriterion(VAcceptAll.class)
public final class AcceptAll extends ClientSideCriterion {

    private static final long serialVersionUID = 7406683402153141461L;
    private static AcceptCriterion singleton = new AcceptAll();

    private AcceptAll() {
    }

    public static AcceptCriterion get() {
        return singleton;
    }

    public boolean accept(DragAndDropEvent dragEvent) {
        return true;
    }
}