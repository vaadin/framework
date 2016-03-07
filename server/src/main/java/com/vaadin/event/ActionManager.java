/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.event;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.event.Action.Container;
import com.vaadin.event.Action.Handler;
import com.vaadin.server.KeyMapper;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.VariableOwner;
import com.vaadin.ui.Component;

/**
 * Javadoc TODO
 *
 * Notes:
 * <p>
 * Empties the keymapper for each repaint to avoid leaks; can cause problems in
 * the future if the client assumes key don't change. (if lazyloading, one must
 * not cache results)
 * </p>
 *
 *
 */
public class ActionManager
        implements Action.Container, Action.Handler, Action.Notifier {

    private static final long serialVersionUID = 1641868163608066491L;

    /**
     * List of action handlers. Guaranteed to keep the original insertion order.
     */
    protected HashSet<Action> ownActions = null;

    /**
     * List of action handlers. Guaranteed to keep the original insertion order.
     */
    protected HashSet<Handler> actionHandlers = null;

    /** Action mapper */
    protected KeyMapper<Action> actionMapper = null;

    protected Component viewer;

    private boolean clientHasActions = false;

    public ActionManager() {

    }

    public <T extends Component & Container & VariableOwner> ActionManager(
            T viewer) {
        this.viewer = viewer;
    }

    private void requestRepaint() {
        if (viewer != null) {
            viewer.markAsDirty();
        }
    }

    public <T extends Component & Container & VariableOwner> void setViewer(
            T viewer) {
        // This somewhat complicated check exists to make sure that proxies are
        // handled correctly
        if (this.viewer == viewer
                || (this.viewer != null && this.viewer.equals(viewer))) {
            return;
        }
        if (this.viewer != null) {
            ((Container) this.viewer).removeActionHandler(this);
        }
        requestRepaint(); // this goes to the old viewer
        if (viewer != null) {
            viewer.addActionHandler(this);
        }
        this.viewer = viewer;
        requestRepaint(); // this goes to the new viewer
    }

    @Override
    public <T extends Action & Action.Listener> void addAction(T action) {
        if (ownActions == null) {
            ownActions = new LinkedHashSet<>();
        }
        if (ownActions.add(action)) {
            requestRepaint();
        }
    }

    @Override
    public <T extends Action & Action.Listener> void removeAction(T action) {
        if (ownActions != null) {
            if (ownActions.remove(action)) {
                requestRepaint();
            }
        }
    }

    @Override
    public void addActionHandler(Handler actionHandler) {
        if (equals(actionHandler)) {
            // don't add the actionHandler to itself
            return;
        }
        if (actionHandler != null) {

            if (actionHandlers == null) {
                actionHandlers = new LinkedHashSet<>();
            }

            if (actionHandlers.add(actionHandler)) {
                requestRepaint();
            }
        }
    }

    @Override
    public void removeActionHandler(Action.Handler actionHandler) {
        if (actionHandlers != null && actionHandlers.contains(actionHandler)) {

            if (actionHandlers.remove(actionHandler)) {
                requestRepaint();
            }
            if (actionHandlers.isEmpty()) {
                actionHandlers = null;
            }

        }
    }

    public void removeAllActionHandlers() {
        if (actionHandlers != null) {
            actionHandlers = null;
            requestRepaint();
        }
    }

    public void paintActions(Object actionTarget, PaintTarget paintTarget)
            throws PaintException {

        actionMapper = null;

        LinkedHashSet<Action> actions = getActionSet(actionTarget, viewer,true);

        /*
         * Must repaint whenever there are actions OR if all actions have been
         * removed but still exist on client side
         */
        if (!actions.isEmpty() || clientHasActions) {
            actionMapper = new ActionKeyMapper();

            paintTarget.addVariable((VariableOwner) viewer, "action", "");
            paintTarget.startTag("actions");

            for (final Action wrappedAction : actions) {

                paintTarget.startTag("action");
                final String akey = actionMapper.key(wrappedAction);

                final Action a = ConnectorActionWrapper.unwrap(wrappedAction);
                paintTarget.addAttribute("key", akey);
                if (a.getCaption() != null) {
                    paintTarget.addAttribute("caption", a.getCaption());
                }
                if (a.getIcon() != null) {
                    paintTarget.addAttribute("icon", a.getIcon());
                }
                if (a instanceof ShortcutAction) {
                    final ShortcutAction sa = (ShortcutAction) a;
                    paintTarget.addAttribute("kc", sa.getKeyCode());

                    final int[] modifiers = sa.getModifiers();
                    if (modifiers != null) {
                        final String[] smodifiers = new String[modifiers.length];
                        for (int i = 0; i < modifiers.length; i++) {
                            smodifiers[i] = String.valueOf(modifiers[i]);
                        }
                        paintTarget.addAttribute("mk", smodifiers);
                    }
                }
                paintTarget.endTag("action");
            }

            paintTarget.endTag("actions");
        }

        /*
         * Update flag for next repaint so we know if we need to paint empty
         * actions or not (must send actions is client had actions before and
         * all actions were removed).
         */
        clientHasActions = !actions.isEmpty();
    }

    public void handleActions(Map<String, Object> variables, Container sender) {
        if (variables.containsKey("action") && actionMapper != null) {
            final String key = (String) variables.get("action");
            final Action action = actionMapper.get(key);
            final Object target = variables.get("actiontarget");
            if (action != null) {
                handleAction(action, sender, target);
            }
        }
    }

    @Override
    public Action[] getActions(Object target, Object sender) {
        LinkedHashSet<Action> actions = getActionSet(target, sender);
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void handleAction(Action action, Object sender, Object target) {
        if (actionHandlers != null) {
            Handler[] array = actionHandlers
                    .toArray(new Handler[actionHandlers.size()]);
            for (Handler handler : array) {
                handler.handleAction(action, sender, target);
            }
        }
        if (ownActions != null && ownActions.contains(action)
                && action instanceof Action.Listener) {
            ((Action.Listener) action).handleAction(sender, target);
        }
    }

    private LinkedHashSet<Action> getActionSet(Object target, Object sender) {
        return getActionSet(target,sender,false);
    }
    private LinkedHashSet<Action> getActionSet(Object target, Object sender, boolean wrap) {
        String ownConnectorId = (viewer != null) ? viewer.getConnectorId() : "0";
        ConnectorActionWrapper wrapper = new ConnectorActionWrapper(ownConnectorId,wrap);

        LinkedHashSet<Action> actions = new LinkedHashSet<Action>();
        if (ownActions != null) {
            actions.addAll(wrapper.maybeWrap(ownActions));
        }
        if (actionHandlers != null) {
            for (Action.Handler h : actionHandlers) {
                String actionConnectorId = ownConnectorId;
                if (h instanceof ConnectorActionManager) {
                    actionConnectorId = ((ConnectorActionManager)h).getConnectorId();
                }
                wrapper = new ConnectorActionWrapper(actionConnectorId,wrap);
                Action[] as = h.getActions(target, sender);
                if (as != null) {
                    for (Action a : as) {
                        actions.add(wrapper.maybeWrap(a));
                    }
                }
            }
        }
        return actions;
    }

    // To ensure that an action triggered by the client actually belongs
    // to an attached connector, the action key generated by the action manager
    // and sent to the client will be prefixed with the id of the connector
    // that handles the action (when possible).
    // Without this prefix a wrong action could be executed (for instance
    // if a button is pressed multiple times but removed after the first server roundtrip)
    // because the key mapper (and the counter used to generate the keys)
    // is a renewed at every execution of paintAction method.
    /**
     * Helper class that wraps/unwraps actions to/from ConnectorAction
     */
    private static class ConnectorActionWrapper implements Serializable {
        private final boolean wrap;
        private final String connectorId;

        private ConnectorActionWrapper(String connectorId, boolean wrap) {
            this.wrap = wrap;
            this.connectorId = connectorId;
        }

        Action maybeWrap(Action action) {
            if (wrap) {
                return ConnectorActionWrapper.wrap(action, connectorId);
            }
            return action;
        }

        HashSet<Action> maybeWrap(HashSet<Action> ownActions) {
            if (wrap) {
                HashSet<Action> wrapped = new HashSet<Action>(ownActions.size());
                for (Action action : ownActions) {
                    wrapped.add(ConnectorActionWrapper.wrap(action,connectorId));
                }
                return wrapped;
            }
            return ownActions;
        }

        static Action unwrap(Action action) {
            if (action instanceof ConnectorAction) {
                return ((ConnectorAction)action).action;
            }
            return action;
        }
        static Action wrap(Action action, String connectorId) {
            if (action instanceof ConnectorAction) {
                return action;
            }
            return new ConnectorAction(action, connectorId);
        }

    }

    /**
     * A holder that associates an Action with the id of the connector
     * that handles it
     */
    private static class ConnectorAction extends Action {
        private final Action action;
        private final String connectorId;

        ConnectorAction(Action action, String connectorId) {
            super(action.getCaption(),action.getIcon());
            this.action = action;
            this.connectorId = connectorId;
        }

        Action getAction() {
            return action;
        }

        String getConnectorId() {
            return connectorId;
        }

    }

    /**
     * Extension of KeyMapper that manages keys prefixed
     * with the id of the connector associated to an Action
     */
    private static class ActionKeyMapper extends KeyMapper<Action> {

        private final Set<String> prefixes = new HashSet<String>();
        private final static Pattern PREFIXED_KEY = Pattern.compile("^([^_]+)_(.*)$");

        @Override
        public String key(Action o) {
            if (o instanceof ConnectorAction) {
                return prefixedKey(((ConnectorAction)o));
            }
            return super.key(o);
        }

        private String prefixedKey(ConnectorAction action) {
            prefixes.add(action.connectorId);
            return String.format("%s_%s",action.connectorId,super.key(action.action));
        }

        @Override
        public Action get(String key) {
            if (key != null) {
                Matcher matcher = PREFIXED_KEY.matcher(key);
                if (matcher.matches() && prefixes.contains(matcher.group(1))) {
                    return super.get(matcher.group(2));
                }
            }
            return super.get(key);
        }

        @Override
        public void removeAll() {
            super.removeAll();
            prefixes.clear();
        }
    }
}
