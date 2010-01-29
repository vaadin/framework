package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.Map;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class AcceptCriterionImpl {

    private final class OverTreeNode implements AcceptCriteria {
        public boolean accept(Transferable transferable, UIDL configuration) {
            Boolean containsKey = (Boolean) transferable
                    .getData("itemIdOverIsNode");
            if (containsKey != null && containsKey.booleanValue()) {
                return true;
            }
            return false;
        }
    }

    private final class ComponentCriteria implements AcceptCriteria {
        public boolean accept(Transferable transferable, UIDL configuration) {
            try {
                // FIXME should have access to client too, change transferrable
                // to DragEvent??
                Paintable component = transferable.getComponent();
                String requiredPid = configuration
                        .getStringAttribute("component");
                String pid = ((Widget) component).getElement()
                        .getPropertyString("tkPid");
                return pid.equals(requiredPid);
            } catch (Exception e) {
            }
            return false;
        }
    }

    private final class And implements AcceptCriteria {
        public boolean accept(Transferable transferable, UIDL configuration) {
            UIDL childUIDL = configuration.getChildUIDL(0);
            UIDL childUIDL2 = configuration.getChildUIDL(1);
            AcceptCriteria acceptCriteria = AcceptCriterion.get(childUIDL
                    .getStringAttribute("name"));
            AcceptCriteria acceptCriteria2 = AcceptCriterion.get(childUIDL2
                    .getStringAttribute("name"));
            if (acceptCriteria == null || acceptCriteria2 == null) {
                ApplicationConnection.getConsole().log(
                        "And criteria didn't found a chidl criteria");
                return false;
            }
            boolean accept = acceptCriteria.accept(transferable, childUIDL);
            boolean accept2 = acceptCriteria2.accept(transferable, childUIDL2);
            return accept && accept2;
        }
    }

    private final class AcceptAll implements AcceptCriteria {
        public boolean accept(Transferable transferable, UIDL configuration) {
            return true;
        }
    }

    private final class HasItemId implements AcceptCriteria {
        public boolean accept(Transferable transferable, UIDL configuration) {
            return transferable.getItemId() != null;
        }
    }

    /**
     * TODO this method could be written by generator
     * 
     * TODO consider moving implementations to top level classes
     * 
     * TODO use fully qualified names of server side counterparts as keys
     */
    public void populateCriterionMap(Map<String, AcceptCriteria> map) {
        AcceptCriteria crit;

        crit = new HasItemId();
        map.put("needsItemId", crit);

        crit = new AcceptAll();
        map.put("acceptAll", crit);

        crit = new And();
        map.put("and", crit);

        crit = new OverTreeNode();
        map.put("overTreeNode", crit);

        crit = new ComponentCriteria();
        map.put("component", crit);

    }
}
