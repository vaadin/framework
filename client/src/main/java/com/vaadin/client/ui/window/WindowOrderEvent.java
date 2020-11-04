/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.client.ui.window;

import java.util.ArrayList;

import com.google.gwt.event.shared.GwtEvent;
import com.vaadin.client.ui.VWindow;

/**
 * Event for window order position updates.
 *
 * @since 7.7.12
 *
 * @author Vaadin Ltd
 */
public class WindowOrderEvent extends GwtEvent<WindowOrderHandler> {

    private static final Type<WindowOrderHandler> TYPE = new Type<WindowOrderHandler>();

    private final ArrayList<VWindow> windows;

    /**
     * Creates a new event with the given order.
     *
     * @param windows
     *            The new order position for the VWindow
     */
    public WindowOrderEvent(ArrayList<VWindow> windows) {
        this.windows = windows;
    }

    @Override
    public Type<WindowOrderHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Returns windows in order.
     *
     * @return windows in the specific order
     */
    public VWindow[] getWindows() {
        return windows.toArray(new VWindow[windows.size()]);
    }

    @Override
    protected void dispatch(WindowOrderHandler handler) {
        handler.onWindowOrderChange(this);
    }

    /**
     * Gets the type of the event.
     *
     * @return the type of the event
     */
    public static Type<WindowOrderHandler> getType() {
        return TYPE;
    }

}
