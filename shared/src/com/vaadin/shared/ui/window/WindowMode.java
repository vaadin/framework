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
package com.vaadin.shared.ui.window;

/**
 * Determines the mode of the Window.
 * <p>
 * A window mode decides the size and position of the Window. It can be set to
 * {@link #NORMAL} or {@link #MAXIMIZED}.
 * 
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public enum WindowMode {
    /**
     * Normal mode. The window size and position is determined by the window
     * state.
     */
    NORMAL,
    /**
     * Maximized mode. The window is positioned in the top left corner and fills
     * the whole screen.
     */
    MAXIMIZED;
}
