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

package com.vaadin.terminal.gwt.client.ui;

/**
 * This class is used for "row actions" in VTree and ITable
 */
public class TreeAction extends Action {

    String targetKey = "";
    String actionKey = "";

    public TreeAction(ActionOwner owner) {
        super(owner);
    }

    public TreeAction(ActionOwner owner, String target, String action) {
        this(owner);
        targetKey = target;
        actionKey = action;
    }

    /**
     * Sends message to server that this action has been fired. Messages are
     * "standard" Vaadin messages whose value is comma separated pair of
     * targetKey (row, treeNod ...) and actions id.
     * 
     * Variablename is always "action".
     * 
     * Actions are always sent immediatedly to server.
     */
    @Override
    public void execute() {
        owner.getClient().updateVariable(owner.getPaintableId(), "action",
                targetKey + "," + actionKey, true);
        owner.getClient().getContextMenu().hide();
    }

    public String getActionKey() {
        return actionKey;
    }

    public void setActionKey(String actionKey) {
        this.actionKey = actionKey;
    }

    public String getTargetKey() {
        return targetKey;
    }

    public void setTargetKey(String targetKey) {
        this.targetKey = targetKey;
    }
}
