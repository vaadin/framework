package com.vaadin.ui;

import java.util.Collection;
import java.util.Map;

import com.vaadin.event.ComponentTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragSource;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper;

@ClientWidget(VDragAndDropWrapper.class)
public class DragAndDropWrapper extends CustomComponent implements DropTarget,
        DragSource {

    public class DDWrapperTransferable implements ComponentTransferable {
        private final Map<String, Object> rawVariables;

        private DDWrapperTransferable(Map<String, Object> rawVariables) {
            this.rawVariables = rawVariables;
        }

        public void setData(String dataFlawor, Object value) {
            // TODO Auto-generated method stub

        }

        public Collection<String> getDataFlawors() {
            return rawVariables.keySet();
        }

        public Object getData(String dataFlawor) {
            return rawVariables.get(dataFlawor);
        }

        public Component getSourceComponent() {
            return DragAndDropWrapper.this;
        }

        /**
         * The component in wrapper that is being dragged or null if the
         * transferrable is not a component (most likely an html5 drag).
         * 
         * @return
         */
        public Component getDraggedComponent() {
            Component object = (Component) rawVariables.get("component");
            return object;
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

    public TargetDetails translateDragDropDetails(
            Map<String, Object> clientVariables) {
        // TODO Auto-generated method stub
        return null;
    }

    public Transferable getTransferable(Transferable transferable,
            final Map<String, Object> rawVariables) {
        return new DDWrapperTransferable(rawVariables);
    }

    public void setDragStartMode(DragStartMode dragStartMode) {
        this.dragStartMode = dragStartMode;
        requestRepaint();
    }

    public DragStartMode getDragStartMode() {
        return dragStartMode;
    }
}
