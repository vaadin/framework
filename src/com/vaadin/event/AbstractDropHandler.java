package com.vaadin.event;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree.Location;
import com.vaadin.ui.Tree.TreeDropDetails;

/**
 * An implementation of DropHandler interface.
 * 
 * AcceptCriterion may be used to configure accept rules. Using them can result
 * client side verifiable accept rules for quick feedback in UI. Still rules are
 * also validate on server so implementor don't need to double check validity on
 * {@link #receive(Transferable)} method.
 * 
 */
public abstract class AbstractDropHandler implements DragDropHandler {
    /**
     * Criterion that can be used create policy to accept/discard dragged
     * content (presented by {@link Transferable}).
     * 
     */
    public interface AcceptCriterion {
        public boolean accepts(Transferable transferable,
                DragDropDetails dragDropDetails);
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

        public boolean accepts(Transferable transferable,
                DragDropDetails dragDropDetails) {
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

        public boolean accepts(Transferable transferable,
                DragDropDetails dragDropDetails) {
            return f1.accepts(transferable, dragDropDetails)
                    && f2.accepts(transferable, dragDropDetails);
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

        public boolean accepts(Transferable transferable,
                DragDropDetails dragDropDetails) {
            if (transferable instanceof ComponentTransferable) {
                return ((ComponentTransferable) transferable)
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
        public boolean accepts(Transferable transferable,
                DragDropDetails dragDropDetails) {
            if (transferable instanceof DataBindedTransferable) {
                return ((DataBindedTransferable) transferable).getItemId() != null;
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

        public boolean accepts(Transferable transferable,
                DragDropDetails dragDropDetails) {
            return !acceptCriterion.accepts(transferable, dragDropDetails);
        }

    }

    public class Or implements AcceptCriterion {
        private AcceptCriterion f1;
        private AcceptCriterion f2;

        Or(AcceptCriterion f1, AcceptCriterion f2) {
            this.f1 = f1;
            this.f2 = f2;
        }

        public boolean accepts(Transferable transferable,
                DragDropDetails dragDropDetails) {
            return f1.accepts(transferable, dragDropDetails)
                    || f2.accepts(transferable, dragDropDetails);
        }
    }

    public static class OverTreeNode implements ClientSideVerifiable {

        public boolean accepts(Transferable transferable,
                DragDropDetails dragDropDetails) {
            try {
                TreeDropDetails eventDetails = (TreeDropDetails) dragDropDetails;
                return eventDetails.getDropLocation() == Location.MIDDLE;
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
     * @seecom.vaadin.event.DropHandler#acceptTransferable(com.vaadin.event.
     * Transferable)
     */
    public boolean acceptTransferable(Transferable transferable,
            DragDropDetails dragDropDetails) {
        return acceptCriterion.accepts(transferable, dragDropDetails);
    }

    private boolean clientSideVerifiable() {
        if (acceptCriterion instanceof ClientSideVerifiable) {
            return ((ClientSideVerifiable) acceptCriterion)
                    .isClientSideVerifiable();
        }
        return false;
    }

    public void handleDragRequest(DragRequest event, Transferable transferable,
            DragDropDetails dragDropDetails) {
        boolean acceptTransferable = acceptTransferable(transferable,
                dragDropDetails);
        if (acceptTransferable) {
            event.setResponseParameter("accepted", true);
        }
    }

    public boolean drop(Transferable transferable,
            DragDropDetails dragDropDetails) {
        boolean acceptTransferable = acceptTransferable(transferable,
                dragDropDetails);
        if (acceptTransferable) {
            receive(transferable, dragDropDetails);
            return true;
        }
        return false;
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
    public abstract void receive(Transferable transferable,
            DragDropDetails dropDetails);

    public void setAcceptCriterion(AcceptCriterion acceptCriterion) {
        this.acceptCriterion = acceptCriterion;
    }

}
