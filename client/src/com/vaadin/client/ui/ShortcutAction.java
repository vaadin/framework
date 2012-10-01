/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.client.ui;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.server.KeyMapper;

public class ShortcutAction {

    private final ShortcutKeyCombination sc;
    private final String caption;
    private final String key;
    private String targetCID;
    private String targetAction;

    /**
     * Constructor
     * 
     * @param key
     *            The @link {@link KeyMapper} key of the action.
     * @param sc
     *            The key combination that triggers the action
     * @param caption
     *            The caption of the action
     */
    public ShortcutAction(String key, ShortcutKeyCombination sc, String caption) {
        this(key, sc, caption, null, null);
    }

    /**
     * Constructor
     * 
     * @param key
     *            The @link {@link KeyMapper} key of the action.
     * @param sc
     *            The key combination that triggers the action
     * @param caption
     *            The caption of the action
     * @param targetPID
     *            The pid of the component the action is targeting. We use the
     *            pid, instead of the actual Paintable here, so we can delay the
     *            fetching of the Paintable in cases where the Paintable does
     *            not yet exist when the action is painted.
     * @param targetAction
     *            The target string of the action. The target string is given to
     *            the targeted Paintable if the paintable implements the
     *            {@link ShortcutActionTarget} interface.
     */
    public ShortcutAction(String key, ShortcutKeyCombination sc,
            String caption, String targetCID, String targetAction) {
        this.sc = sc;
        this.key = key;
        this.caption = caption;
        this.targetCID = targetCID;
        this.targetAction = targetAction;
    }

    /**
     * Get the key combination that triggers the action
     */
    public ShortcutKeyCombination getShortcutCombination() {
        return sc;
    }

    /**
     * Get the caption of the action
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Get the {@link KeyMapper} key for the action
     */
    public String getKey() {
        return key;
    }

    /**
     * Get the pid of the target of the action. Use
     * {@link ApplicationConnection#getPaintable(String)} to get the actual
     * Paintable
     */
    public String getTargetCID() {
        return targetCID;
    }

    /**
     * Get the target string of the action
     */
    public String getTargetAction() {
        return targetAction;
    }

}