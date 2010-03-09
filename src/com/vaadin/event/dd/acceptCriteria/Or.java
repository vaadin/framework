/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.dd.VOr;

/**
 * TODO Javadoc
 * 
 * @since 6.3
 * 
 */
@ClientCriterion(VOr.class)
public class Or extends ClientSideCriterion {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private AcceptCriterion f1;
    private AcceptCriterion f2;

    public Or(ClientSideCriterion f1, ClientSideCriterion f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        f1.paint(target);
        f2.paint(target);
    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        return f1.accepts(dragEvent) || f2.accepts(dragEvent);
    }

}