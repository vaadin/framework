package com.vaadin.event.dnd;

import java.lang.reflect.Method;
import java.util.Map;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.dnd.DropTargetRpc;
import com.vaadin.shared.ui.dnd.DropTargetState;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

public class DropTargetExtension extends AbstractExtension {

    public DropTargetExtension() {
        registerRpc((DropTargetRpc) (data, dropEffect) -> {
            DropEvent event = new DropEvent((AbstractComponent) getParent());
            event.setData(data);
            event.setDropEffect(dropEffect);

            fireEvent(event);
        });
    }

    @Override
    public void extend(AbstractClientConnector target) {
        super.extend(target);
    }

    public void setDropEffect(DropEffect dropEffect) {
        getState().dropEffect = dropEffect.name();
    }

    public Registration addDropListener(DropListener listener) {
        return addListener(DropEvent.class, listener, DropListener.DROP_METHOD);
    }

    @Override
    protected DropTargetState getState() {
        return (DropTargetState) super.getState();
    }

    @Override
    protected DropTargetState getState(boolean markAsDirty) {
        return (DropTargetState) super.getState(markAsDirty);
    }

    public enum DropEffect {
        /**
         * A copy of the source item is made at the new location.
         */
        COPY,

        /**
         * An item is moved to a new location.
         */
        MOVE,

        /**
         * A link is established to the source at the new location.
         */
        LINK,

        /**
         * The item may not be dropped.
         */
        NONE
    }

    public static class DropEvent extends Component.Event {

        private Map<String, String> data;
        private DropEffect dropEffect;

        DropEvent(Component source) {
            super(source);
        }

        void setData(Map<String, String> data) {
            this.data = data;
        }

        public String getData(String format) {
            return data != null ? data.get(format) : null;
        }

        void setDropEffect(String dropEffect) {
            this.dropEffect = DropEffect.valueOf(dropEffect);
        }

        public DropEffect getDropEffect() {
            return dropEffect;
        }
    }

    @FunctionalInterface
    public interface DropListener extends ConnectorEventListener {
        static final Method DROP_METHOD = ReflectTools
                .findMethod(DropListener.class, "drop", DropEvent.class);

        void drop(DropEvent event);
    }
}
