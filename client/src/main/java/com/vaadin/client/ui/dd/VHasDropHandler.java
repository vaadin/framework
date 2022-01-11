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
package com.vaadin.client.ui.dd;

import com.vaadin.client.ComponentConnector;

/**
 * Used to detect Widget from widget tree that has {@link #getDropHandler()}
 *
 * Decide whether to get rid of this class. If so, {@link VAbstractDropHandler}
 * must extend {@link ComponentConnector}.
 *
 */
public interface VHasDropHandler {
    public VDropHandler getDropHandler();
}
