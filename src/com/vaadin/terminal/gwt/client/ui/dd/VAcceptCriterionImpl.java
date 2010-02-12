package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class VAcceptCriterionImpl {

    private final class OverTreeNode implements VAcceptCriteria {
        public void accept(VDragEvent drag, UIDL configuration,
                VAcceptCallback callback) {
            Boolean containsKey = (Boolean) drag.getDropDetails().get(
                    "itemIdOverIsNode");
            if (containsKey != null && containsKey.booleanValue()) {
                callback.accepted(drag);
                return;
            }
            return;
        }

        public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
            return false;
        }
    }

    private final class ComponentCriteria implements VAcceptCriteria {
        public void accept(VDragEvent drag, UIDL configuration,
                VAcceptCallback callback) {
            try {
                Paintable component = drag.getTransferable().getComponent();
                String requiredPid = configuration
                        .getStringAttribute("component");
                Paintable paintable = VDragAndDropManager.get()
                        .getCurrentDropHandler().getApplicationConnection()
                        .getPaintable(requiredPid);
                if (paintable == component) {
                    callback.accepted(drag);
                }
            } catch (Exception e) {
            }
            return;
        }

        public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
            return false;
        }
    }

    private final class And implements VAcceptCriteria {
        private boolean b1;
        private boolean b2;
        private VAcceptCriteria crit1;
        private VAcceptCriteria crit2;

        public void accept(VDragEvent drag, UIDL configuration,
                VAcceptCallback callback) {
            if (crit1 == null) {
                crit1 = getCriteria(drag, configuration, 0);
                crit2 = getCriteria(drag, configuration, 1);
                if (crit1 == null || crit2 == null) {
                    ApplicationConnection.getConsole().log(
                            "And criteria didn't found a chidl criteria");
                    return;
                }
            }

            b1 = false;
            b2 = false;

            VAcceptCallback accept1cb = new VAcceptCallback() {
                public void accepted(VDragEvent event) {
                    b1 = true;
                }
            };
            VAcceptCallback accept2cb = new VAcceptCallback() {
                public void accepted(VDragEvent event) {
                    b2 = true;
                }
            };

            crit1.accept(drag, configuration.getChildUIDL(0), accept1cb);
            crit2.accept(drag, configuration.getChildUIDL(0), accept2cb);
            if (b1 && b2) {
                callback.accepted(drag);
            }
        }

        private VAcceptCriteria getCriteria(VDragEvent drag,
                UIDL configuration, int i) {
            UIDL childUIDL = configuration.getChildUIDL(i);
            return VAcceptCriterion.get(childUIDL.getStringAttribute("name"));
        }

        public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
            return false; // TODO enforce on server side
        }
    }

    private final class AcceptAll implements VAcceptCriteria {
        public void accept(VDragEvent drag, UIDL configuration,
                VAcceptCallback callback) {
            callback.accepted(drag);
        }

        public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
            return false;
        }
    }

    private final class HasItemId implements VAcceptCriteria {
        public void accept(VDragEvent drag, UIDL configuration,
                VAcceptCallback callback) {
            if (drag.getTransferable().getData("itemId") != null) {
                callback.accepted(drag);
            }
        }

        public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
            return false;
        }
    }

    private final class ServerAccept implements VAcceptCriteria {
        public void accept(final VDragEvent drag, UIDL configuration,
                final VAcceptCallback callback) {

            // TODO could easily cache the response for current drag event

            VDragEventServerCallback acceptCallback = new VDragEventServerCallback() {
                public void handleResponse(boolean accepted, UIDL response) {
                    if (accepted) {
                        callback.accepted(drag);
                    }
                }
            };
            VDragAndDropManager.get().visitServer(acceptCallback);
        }

        public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
            return true;
        }
    }

    private final class LazyInitItemIdentifiers implements VAcceptCriteria {
        private boolean loaded = false;
        private HashSet<String> hashSet;
        private VDragEvent lastDragEvent;

        public void accept(final VDragEvent drag, UIDL configuration,
                final VAcceptCallback callback) {
            if (lastDragEvent == null || lastDragEvent != drag) {
                loaded = false;
                lastDragEvent = drag;
            }
            if (loaded) {
                Object object = drag.getDropDetails().get("itemIdOver");
                if (hashSet.contains(object)) {
                    callback.accepted(drag);
                }
            } else {

                VDragEventServerCallback acceptCallback = new VDragEventServerCallback() {

                    public void handleResponse(boolean accepted, UIDL response) {
                        hashSet = new HashSet<String>();
                        String[] stringArrayAttribute = response
                                .getStringArrayAttribute("allowedIds");
                        for (int i = 0; i < stringArrayAttribute.length; i++) {
                            hashSet.add(stringArrayAttribute[i]);
                        }
                        loaded = true;
                        if (accepted) {
                            callback.accepted(drag);
                        }
                    }
                };

                VDragAndDropManager.get().visitServer(acceptCallback);
            }

        }

        public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
            return loaded;
        }
    }

    private Map<String, VAcceptCriteria> instances = new HashMap<String, VAcceptCriteria>();

    /**
     * TODO this class/method must be written by generator
     * 
     * TODO move implementations to top level classes.
     * 
     * TODO use fully qualified names of server side counterparts as keys
     */
    private void populateCriterionMap(Map<String, VAcceptCriteria> map) {
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

    public void init() {
        populateCriterionMap(instances);
    }

    public VAcceptCriteria get(String name) {
        // FIXME make all lazy inited and possibility to use instances per
        // handler
        if (name.equals("-ss")) {
            return new ServerAccept();
        } else if (name.equals("com.vaadin.ui.Tree.TreeDropCriterion")) {
            return new LazyInitItemIdentifiers();
        } else {
            return instances.get(name);
        }
    }
}
