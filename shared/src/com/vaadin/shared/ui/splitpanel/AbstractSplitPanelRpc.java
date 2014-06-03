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
package com.vaadin.shared.ui.splitpanel;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

public interface AbstractSplitPanelRpc extends ServerRpc {

    /**
     * Called when the position has been updated by the user.
     * 
     * @param position
     *            The new position in % if the current unit is %, in px
     *            otherwise
     */
    public void setSplitterPosition(float position);

    /**
     * Called when a click event has occurred on the splitter.
     * 
     * @param mouseDetails
     *            Details about the mouse when the event took place
     */
    public void splitterClick(MouseEventDetails mouseDetails);

}
