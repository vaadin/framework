/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.event.dd.acceptcriteria;

import java.io.Serializable;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

/**
 * Parent class for criteria that can be completely validated on client side.
 * All classes that provide criteria that can be completely validated on client
 * side should extend this class.
 * 
 * It is recommended that subclasses of ClientSideCriterion re-validate the
 * condition on the server side in
 * {@link AcceptCriterion#accept(com.vaadin.event.dd.DragAndDropEvent)} after
 * the client side validation has accepted a transfer.
 * 
 * @since 6.3
 */
public abstract class ClientSideCriterion implements Serializable,
        AcceptCriterion {

    /*
     * All criteria that extend this must be completely validatable on client
     * side.
     * 
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.event.dd.acceptCriteria.AcceptCriterion#isClientSideVerifiable
     * ()
     */
    public final boolean isClientSideVerifiable() {
        return true;
    }

    public void paint(PaintTarget target) throws PaintException {
        target.startTag("-ac");
        target.addAttribute("name", getIdentifier());
        paintContent(target);
        target.endTag("-ac");
    }

    protected void paintContent(PaintTarget target) throws PaintException {
    }

    protected String getIdentifier() {
        return getClass().getCanonicalName();
    }

    public final void paintResponse(PaintTarget target) throws PaintException {
        // NOP, nothing to do as this is client side verified criterion
    }

}
