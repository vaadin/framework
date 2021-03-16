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

import com.vaadin.client.UIDL;

public abstract class VAcceptCriterion {

    /**
     * Checks if current drag event has valid drop target and target accepts the
     * transferable. If drop target is valid, callback is used.
     *
     * @param drag
     * @param configuration
     * @param callback
     */
    public void accept(final VDragEvent drag, UIDL configuration,
            final VAcceptCallback callback) {
        if (needsServerSideCheck(drag, configuration)) {
            VDragEventServerCallback acceptCallback = new VDragEventServerCallback() {
                @Override
                public void handleResponse(boolean accepted, UIDL response) {
                    if (accepted) {
                        callback.accepted(drag);
                    }
                }
            };
            VDragAndDropManager.get().visitServer(acceptCallback);
        } else {
            boolean validates = accept(drag, configuration);
            if (validates) {
                callback.accepted(drag);
            }
        }

    }

    protected abstract boolean accept(VDragEvent drag, UIDL configuration);

    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
        return false;
    }

}
