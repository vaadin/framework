package com.vaadin.tests.dd;

import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptCriteria.AcceptAll;
import com.vaadin.event.dd.acceptCriteria.AcceptCriterion;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Label;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;

/**
 * replacement for a proto class to keep tests working
 */
public class DragDropPane extends DragAndDropWrapper implements DropHandler {

    private AbsoluteLayout root;
    private AcceptCriterion crit;

    public DragDropPane() {
        super(new AbsoluteLayout());
        root = (AbsoluteLayout) getCompositionRoot();
        setDropHandler(this);
        setDragStartMode(DragStartMode.COMPONENT);
    }

    @Override
    public void addComponent(Component c) {
        root.addComponent(c);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void addComponent(Component l, String string) {
        root.addComponent(l, string);
    }

    public void setAcceptCriterion(AcceptCriterion crit) {
        this.crit = crit;
    }

    public void drop(DragAndDropEvent event) {

        WrapperDropDetails ed = (WrapperDropDetails) event
                .getDropTargetDetails();
        Transferable ctr = event.getTransferable();
        // use "component" (from DragDropPane) if available, else take
        // the source component
        Component component = null;
        if (ctr instanceof WrapperTransferable) {
            component = ((WrapperTransferable) ctr).getDraggedComponent();
        } else if (ctr instanceof DataBoundTransferable) {
            // Item has been dragged, construct a Label from
            // Item id
            Label l = new Label();
            l.setSizeUndefined();
            l.setValue("ItemId : " + ((DataBoundTransferable) ctr).getItemId());
            component = l;
        }

        if (component != null) {

            if (component.getParent() != root) {

                root.addComponent(component);

                Integer left = ed.getAbsoluteLeft();
                Integer top = ed.getAbsoluteTop();

                MouseEventDetails eventDetails = ed.getMouseEvent();

                int clientX = eventDetails.getClientX();
                int clientY = eventDetails.getClientY();

                try {
                    root.getPosition(component).setTopValue(clientY - top);
                    root.getPosition(component).setLeftValue(clientX - left);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            } else {
                // drag started and ended inside the this Pane

                MouseEventDetails start = ((WrapperTransferable) event
                        .getTransferable()).getMouseDownEvent();
                MouseEventDetails eventDetails = ed.getMouseEvent();

                int deltaX = eventDetails.getClientX() - start.getClientX();
                int deltaY = eventDetails.getClientY() - start.getClientY();

                ComponentPosition p = root.getPosition(component);
                p.setTopValue(p.getTopValue() + deltaY);
                p.setLeftValue(p.getLeftValue() + deltaX);

            }
        }

        else {
            // drag coming outside of Vaadin
            String object = (String) ctr.getData("text/plain");

            String content = (String) ctr.getData("fileContents");

            Label l = new Label();
            l.setCaption("Generated from HTML5 drag:");
            if (object != null) {
                l.setValue(object);
            } else {
                l.setValue("HTML5 dd");
            }

            l.setDescription(content);
            l.setSizeUndefined();

            root.addComponent(l);

        }
        return;
    }

    public AcceptCriterion getAcceptCriterion() {
        return crit != null ? crit : AcceptAll.get();
    }

}
