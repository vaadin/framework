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

package com.vaadin.client;

/**
 * ContainerResizedListener interface is useful for Widgets that support
 * relative sizes and who need some additional sizing logic.
 * 
 * @deprecated As of 7.0, serves no purpose. Use {@link LayoutManager} and its
 *             methods instead.
 */
@Deprecated
public interface ContainerResizedListener {
    /**
     * This function is run when container box has been resized. Object
     * implementing ContainerResizedListener is responsible to call the same
     * function on its ancestors that implement NeedsLayout in case their
     * container has resized. runAnchestorsLayout(HasWidgets parent) function
     * from Util class may be a good helper for this.
     * 
     * @deprecated As of 7.0, this method is never called by the framework.
     * 
     */
    @Deprecated
    public void iLayout();
}
