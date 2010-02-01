package com.vaadin.ui;

import java.util.Map;

import com.vaadin.event.AbstractDropHandler;
import com.vaadin.event.ComponentTransferable;
import com.vaadin.event.DataBindedTransferrable;
import com.vaadin.event.HasDropHandler;
import com.vaadin.event.Transferable;
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
public class DragDropPane extends AbsoluteLayout implements HasDropHandler {

    private AbstractDropHandler abstractDropHandler;

    public DragDropPane(AbstractDropHandler dropHandler) {
        setWidth("400px");
        setHeight("300px");

        if (dropHandler == null) {
            dropHandler = new AbstractDropHandler() {
                @Override
                public void receive(Transferable transferable,
                        Object dropDetails) {

                    DragEventDetails ed = (DragEventDetails) dropDetails;
                    if (transferable instanceof ComponentTransferable) {
                        ComponentTransferable ctr = (ComponentTransferable) transferable;
                        Component component = ctr.getSourceComponent();

                        if (component.getParent() != DragDropPane.this) {
                            if (transferable instanceof DataBindedTransferrable) {
                                // Item has been dragged, construct a Label from
                                // Item id
                                Label l = new Label();
                                l.setSizeUndefined();
                                l
                                        .setValue("ItemId : "
                                                + ((DataBindedTransferrable) transferable)
                                                        .getItemId());
                                DragDropPane.this.addComponent(l);
                                component = l;

                            } else {
                                // we have a component that is been dragged, add
                                // it
                                // to
                                // this
                                DragDropPane.this.addComponent(component);
                            }

                            Integer left = ed.getAbsoluteLeft();
                            Integer top = ed.getAbsoluteTop();

                            MouseEventDetails eventDetails = ed.getMouseEvent();

                            int clientX = eventDetails.getClientX();
                            int clientY = eventDetails.getClientY();

                            try {
                                DragDropPane.this.getPosition(component)
                                        .setTopValue(clientY - top);
                                DragDropPane.this.getPosition(component)
                                        .setLeftValue(clientX - left);
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                        } else {
                            // drag ended inside the this Pane

                            MouseEventDetails start = ed.getMouseDownEvent();
                            MouseEventDetails eventDetails = ed.getMouseEvent();

                            int deltaX = eventDetails.getClientX()
                                    - start.getClientX();
                            int deltaY = eventDetails.getClientY()
                                    - start.getClientY();

                            ComponentPosition p = DragDropPane.this
                                    .getPosition(component);
                            p.setTopValue(p.getTopValue() + deltaY);
                            p.setLeftValue(p.getLeftValue() + deltaX);

                        }

                    } else {
                        // drag coming outside of Vaadin
                        String object = (String) transferable
                                .getData("text/plain");

                        String content = (String) transferable
                                .getData("fileContents");

                        Label l = new Label();
                        l.setCaption("Generated from HTML5 drag:");
                        if (object != null) {
                            l.setValue(object);
                        } else {
                            l.setValue("HTML5 dd");
                        }

                        l.setDescription(content);
                        l.setSizeUndefined();

                        DragDropPane.this.addComponent(l);

                    }

                }
            };
            if (dropHandler instanceof AbstractDropHandler) {
                AbstractDropHandler new_name = dropHandler;
                new_name
                        .setAcceptCriterion(AbstractDropHandler.CRITERION_ACCEPT_ALL);
            }
        }
        abstractDropHandler = dropHandler;
    }

    public DragDropPane() {
        this(null);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        if (abstractDropHandler instanceof AbstractDropHandler) {
            AbstractDropHandler new_name = abstractDropHandler;
            new_name.paint(target);
        }
    }

    public AbstractDropHandler getDropHandler() {
        return abstractDropHandler;
    }

    class DragEventDetails {

        private Map<String, Object> vars;

        public DragEventDetails(Map<String, Object> rawVariables) {
            vars = rawVariables;
        }

        public Integer getAbsoluteTop() {
            return (Integer) vars.get("absoluteTop");
        }

        public Integer getAbsoluteLeft() {
            return (Integer) vars.get("absoluteLeft");
        }

        public MouseEventDetails getMouseDownEvent() {
            return MouseEventDetails
                    .deSerialize((String) vars.get("mouseDown"));
        }

        public MouseEventDetails getMouseEvent() {
            return MouseEventDetails.deSerialize((String) vars
                    .get("mouseEvent"));
        }

    }

    public Object getDragEventDetails(Map<String, Object> rawVariables) {
        return new DragEventDetails(rawVariables);
    }

}
