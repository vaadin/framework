/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal;

import java.util.EventListener;

import com.vaadin.ui.Component;

/**
 * Interface provided to ease porting of Vaadin 6 components to Vaadin 7. By
 * implementing this interface your Component will be able to use
 * {@link #paintContent(PaintTarget)} and
 * {@link #changeVariables(Object, java.util.Map)} just like in Vaadin 6.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 * 
 */
public interface Vaadin6Component extends VariableOwner, Component,
        EventListener {

    /**
     * <p>
     * Paints the Paintable into a UIDL stream. This method creates the UIDL
     * sequence describing it and outputs it to the given UIDL stream.
     * </p>
     * 
     * <p>
     * It is called when the contents of the component should be painted in
     * response to the component first being shown or having been altered so
     * that its visual representation is changed.
     * </p>
     * 
     * @param target
     *            the target UIDL stream where the component should paint itself
     *            to.
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void paintContent(PaintTarget target) throws PaintException;

}
