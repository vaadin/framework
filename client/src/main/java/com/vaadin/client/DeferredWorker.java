/*
 * Copyright 2000-2020 Vaadin Ltd.
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

/**
 * Give widgets and connectors the possibility to indicate to the framework that
 * there is work scheduled to be executed in the near future and that the
 * framework should wait for this work to complete before assuming the UI has
 * reached a steady state.
 *
 * @since 7.3
 * @author Vaadin Ltd
 */
public interface DeferredWorker {
    /**
     * Checks whether there are operations pending for this widget or connector
     * that must be executed before reaching a steady state.
     *
     * @returns <code>true</code> iff there are operations pending which must be
     *          executed before reaching a steady state
     */
    public boolean isWorkPending();
}
