package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ValueMap;
import com.vaadin.terminal.gwt.client.ui.dd.VDragAndDropManager.DragEventType;

public abstract class VAbstractDropHandler implements VDropHandler {

    private boolean serverValidate;
    private UIDL criterioUIDL;

    public void updateRules(UIDL uidl) {
        serverValidate = uidl.getBooleanAttribute("serverValidate");
        int childCount = uidl.getChildCount();
        for (int i = 0; i < childCount; i++) {
            UIDL childUIDL = uidl.getChildUIDL(i);
            if (childUIDL.getTag().equals("acceptCriterion")) {
                criterioUIDL = childUIDL;
                // TODO consider parsing the criteria tree here instead of
                // translating uidl during validates()

            }
        }
    }

    public boolean validateOnServer() {
        return serverValidate;
    }

    /**
     * Default implementation does nothing.
     */
    public void dragOver(VDragEvent currentDrag) {

    }

    /**
     * Default implementation does nothing.
     */
    public void dragLeave(VDragEvent drag) {
        // TODO Auto-generated method stub

    }

    /**
     * If transferrable is accepted (either via server visit or client side
     * rules) the default implementation calls {@link #dragAccepted(VDragEvent)}
     * method.
     */
    public void dragEnter(final VDragEvent drag) {
        if (serverValidate) {
            VDragAndDropManager.get().visitServer(DragEventType.ENTER,
                    new VAcceptCallback() {
                        public void handleResponse(ValueMap responseData) {
                            if (responseData.containsKey("accepted")) {
                                dragAccepted(drag);
                            }
                        }
                    });
        } else if (validates(drag)) {
            dragAccepted(drag);
        }
    }

    abstract protected void dragAccepted(VDragEvent drag);

    /**
     * Returns true if client side rules are met.
     * 
     * @param drag
     * @return
     */
    protected boolean validates(VDragEvent drag) {
        if (criterioUIDL != null) {
            String criteriaName = criterioUIDL.getStringAttribute("name");
            VAcceptCriteria acceptCriteria = VAcceptCriterion.get(criteriaName);
            if (acceptCriteria != null) {
                // ApplicationConnection.getConsole().log(
                // "Criteria : " + acceptCriteria.getClass().getName());
                return acceptCriteria.accept(drag, criterioUIDL);
            }
        }
        return false;
    }

    public boolean drop(VDragEvent drag) {
        if (serverValidate) {
            return true;
        } else {
            return validates(drag);
        }
    }

    public abstract Paintable getPaintable();

}
