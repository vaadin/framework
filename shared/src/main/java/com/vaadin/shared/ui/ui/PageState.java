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
package com.vaadin.shared.ui.ui;

import java.io.Serializable;

/**
 * The shared state of a {@link com.vaadin.server.Page Page}.
 *
 * Note that at the moment this is not a stand-alone state class but embedded in
 * {@link UIState}. This might change in the future.
 *
 * @since 7.1
 */
public class PageState implements Serializable {
    /**
     * True if the page has browser window resize listeners.
     */
    public boolean hasResizeListeners = false;

    /**
     * Non-null if the title is set. Null means Vaadin does not touch the title.
     */
    public String title = null;
}
