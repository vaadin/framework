/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropTargetDetails;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.dd.VDropDetailEquals;

/**
 * Criterion for checking if drop target details contains the specific property
 * with the specific value. Currently only String values are supported.
 * 
 * @since 6.3
 * 
 *        TODO add support for other basic data types that we support in UIDL.
 * 
 */
@ClientCriterion(VDropDetailEquals.class)
public final class DropTargetDetailEquals extends ClientSideCriterion {

    private String propertyName;
    private String value;

    /**
     * Constructs a criterion which ensures that the value there is a value in
     * {@link DropTargetDetails} that equals the reference value.
     * 
     * @param dataFlavor
     *            the type of data to be checked
     * @param value
     *            the reference value to which the drop target detail will be
     *            compared
     */
    public DropTargetDetailEquals(String dataFlavor, String value) {
        propertyName = dataFlavor;
        this.value = value;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute("p", propertyName);
        target.addAttribute("v", value);
    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        Object data = dragEvent.getDropTargetDetails().getData(propertyName);
        return value.equals(data);
    }
}