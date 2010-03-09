/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import java.util.Map;

import com.vaadin.event.TransferableImpl;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragSource;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.DropTargetDetails;
import com.vaadin.event.dd.DropTargetDetailsImpl;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper;
import com.vaadin.terminal.gwt.client.ui.dd.HorizontalDropLocation;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;

@ClientWidget(VDragAndDropWrapper.class)
public class DragAndDropWrapper extends CustomComponent implements DropTarget,
        DragSource {

    public class WrapperTransferable extends TransferableImpl {

        public WrapperTransferable(Component sourceComponent,
                Map<String, Object> rawVariables) {
            super(sourceComponent, rawVariables);
        }

        /**
         * The component in wrapper that is being dragged or null if the
         * transferrable is not a component (most likely an html5 drag).
         * 
         * @return
         */
        public Component getDraggedComponent() {
            Component object = (Component) getData("component");
            return object;
        }

        /**
         * @return the mouse down event that started the drag and drop operation
         */
        public MouseEventDetails getMouseDownEvent() {
            return MouseEventDetails.deSerialize((String) getData("mouseDown"));
        }

    }

    public class WrapperDropDetails extends DropTargetDetailsImpl {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public WrapperDropDetails(Map<String, Object> rawDropData) {
            super(rawDropData);
        }

        /**
         * @return the absolute position of wrapper on the page
         */
        public Integer getAbsoluteLeft() {
            return (Integer) getData("absoluteLeft");
        }

        /**
         * 
         * @return the absolute position of wrapper on the page
         */
        public Integer getAbsoluteTop() {
            return (Integer) getData("absoluteTop");
        }

        /**
         * @return details about the actual event that caused the event details.
         *         Practically mouse move or mouse up.
         */
        public MouseEventDetails getMouseEvent() {
            return MouseEventDetails
                    .deSerialize((String) getData("mouseEvent"));
        }

        public VerticalDropLocation verticalDropLocation() {
            return VerticalDropLocation
                    .valueOf((String) getData("verticalLocation"));
        }

        public HorizontalDropLocation horizontalDropLocation() {
            return HorizontalDropLocation
                    .valueOf((String) getData("horizontalLocation"));
        }

    }

    public enum DragStartMode {
        NONE, COMPONENT, WRAPPER
    }

    private DragStartMode dragStartMode = DragStartMode.NONE;

    public DragAndDropWrapper(Component root) {
        super(root);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute("dragStartMode", dragStartMode.ordinal());
        if (getDropHandler() != null) {
            getDropHandler().getAcceptCriterion().paint(target);
        }
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private DropHandler dropHandler;

    public DropHandler getDropHandler() {
        return dropHandler;
    }

    public void setDropHandler(DropHandler dropHandler) {
        this.dropHandler = dropHandler;
        requestRepaint();
    }

    public DropTargetDetails translateDropTargetDetails(
            Map<String, Object> clientVariables) {
        return new WrapperDropDetails(clientVariables);
    }

    public Transferable getTransferable(final Map<String, Object> rawVariables) {
        return new WrapperTransferable(this, rawVariables);
    }

    public void setDragStartMode(DragStartMode dragStartMode) {
        this.dragStartMode = dragStartMode;
        requestRepaint();
    }

    public DragStartMode getDragStartMode() {
        return dragStartMode;
    }
}
