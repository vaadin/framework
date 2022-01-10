/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.client.widgets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.OutlineStyle;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.FocusableFlowPanel;

/**
 * Focusable flow panel which fires focus/blur events if it or any of its child
 * is focused/blurred, but doesn't fire events if it happens between its content
 * (child) elements.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class ChildFocusAwareFlowPanel extends FocusableFlowPanel
        implements HasAllFocusHandlers {

    private class FocusBlurHandler implements BlurHandler, FocusHandler {

        private boolean blurOccurred;

        @Override
        public void onBlur(BlurEvent event) {
            blurOccurred = true;
            Scheduler.get().scheduleDeferred(() -> fireBlurEvent(event));
        }

        @Override
        public void onFocus(FocusEvent event) {
            if (!blurOccurred) {
                // no blur occurred before this focus event
                eventBus.fireEvent(event);
            } else {
                // blur occurred before this focus event
                // another component inside the panel was
                // blurred => do not fire the focus and set blurOccurred to
                // false, so
                // blur will not be fired, too
                blurOccurred = false;
            }
        }

        private void fireBlurEvent(BlurEvent event) {
            if (blurOccurred) {
                eventBus.fireEvent(event);
                blurOccurred = false;
            }
        }
    }

    private final HandlerManager eventBus;

    private final FocusBlurHandler handler = new FocusBlurHandler();

    private final Map<Widget, HandlerRegistration> focusRegistrations = new HashMap<>();
    private final Map<Widget, HandlerRegistration> blurRegistrations = new HashMap<>();

    /**
     * Creates a new panel instance.
     */
    public ChildFocusAwareFlowPanel() {
        eventBus = new HandlerManager(this);
        getElement().getStyle().setOutlineStyle(OutlineStyle.NONE);
        // The panel itself should not be focused.
        getElement().setTabIndex(-1);
        super.addFocusHandler(handler);
        super.addBlurHandler(handler);
    }

    @Override
    public void add(Widget widget) {
        super.add(widget);
        addHandlers(widget);
    }

    @Override
    public void clear() {
        super.clear();
        focusRegistrations.clear();
        blurRegistrations.clear();
    }

    @Override
    public void insert(Widget widget, int beforeIndex) {
        super.insert(widget, beforeIndex);
        addHandlers(widget);
    }

    @Override
    public boolean remove(int index) {
        Widget widget = getWidget(index);
        boolean isRemoved = super.remove(index);
        if (isRemoved) {
            removeHandlers(widget);
        }
        return isRemoved;
    }

    @Override
    public boolean remove(Widget widget) {
        boolean isRemoved = super.remove(widget);
        if (isRemoved) {
            removeHandlers(widget);
        }
        return isRemoved;
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return eventBus.addHandler(FocusEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return eventBus.addHandler(BlurEvent.getType(), handler);
    }

    @Override
    public void focus() {
        Iterator<Widget> it = iterator();
        if (it.hasNext()) {
            Widget child = it.next();
            if (child instanceof Focusable) {
                ((Focusable) child).setFocus(true);
            }
        }
    }

    /**
     * Put focus in the first child Widget that can be focused and is not
     * disabled.
     */
    public void focusFirstEnabledChild() {
        for (int i = 0; i < getWidgetCount(); i++) {
            Widget widget = getWidget(i);
            if (!(widget instanceof FocusWidget)) {
                continue;
            }
            FocusWidget focusableChild = (FocusWidget) widget;
            if (focusableChild.isEnabled()) {
                focusableChild.setFocus(true);
                break;
            }

        }

    }

    private void addHandlers(Widget widget) {
        if (focusRegistrations.containsKey(widget)) {
            assert blurRegistrations.containsKey(widget);
            return;
        }
        if (widget instanceof FocusWidget) {
            HandlerRegistration focusRegistration = ((FocusWidget) widget)
                    .addFocusHandler(handler);
            HandlerRegistration blurRegistration = ((FocusWidget) widget)
                    .addBlurHandler(handler);
            focusRegistrations.put(widget, focusRegistration);
            blurRegistrations.put(widget, blurRegistration);
        }
    }

    private void removeHandlers(Widget widget) {
        HandlerRegistration focusRegistration = focusRegistrations
                .remove(widget);
        if (focusRegistration != null) {
            focusRegistration.removeHandler();
        }
        HandlerRegistration blurRegistration = blurRegistrations.remove(widget);
        if (blurRegistration != null) {
            blurRegistration.removeHandler();
        }
    }

}
