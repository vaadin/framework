package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ValueMap;
import com.vaadin.terminal.gwt.client.ui.dd.DragAndDropManager.DragEventType;

public abstract class AbstractDropHandler implements DropHandler {

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
    public void dragOver(DragEvent currentDrag) {

    }

    /**
     * Default implementation does nothing.
     */
    public void dragLeave(DragEvent drag) {
        // TODO Auto-generated method stub

    }

    /**
     * If transferrable is accepted (either via server visit or client side
     * rules) the default implementation calls {@link #dragAccepted(DragEvent)}
     * method.
     */
    public void dragEnter(final DragEvent drag) {
        if (serverValidate) {
            DragAndDropManager.get().visitServer(DragEventType.ENTER,
                    new AcceptCallback() {
                        public void handleResponse(ValueMap responseData) {
                            if (responseData.containsKey("accepted")) {
                                dragAccepted(drag);
                            }
                        }
                    });
        } else if (validates(drag.getTransferrable())) {
            dragAccepted(drag);
        }
    }

    abstract protected void dragAccepted(DragEvent drag);

    /**
     * Returns true if client side rules are met.
     * 
     * @param transferable
     * @return
     */
    protected boolean validates(Transferable transferable) {
        if (criterioUIDL != null) {
            String criteriaName = criterioUIDL.getStringAttribute("name");
            AcceptCriteria acceptCriteria = AcceptCriterion.get(criteriaName);
            if (acceptCriteria != null) {
                // ApplicationConnection.getConsole().log(
                // "Criteria : " + acceptCriteria.getClass().getName());
                return acceptCriteria.accept(transferable, criterioUIDL);
            }
        }
        return false;
    }

    public boolean drop(DragEvent drag) {
        if (serverValidate) {
            return true;
        } else {
            return validates(drag.getTransferrable());
        }
    }

    public abstract Paintable getPaintable();

}
