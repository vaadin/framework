/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.Collection;
import java.util.Map;

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

}
