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
package com.vaadin.client.widget.grid.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Common handler interface for editor events
 */
public interface EditorEventHandler extends EventHandler {

    /**
     * Action to perform when the editor has been opened
     * 
     * @param e
     *            an editor open event object
     */
    public void onEditorOpen(EditorOpenEvent e);

    /**
     * Action to perform when the editor is re-opened on another row
     * 
     * @param e
     *            an editor move event object
     */
    public void onEditorMove(EditorMoveEvent e);

    /**
     * Action to perform when the editor is closed
     * 
     * @param e
     *            an editor close event object
     */
    public void onEditorClose(EditorCloseEvent e);

}