/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import java.io.Serializable;

import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

/**
 * TODO Javadoc
 * 
 * @since 6.3
 * 
 */
public interface AcceptCriterion extends Serializable {

    /**
     * Criterion that can be used create policy to accept/discard dragged
     * content (presented by {@link Transferable}).
     * 
     * May depend on state, like in OR or AND, so to be really
     * ClientSideVerifiable needs to return true here (instead of just
     * implementing marker interface).
     */
    public boolean isClientSideVerifiable();

    public void paint(PaintTarget target) throws PaintException;

    /**
     * This needs to be implemented iff criterion does some lazy server side
     * initialization. The UIDL painted in this method will be passed to client
     * side drop handler implementation. Implementation can assume that
     * {@link #accepts(DragAndDropEvent)} is called before this method.
     * 
     * @param target
     * @throws PaintException
     */
    public void paintResponse(PaintTarget target) throws PaintException;

    /**
     * Validates the data in event to be approriate for
     * {@link DropHandler#drop(com.vaadin.event.dd.DropEvent)} method.
     * <p>
     * Note, that event if your criterion is matched on client side, it is a
     * very good manner to validate the data on server side too.
     * 
     * @param dragEvent
     * @return
     */
    public boolean accepts(DragAndDropEvent dragEvent);
}