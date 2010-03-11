/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import java.util.Map;
import java.util.Set;

import com.vaadin.event.Transferable;
import com.vaadin.event.TransferableImpl;
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
import com.vaadin.ui.Upload.Receiver;

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

        public Html5File[] getFiles() {
            // TODO Auto-generated method stub
            return null;
        }

        public class Html5File {

            public String getFileName() {
                // TODO Auto-generated method stub
                return null;
            }

            // public int getFileSize() {
            // // TODO Auto-generated method stub
            // return 0;
            // }

            /**
             * HTML5 drags are read from client disk with a callback. This and
             * possibly long transfer time forces us to receive dragged file
             * contents with a callback.
             * 
             * @param receiver
             *            the callback that returns stream where the
             *            implementation writes the file contents as it arrives.
             */
            public void receive(Receiver receiver) {
                // TODO Auto-generated method stub

            }

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
        /**
         * {@link DragAndDropWrapper} does not start drag events at all
         */
        NONE,
        /**
         * The component on which the drag started will be shown as drag image.
         */
        COMPONENT,
        /**
         * The whole wrapper is used as a drag image when dragging.
         */
        WRAPPER
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

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);

        Set<String> keySet = variables.keySet();
        for (String string : keySet) {
            // TODO get files
        }
    }
}
