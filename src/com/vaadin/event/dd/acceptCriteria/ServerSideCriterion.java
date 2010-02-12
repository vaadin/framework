package com.vaadin.event.dd.acceptCriteria;

import java.io.Serializable;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

public abstract class ServerSideCriterion implements Serializable,
        AcceptCriterion {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public final boolean isClientSideVerifiable() {
        return false;
    }

    public void paint(PaintTarget target) throws PaintException {
        target.startTag("-ac");
        target.addAttribute("name", getIdentifier());
        paintContent(target);
        target.endTag("-ac");
    }

    public void paintContent(PaintTarget target) {
    }

    public void paintResponse(PaintTarget target) throws PaintException {
    }

    protected String getIdentifier() {
        return ServerSideCriterion.class.getCanonicalName();
    }
}
