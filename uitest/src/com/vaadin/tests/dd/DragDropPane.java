package com.vaadin.tests.dd;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.StreamVariable;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Label;

/**
 * replacement for a proto class to keep tests working
 */
public class DragDropPane extends DragAndDropWrapper implements DropHandler {

    private AbsoluteLayout root;
    private AcceptCriterion crit;

    public DragDropPane() {
        super(new AbsoluteLayout());
        root = (AbsoluteLayout) getCompositionRoot();
        setDropHandler(this);
        setDragStartMode(DragStartMode.COMPONENT);
    }

    public void addComponent(Component c) {
        root.addComponent(c);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void addComponent(Component l, String string) {
        root.addComponent(l, string);
    }

    public void setAcceptCriterion(AcceptCriterion crit) {
        this.crit = crit;
    }

    @Override
    public void drop(DragAndDropEvent event) {

        WrapperTargetDetails ed = (WrapperTargetDetails) event
                .getTargetDetails();
        Transferable ctr = event.getTransferable();
        // use "component" (from DragDropPane) if available, else take
        // the source component
        Component component = null;
        if (ctr instanceof WrapperTransferable) {
            component = ((WrapperTransferable) ctr).getDraggedComponent();
        } else if (ctr instanceof DataBoundTransferable) {
            // Item has been dragged, construct a Label from
            // Item id
            Label l = new Label();
            l.setSizeUndefined();
            l.setValue("ItemId : " + ((DataBoundTransferable) ctr).getItemId());
            component = l;
        }

        if (component != null) {

            if (component.getParent() != root) {

                root.addComponent(component);

                Integer left = ed.getAbsoluteLeft();
                Integer top = ed.getAbsoluteTop();

                MouseEventDetails eventDetails = ed.getMouseEvent();

                int clientX = eventDetails.getClientX();
                int clientY = eventDetails.getClientY();

                try {
                    root.getPosition(component).setTopValue(
                            Float.valueOf(clientY - top));
                    root.getPosition(component).setLeftValue(
                            Float.valueOf(clientX - left));
                } catch (Exception e) {
                }
            } else {
                // drag started and ended inside the this Pane

                MouseEventDetails start = ((WrapperTransferable) event
                        .getTransferable()).getMouseDownEvent();
                MouseEventDetails eventDetails = ed.getMouseEvent();

                int deltaX = eventDetails.getClientX() - start.getClientX();
                int deltaY = eventDetails.getClientY() - start.getClientY();

                ComponentPosition p = root.getPosition(component);
                p.setTopValue(p.getTopValue() + deltaY);
                p.setLeftValue(p.getLeftValue() + deltaX);

            }
        }

        else {
            // drag coming outside of Vaadin

            WrapperTransferable wtr = (WrapperTransferable) ctr;

            String object = wtr.getText();
            // String html = wtr.getHtml();
            // String url = (String) ctr.getData("Url");

            final Label l = new Label();
            l.setCaption("Generated from HTML5 drag:");
            if (object != null) {
                if (object.length() > 80) {
                    object = object.substring(0, 79);
                }
                l.setValue(object);
            } else {
                l.setValue("HTML5 dd");
            }

            Html5File[] files = wtr.getFiles();
            if (files != null) {
                for (Html5File html5File : files) {
                    l.setCaption(html5File.getFileName());
                    html5File.setStreamVariable(new StreamVariable() {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        @Override
                        public OutputStream getOutputStream() {
                            return byteArrayOutputStream;
                        }

                        @Override
                        public boolean listenProgress() {
                            return false;
                        }

                        @Override
                        public void onProgress(StreamingProgressEvent event) {
                        }

                        @Override
                        public void streamingStarted(StreamingStartEvent event) {
                        }

                        @Override
                        public void streamingFinished(StreamingEndEvent event) {
                            l.setValue((new String(byteArrayOutputStream
                                    .toByteArray()).substring(0, 80) + "..."));
                        }

                        @Override
                        public void streamingFailed(StreamingErrorEvent event) {
                        }

                        @Override
                        public boolean isInterrupted() {
                            return false;
                        }
                    });
                }
            }

            l.setSizeUndefined();

            root.addComponent(l);

        }
        return;
    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        return crit != null ? crit : AcceptAll.get();
    }

}
