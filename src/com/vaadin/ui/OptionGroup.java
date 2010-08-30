/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.VOptionGroup;

/**
 * Configures select to be used as an option group.
 */
@SuppressWarnings("serial")
@ClientWidget(VOptionGroup.class)
public class OptionGroup extends AbstractSelect implements
        FieldEvents.BlurNotifier, FieldEvents.FocusNotifier {

    private Set<Object> disabledItemIds = new HashSet<Object>();

    public OptionGroup() {
        super();
    }

    public OptionGroup(String caption, Collection options) {
        super(caption, options);
    }

    public OptionGroup(String caption, Container dataSource) {
        super(caption, dataSource);
    }

    public OptionGroup(String caption) {
        super(caption);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute("type", "optiongroup");
        super.paintContent(target);
    }

    @Override
    protected void paintItem(PaintTarget target, Object itemId)
            throws PaintException {
        super.paintItem(target, itemId);
        if (!isItemEnabled(itemId)) {
            target.addAttribute("disabled", true);
        }
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);

        if (variables.containsKey(FocusEvent.EVENT_ID)) {
            fireEvent(new FocusEvent(this));
        }
        if (variables.containsKey(BlurEvent.EVENT_ID)) {
            fireEvent(new BlurEvent(this));
        }
    }

    public void addListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    public void removeListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

    public void addListener(FocusListener listener) {
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    public void removeListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);

    }

    @Override
    protected void setValue(Object newValue, boolean repaintIsNotNeeded) {
        if (repaintIsNotNeeded) {
            /*
             * Check that value from changeVariables() doesn't contain unallowed
             * selections: In the multi select mode, the user has selected or
             * deselected a disabled item. In the single select mode, the user
             * has selected a disabled item.
             */
            if (isMultiSelect()) {
                Set currentValueSet = (Set) getValue();
                Set newValueSet = (Set) newValue;
                for (Object itemId : currentValueSet) {
                    if (!isItemEnabled(itemId) && !newValueSet.contains(itemId)) {
                        requestRepaint();
                        return;
                    }
                }
                for (Object itemId : newValueSet) {
                    if (!isItemEnabled(itemId)
                            && !currentValueSet.contains(itemId)) {
                        requestRepaint();
                        return;
                    }
                }
            } else {
                if (newValue == null) {
                    newValue = getNullSelectionItemId();
                }
                if (!isItemEnabled(newValue)) {
                    requestRepaint();
                    return;
                }
            }
        }
        super.setValue(newValue, repaintIsNotNeeded);
    }

    /**
     * Sets an item disabled or enabled. In the multiselect mode, a disabled
     * item cannot be selected or deselected by the user. In the single
     * selection mode, a disable item cannot be selected.
     * 
     * However, programmatical selection or deselection of an disable item is
     * possible. By default, items are enabled.
     * 
     * @param itemId
     *            the id of the item to be disabled or enabled
     * @param enabled
     *            if true the item is enabled, otherwise the item is disabled
     */
    public void setItemEnabled(Object itemId, boolean enabled) {
        if (itemId != null) {
            if (enabled) {
                disabledItemIds.remove(itemId);
            } else {
                disabledItemIds.add(itemId);
            }
            requestRepaint();
        }
    }

    /**
     * Returns true if the item is enabled.
     * 
     * @param itemId
     *            the id of the item to be checked
     * @return true if the item is enabled, false otherwise
     * @see #setItemEnabled(Object, boolean)
     */
    public boolean isItemEnabled(Object itemId) {
        if (itemId != null) {
            return !disabledItemIds.contains(itemId);
        }
        return true;
    }
}
