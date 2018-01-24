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

package com.vaadin.ui;

import com.vaadin.shared.ui.windowdesktop.WindowDesktopState;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * A component that represente a Desktop in where a SubWindow 
 * can be added and float within its limits.
 * 
 * @author Marcelo D. RE {@literal <marcelo.re@gmail.com>}
 */
public class WindowDesktop extends Composite {
    /**
     * List of windows in this UI.
     */
    private final LinkedHashSet<Window> windows = new LinkedHashSet<>();

    private Panel desktopPanel = new Panel();
    private CssLayout layout = new CssLayout();
    
    /**
     * create an empty desktop
     */
    public WindowDesktop() {
        this.init();
    }
    
    private void init() {
        layout.setSizeFull();
        desktopPanel.setContent(layout);
        this.setCompositionRoot(desktopPanel);
    }
    
    /**
     * Add a subwindow to the desktop.
     *
     * @param sw
     *            the subwindow to be added
     */
    public WindowDesktop addSubWindow(Window sw) {
        this.windows.add(sw);
        this.layout.addComponent(sw);
        return this;
    }
    
    /**
     * Remove the window from the desktop
     *
     * @param window
     *            the window to be removed
     */
    public boolean removeWindow(Window window) {
        if (!windows.remove(window)) {
            // Window window is not a subwindow of this UI.
            return false;
        }
        window.setParent(null);
        markAsDirty();
        window.fireClose();
        this.layout.removeComponent(window);
        desktopPanel.fireComponentDetachEvent(window);
//        fireWindowOrder(Collections.singletonMap(-1, window));
        return true;
    }
    
    /**
     * Gets all the windows added to this UI.
     *
     * @return an unmodifiable collection of windows
     */
    public Collection<Window> getWindows() {
        return Collections.unmodifiableCollection(windows);
    }

    @Override
    protected WindowDesktopState getState() {
        return (WindowDesktopState)super.getState();
        
    }
    
    @Override
    protected WindowDesktopState getState(boolean markAsDirty) {
        return (WindowDesktopState) super.getState(markAsDirty);
    }
    
}
