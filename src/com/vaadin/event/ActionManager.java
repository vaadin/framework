/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.event;

import java.util.HashSet;
import java.util.Map;

import com.vaadin.event.Action.Container;
import com.vaadin.event.Action.Handler;
import com.vaadin.terminal.KeyMapper;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
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

    /** List of action handlers */
    protected HashSet<Action> ownActions = null;

    /** List of action handlers */
    protected HashSet<Handler> actionHandlers = null;

    /** Action mapper */
    protected KeyMapper actionMapper = null;

    protected Component viewer;

    private boolean clientHasActions = false;

    public ActionManager() {

    }

    public <T extends Component & Container> ActionManager(T viewer) {
        this.viewer = viewer;
    }

    private void requestRepaint() {
        if (viewer != null) {
            viewer.requestRepaint();
        }
    }

    public <T extends Component & Container> void setViewer(T viewer) {
        if (viewer == this.viewer) {
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

    public <T extends Action & Action.Listener> void addAction(T action) {
        if (ownActions == null) {
            ownActions = new HashSet<Action>();
        }
        if (ownActions.add(action)) {
            requestRepaint();
        }
    }

    public <T extends Action & Action.Listener> void removeAction(T action) {
        if (ownActions != null) {
            if (ownActions.remove(action)) {
                requestRepaint();
            }
        }
    }

    public void addActionHandler(Handler actionHandler) {
        if (actionHandler == this) {
            // don't add the actionHandler to itself
            return;
        }
        if (actionHandler != null) {

            if (actionHandlers == null) {
                actionHandlers = new HashSet<Handler>();
            }

            if (actionHandlers.add(actionHandler)) {
                requestRepaint();
            }
        }
    }

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

        HashSet<Action> actions = new HashSet<Action>();
        if (actionHandlers != null) {
            for (Action.Handler handler : actionHandlers) {
                Action[] as = handler.getActions(actionTarget, viewer);
                if (as != null) {
                    for (Action action : as) {
                        actions.add(action);
                    }
                }
            }
        }
        if (ownActions != null) {
            actions.addAll(ownActions);
        }

        /*
         * Must repaint whenever there are actions OR if all actions have been
         * removed but still exist on client side
         */
        if (!actions.isEmpty() || clientHasActions) {
            actionMapper = new KeyMapper();

            paintTarget.addVariable(viewer, "action", "");
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
            final Action action = (Action) actionMapper.get(key);
            final Object target = variables.get("actiontarget");
            if (action != null) {
                handleAction(action, sender, target);
            }
        }
    }

    public Action[] getActions(Object target, Object sender) {
        HashSet<Action> actions = new HashSet<Action>();
        if (ownActions != null) {
            for (Action a : ownActions) {
                actions.add(a);
            }
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
        return actions.toArray(new Action[actions.size()]);
    }

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

}
