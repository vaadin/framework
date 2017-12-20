/*
 * Copyright 2000-2016 Vaadin Ltd.
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

/**
 * Implemented by widgets which need to know if they are going to be
 * {@link com.google.gwt.user.client.ui.Panel#orphan(com.google.gwt.user.client.ui.Widget)
 * orphaned}, so it can store the remaining DOM events, to be used after it is
 * {@link com.google.gwt.user.client.ui.Panel#adopt(com.google.gwt.user.client.ui.Widget)
 * adopted} again.
 *
 * @since
 */
public interface Orphanable {

    /**
     * Informs the widget that it will be orphaned later, so it can store DOM
     * events.
     */
    void beforeOrphaned();

    /**
     * Informs the widget that it is adopted again, and it should trigger the
     * stored DOM events.
     */
    void afterAdoption();
}
