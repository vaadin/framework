/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.client;

import com.google.gwt.core.client.impl.SchedulerImpl;

public class VSchedulerImpl extends SchedulerImpl {

    /**
     * Keeps track of if there are deferred commands that are being executed. 0
     * == no deferred commands currently in progress, > 0 otherwise.
     */
    private int deferredCommandTrackers = 0;

    @Override
    public void scheduleDeferred(ScheduledCommand cmd) {
        deferredCommandTrackers++;
        super.scheduleDeferred(cmd);
        super.scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                deferredCommandTrackers--;
            }
        });
    }

    public boolean hasWorkQueued() {
        boolean hasWorkQueued = (deferredCommandTrackers != 0);
        return hasWorkQueued;
    }
}
