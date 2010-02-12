package com.vaadin.ui;

import java.util.Map;

import com.vaadin.event.ComponentTransferable;
import com.vaadin.event.DataBindedTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.TargetDetailsImpl;
import com.vaadin.event.dd.acceptCriteria.AcceptAll;
import com.vaadin.event.dd.acceptCriteria.AcceptCriterion;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.MouseEventDetails;

/**
 * Test class that implements various component related Drag and Drop features.
 * 
 * TODO consider implementing this kind of general purpose layout class:
 * 
 * - extend simple component (CssLayout or CustomComponent, instead of absolute
 * layout)
 * 
 * - implement both drag source and drop handler with server side api
 * 
 * - implement html5 drop target etc
 * 
 * - include a lots of details for drop event (coordinates & sizes of drop
 * position and widget/Paintable hierarchy up to drop handler)
 * 
 * This way we could have one rather complex dd component that could be used (by
 * wrapping layouts) in most common situations with server side api. Core
 * layouts wouldn't need changes (and have regression risk/ performance
 * penalty).
 * 
 */
@SuppressWarnings("serial")
@ClientWidget(com.vaadin.terminal.gwt.client.ui.VDragDropPane.class)
public class DragDropPane extends AbsoluteLayout implements DropTarget {

    private DropHandler dropHandler;

    public DragDropPane(DropHandler dropHandler) {
        setWidth("400px");
        setHeight("300px");

        if (dropHandler == null) {
            this.dropHandler = new ImportPrettyMuchAnything();
        } else {
            this.dropHandler = dropHandler;
        }

    }

    public DragDropPane() {
        this(null);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        dropHandler.getAcceptCriterion().paint(target);
    }

    public DropHandler getDropHandler() {
        return dropHandler;
    }

    public static class ImportPrettyMuchAnything implements DropHandler {
        public void drop(DropEvent event) {
            DragDropPane pane = (DragDropPane) event.getDropTargetData()
                    .getTarget();

            DragEventDetails ed = (DragEventDetails) event.getDropTargetData();
            Transferable transferable = event.getTransferable();
            if (transferable instanceof ComponentTransferable) {
                ComponentTransferable ctr = (ComponentTransferable) transferable;
                Component component = ctr.getSourceComponent();

                if (component.getParent() != pane) {
                    if (transferable instanceof DataBindedTransferable) {
                        // Item has been dragged, construct a Label from
                        // Item id
                        Label l = new Label();
                        l.setSizeUndefined();
                        l.setValue("ItemId : "
                                + ((DataBindedTransferable) transferable)
                                        .getItemId());
                        pane.addComponent(l);
                        component = l;

                    } else {
                        // we have a component that is been dragged, add
                        // it
                        // to
                        // this
                        pane.addComponent(component);
                    }

                    Integer left = ed.getAbsoluteLeft();
                    Integer top = ed.getAbsoluteTop();

                    MouseEventDetails eventDetails = ed.getMouseEvent();

                    int clientX = eventDetails.getClientX();
                    int clientY = eventDetails.getClientY();

                    try {
                        pane.getPosition(component).setTopValue(clientY - top);
                        pane.getPosition(component)
                                .setLeftValue(clientX - left);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                } else {
                    // drag ended inside the this Pane

                    MouseEventDetails start = ed.getMouseDownEvent();
                    MouseEventDetails eventDetails = ed.getMouseEvent();

                    int deltaX = eventDetails.getClientX() - start.getClientX();
                    int deltaY = eventDetails.getClientY() - start.getClientY();

                    ComponentPosition p = pane.getPosition(component);
                    p.setTopValue(p.getTopValue() + deltaY);
                    p.setLeftValue(p.getLeftValue() + deltaX);

                }

            } else {
                // drag coming outside of Vaadin
                String object = (String) transferable.getData("text/plain");

                String content = (String) transferable.getData("fileContents");

                Label l = new Label();
                l.setCaption("Generated from HTML5 drag:");
                if (object != null) {
                    l.setValue(object);
                } else {
                    l.setValue("HTML5 dd");
                }

                l.setDescription(content);
                l.setSizeUndefined();

                pane.addComponent(l);

            }
            return;
        }

        public AcceptCriterion getAcceptCriterion() {
            return AcceptAll.get();
        }
    }

    class DragEventDetails extends TargetDetailsImpl {

        public DragEventDetails(Map<String, Object> rawVariables) {
            super(rawVariables);
        }

        public Integer getAbsoluteTop() {
            return (Integer) getData("absoluteTop");
        }

        public Integer getAbsoluteLeft() {
            return (Integer) getData("absoluteLeft");
        }

        public MouseEventDetails getMouseDownEvent() {
            return MouseEventDetails.deSerialize((String) getData("mouseDown"));
        }

        public MouseEventDetails getMouseEvent() {
            return MouseEventDetails
                    .deSerialize((String) getData("mouseEvent"));
        }

    }

    public TargetDetails translateDragDropDetails(
            Map<String, Object> clientVariables) {
        return new DragEventDetails(clientVariables);
    }

    public void setDropHandler(DropHandler dropHandler2) {
        dropHandler = dropHandler2;
        requestRepaint();
    }

}
