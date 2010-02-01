package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.Map;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class VAcceptCriterionImpl {

    private final class OverTreeNode implements VAcceptCriteria {
        public boolean accept(VDragEvent drag, UIDL configuration) {
            Boolean containsKey = (Boolean) drag.getEventDetails().get(
                    "itemIdOverIsNode");
            if (containsKey != null && containsKey.booleanValue()) {
                return true;
            }
            return false;
        }
    }

    private final class ComponentCriteria implements VAcceptCriteria {
        public boolean accept(VDragEvent drag, UIDL configuration) {
            try {
                Paintable component = drag.getTransferrable().getComponent();
                String requiredPid = configuration
                        .getStringAttribute("component");
                Paintable paintable = VDragAndDropManager.get()
                        .getCurrentDropHandler().getApplicationConnection()
                        .getPaintable(requiredPid);
                return paintable == component;
            } catch (Exception e) {
            }
            return false;
        }
    }

    private final class And implements VAcceptCriteria {
        public boolean accept(VDragEvent drag, UIDL configuration) {
            UIDL childUIDL = configuration.getChildUIDL(0);
            UIDL childUIDL2 = configuration.getChildUIDL(1);
            VAcceptCriteria acceptCriteria = VAcceptCriterion.get(childUIDL
                    .getStringAttribute("name"));
            VAcceptCriteria acceptCriteria2 = VAcceptCriterion.get(childUIDL2
                    .getStringAttribute("name"));
            if (acceptCriteria == null || acceptCriteria2 == null) {
                ApplicationConnection.getConsole().log(
                        "And criteria didn't found a chidl criteria");
                return false;
            }
            boolean accept = acceptCriteria.accept(drag, childUIDL);
            boolean accept2 = acceptCriteria2.accept(drag, childUIDL2);
            return accept && accept2;
        }
    }

    private final class AcceptAll implements VAcceptCriteria {
        public boolean accept(VDragEvent drag, UIDL configuration) {
            return true;
        }
    }

    private final class HasItemId implements VAcceptCriteria {
        public boolean accept(VDragEvent drag, UIDL configuration) {
            return drag.getTransferrable().getItemId() != null;
        }
    }

    /**
     * TODO this method could be written by generator
     * 
     * TODO consider moving implementations to top level classes
     * 
     * TODO use fully qualified names of server side counterparts as keys
     */
    public void populateCriterionMap(Map<String, VAcceptCriteria> map) {
        VAcceptCriteria crit;

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
