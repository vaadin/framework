package com.vaadin.event;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.dd.DragAndDropManager.DragEventType;
import com.vaadin.ui.Component;

/**
 * An implementation of DropHandler interface.
 * 
 * AcceptCriterion may be used to configure accept rules. Using them can result
 * client side verifiable accept rules for quick feedback in UI. Still rules are
 * also validate on server so implementor don't need to double check validity on
 * {@link #receive(Transferable)} method.
 * 
 */
public abstract class AbstractDropHandler implements DropHandler {
    /**
     * Criterion that can be used create policy to accept/discard dragged
     * content (presented by {@link Transferable}).
     * 
     * TODO figure out how this can be done partly on client at least in some
     * cases. isClientSideFilterable() tms.
     * 
     */
    public interface AcceptCriterion {
        public boolean accepts(Transferable transferable);
    }

    public interface ClientSideVerifiable extends AcceptCriterion {

        /**
         * May depend on state, like in OR or AND, so to be really
         * ClientSideVerifiable needs to return true here (instead of just
         * implementing marker interface).
         */
        public boolean isClientSideVerifiable();

        public void paint(PaintTarget target) throws PaintException;

    }

    private static final class AcceptAll implements ClientSideVerifiable {
        public boolean accepts(Transferable transferable) {
            return true;
        }

        public boolean isClientSideVerifiable() {
            return true;
        }

        public void paint(PaintTarget target) throws PaintException {
            target.startTag("acceptCriterion");
            target.addAttribute("name", "acceptAll");
            target.endTag("acceptCriterion");
        }
    }

    public static class And implements ClientSideVerifiable {
        private AcceptCriterion f1;
        private AcceptCriterion f2;

        public And(AcceptCriterion f1, AcceptCriterion f2) {
            this.f1 = f1;
            this.f2 = f2;
        }

        public boolean accepts(Transferable transferable) {
            return f1.accepts(transferable) && f2.accepts(transferable);
        }

        public boolean isClientSideVerifiable() {
            boolean a1 = f1 instanceof ClientSideVerifiable ? ((ClientSideVerifiable) f1)
                    .isClientSideVerifiable()
                    : false;
            boolean a2 = f2 instanceof ClientSideVerifiable ? ((ClientSideVerifiable) f2)
                    .isClientSideVerifiable()
                    : false;
            return a1 && a2;
        }

        public void paint(PaintTarget target) throws PaintException {
            target.startTag("acceptCriterion");
            target.addAttribute("name", "and");
            ((ClientSideVerifiable) f1).paint(target);
            ((ClientSideVerifiable) f2).paint(target);
            target.endTag("acceptCriterion");
        }
    }

    public static class ComponentFilter implements ClientSideVerifiable {
        private Component component;

        public ComponentFilter(Component component) {
            this.component = component;
        }

        public boolean accepts(Transferable transferable) {
            if (transferable instanceof ComponentTransferrable) {
                return ((ComponentTransferrable) transferable)
                        .getSourceComponent() == component;
            } else {
                return false;
            }
        }

        public boolean isClientSideVerifiable() {
            return true;
        }

        public void paint(PaintTarget target) throws PaintException {
            target.startTag("acceptCriterion");
            target.addAttribute("name", "component");
            target.addAttribute("component", component);
            target.endTag("acceptCriterion");
        }
    }

    private static final class IsDataBinded implements ClientSideVerifiable {
        public boolean accepts(Transferable transferable) {
            if (transferable instanceof DataBindedTransferrable) {
                return ((DataBindedTransferrable) transferable).getItemId() != null;
            }
            return false;
        }

        public boolean isClientSideVerifiable() {
            return true;
        }

        public void paint(PaintTarget target) throws PaintException {
            target.startTag("acceptCriterion");
            target.addAttribute("name", "needsItemId");
            target.endTag("acceptCriterion");
        }
    }

    public class Not implements AcceptCriterion {
        private AcceptCriterion acceptCriterion;

        public Not(AcceptCriterion acceptCriterion) {
            this.acceptCriterion = acceptCriterion;
        }

        public boolean accepts(Transferable transferable) {
            return !acceptCriterion.accepts(transferable);
        }

    }

    public class Or implements AcceptCriterion {
        private AcceptCriterion f1;
        private AcceptCriterion f2;

        Or(AcceptCriterion f1, AcceptCriterion f2) {
            this.f1 = f1;
            this.f2 = f2;
        }

        public boolean accepts(Transferable transferable) {
            return f1.accepts(transferable) || f2.accepts(transferable);
        }
    }

    public static class OverTreeNode implements ClientSideVerifiable {

        public boolean accepts(Transferable transferable) {
            try {
                return transferable.getData("detail").toString().toLowerCase()
                        .equals("center");
            } catch (Exception e) {
                return false;
            }
        }

        public boolean isClientSideVerifiable() {
            return true;
        }

        public void paint(PaintTarget target) throws PaintException {
            target.startTag("acceptCriterion");
            target.addAttribute("name", "overTreeNode");
            target.endTag("acceptCriterion");
        }

    }

    public static final AcceptCriterion CRITERION_ACCEPT_ALL = new AcceptAll();

    public static final AcceptCriterion CRITERION_HAS_ITEM_ID = new IsDataBinded();

    private AcceptCriterion acceptCriterion = CRITERION_ACCEPT_ALL;

    /*
     * (non-Javadoc)
     * 
     * @seecom.vaadin.event.DropHandler#acceptTransferrable(com.vaadin.event.
     * Transferable)
     */
    public boolean acceptTransferrable(Transferable transferable) {
        return acceptCriterion.accepts(transferable);
    }

    private boolean clientSideVerifiable() {
        if (acceptCriterion instanceof ClientSideVerifiable) {
            return ((ClientSideVerifiable) acceptCriterion)
                    .isClientSideVerifiable();
        }
        return false;
    }

    public void handleDragRequest(DragRequest event) {
        boolean acceptTransferrable = acceptTransferrable(event
                .getTransferrable());
        if (acceptTransferrable) {
            if (event.getType() == DragEventType.DROP) {
                receive(event.getTransferrable());
            } else {
                event.setResponseParameter("accepted", true);
            }

        }
    }

    public void paint(PaintTarget target) throws PaintException {
        target.startTag("dh");
        if (!clientSideVerifiable()) {
            target.addAttribute("serverValidate", true);
        } else {
            ((ClientSideVerifiable) acceptCriterion).paint(target);
        }
        target.endTag("dh");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.event.DropHandler#receive(com.vaadin.event.Transferable)
     */
    public abstract void receive(Transferable transferable);

    public void setAcceptCriterion(AcceptCriterion acceptCriterion) {
        this.acceptCriterion = acceptCriterion;
    }

}
