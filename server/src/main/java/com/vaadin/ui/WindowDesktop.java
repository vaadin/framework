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

import com.vaadin.event.Action;
import com.vaadin.event.ActionManager;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.windowdesktop.WindowDesktopState;
import com.vaadin.ui.declarative.DesignContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Element;

/**
 *
 * @author Marcelo D. Ré {@literal <marcelo.re@gmail.com>}
 */
public class WindowDesktop extends Panel {
    private final static Logger LOGGER = Logger.getLogger(WindowDesktop.class .getName());
    static {
        LOGGER.setLevel(Level.INFO);
    }
    /**
     * List of windows in this UI.
     */
    private final LinkedHashSet<Window> windows = new LinkedHashSet<>();

    private CssLayout layout = new CssLayout();
    
    public WindowDesktop() {
        this.init();
    }

    public WindowDesktop(Component content) {
        super(content);
        this.init();
    }

    public WindowDesktop(String caption) {
        super(caption);
        this.init();
    }

    public WindowDesktop(String caption, Component content) {
        super(caption, content);
        this.init();
    }
    
    private void init() {
        layout.setSizeFull();
        this.setContent(layout);
    }
    
    public WindowDesktop addSubWindow(Window sw) {
        this.windows.add(sw);
        this.layout.addComponent(sw);
        return this;
    }
    
    public boolean removeWindow(Window window) {
        if (!windows.remove(window)) {
            // Window window is not a subwindow of this UI.
            return false;
        }
        window.setParent(null);
        markAsDirty();
        window.fireClose();
        this.layout.removeComponent(window);
        fireComponentDetachEvent(window);
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
