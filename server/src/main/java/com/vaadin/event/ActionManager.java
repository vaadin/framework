/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

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
public class ActionManager implements Action.Container, Action.Handler,
        Action.Notifier {

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
            ownActions = new LinkedHashSet<Action>();
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
                actionHandlers = new LinkedHashSet<Handler>();
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

        LinkedHashSet<Action> actions = getActionSet(actionTarget, viewer);

        /*
         * Must repaint whenever there are actions OR if all actions have been
         * removed but still exist on client side
         */
        if (!actions.isEmpty() || clientHasActions) {
            actionMapper = new KeyMapper<Action>();

            paintTarget.addVariable((VariableOwner) viewer, "action", "");
            paintTarget.startTag("actions");

            for (final Action a : actions) {
                paintTarget.startTag("action");
                final String akey = actionMapper.key(a);
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
            Handler[] array = actionHandlers.toArray(new Handler[actionHandlers
                    .size()]);
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
        LinkedHashSet<Action> actions = new LinkedHashSet<Action>();
        if (ownActions != null) {
            actions.addAll(ownActions);

        }
        if (actionHandlers != null) {
            for (Action.Handler h : actionHandlers) {
                Action[] as = h.getActions(target, sender);
                if (as != null) {
                    for (Action a : as) {
                        actions.add(a);
                    }
                }
            }
        }
        return actions;
    }
}
