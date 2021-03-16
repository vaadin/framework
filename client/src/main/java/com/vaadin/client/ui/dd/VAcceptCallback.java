/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.client.ui.dd;

public interface VAcceptCallback {

    /**
     * This method is called by {@link VDragAndDropManager} if the
     * {@link VDragEvent} is still active. Developer can update for example drag
     * icon or empahsis the target if the target accepts the transferable. If
     * the drag and drop operation ends or the {@link VAbstractDropHandler} has
     * changed before response arrives, the method is never called.
     */
    public void accepted(VDragEvent event);

}
