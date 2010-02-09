package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.event.AbstractDropHandler;
import com.vaadin.event.DropTarget;
import com.vaadin.event.Transferable;
import com.vaadin.event.AbstractDropHandler.AcceptCriterion;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public abstract class VAbstractDropHandler implements VDropHandler {

    private boolean mustValidateOnServer = true;
    private UIDL criterioUIDL;

    /**
     * Implementor/user of {@link VAbstractDropHandler} must pass the UIDL
     * painted by {@link AbstractDropHandler} (the server side counterpart) to
     * this method. Practically the details about {@link AcceptCriterion} are
     * saved.
     * 
     * @param uidl
     */
    public void updateRules(UIDL uidl) {
        mustValidateOnServer = uidl.getBooleanAttribute("serverValidate");
        int childCount = uidl.getChildCount();
        for (int i = 0; i < childCount; i++) {
            UIDL childUIDL = uidl.getChildUIDL(i);
            if (childUIDL.getTag().equals("acceptCriterion")) {
                criterioUIDL = childUIDL;
            }
        }
    }

    /**
     * Default implementation does nothing.
     */
    public void dragOver(VDragEvent currentDrag) {

    }

    /**
     * Default implementation does nothing. Implementors should clean possible
     * emphasis or drag icons here.
     */
    public void dragLeave(VDragEvent drag) {

    }

    /**
     * The default implementation in {@link VAbstractDropHandler} checks if the
     * Transferable is accepted.
     * <p>
     * If transferable is accepted (either via server visit or client side
     * rules) the default implementation calls abstract
     * {@link #dragAccepted(VDragEvent)} method.
     * <p>
     * If drop handler has distinct places where some parts may accept the
     * {@link Transferable} and others don't, one should use similar validation
     * logic in dragOver method and replace this method with empty
     * implementation.
     * 
     */
    public void dragEnter(final VDragEvent drag) {
        validate(new VAcceptCallback() {
            public void accepted() {
                dragAccepted(drag);
            }
        }, drag);
    }

    /**
     * This method is called when a valid drop location was found with
     * {@link AcceptCriterion} either via client or server side check.
     * <p>
     * Implementations can set some hints for users here to highlight that the
     * drag is on a valid drop location.
     * 
     * @param drag
     */
    abstract protected void dragAccepted(VDragEvent drag);

    protected void validate(VAcceptCallback cb, VDragEvent event) {
        if (mustValidateOnServer) {
            VDragAndDropManager.get().visitServer(cb);
        } else if (validateOnClient(event)) {
            cb.accepted();
        }
    }

    /**
     * Returns true if client side rules are met.
     * 
     * @param drag
     * @return
     */
    protected boolean validateOnClient(VDragEvent drag) {
        if (criterioUIDL != null) {
            String criteriaName = criterioUIDL.getStringAttribute("name");
            VAcceptCriteria acceptCriteria = VAcceptCriterion.get(criteriaName);
            if (acceptCriteria != null) {
                // ApplicationConnection.getConsole().log(
                // "Criteria : " + acceptCriteria.getClass().getName());
                return acceptCriteria.accept(drag, criterioUIDL);
            }
        }
        return false;
    }

    /**
     * The default implemmentation visits server if {@link AcceptCriterion} 
     * can't be verified on client or if {@link AcceptCriterion} are met on
     * client.
     */
    public boolean drop(VDragEvent drag) {
        if (mustValidateOnServer) {
            return true;
        } else {
            return validateOnClient(drag);
        }
    }

    /**
     * Returns the Paintable who owns this {@link VAbstractDropHandler}. Server
     * side counterpart of the Paintable is expected to implement
     * {@link DropTarget} interface.
     */
    public abstract Paintable getPaintable();

}
