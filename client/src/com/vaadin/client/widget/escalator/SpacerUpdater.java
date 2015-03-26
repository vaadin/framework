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
package com.vaadin.client.widget.escalator;

import com.vaadin.client.widget.escalator.RowContainer.BodyRowContainer;

/**
 * An interface that handles the display of content for spacers.
 * <p>
 * The updater is responsible for making sure all elements are properly
 * constructed and cleaned up.
 * 
 * @since 7.5.0
 * @author Vaadin Ltd
 * @see Spacer
 * @see BodyRowContainer
 */
public interface SpacerUpdater {

    /** A spacer updater that does nothing. */
    public static final SpacerUpdater NULL = new SpacerUpdater() {
        @Override
        public void init(Spacer spacer) {
            // NOOP
        }

        @Override
        public void destroy(Spacer spacer) {
            // NOOP
        }
    };

    /**
     * Called whenever a spacer should be initialized with content.
     * 
     * @param spacer
     *            the spacer reference that should be initialized
     */
    void init(Spacer spacer);

    /**
     * Called whenever a spacer should be cleaned.
     * <p>
     * The structure to clean up is the same that has been constructed by
     * {@link #init(Spacer)}.
     * 
     * @param spacer
     *            the spacer reference that should be destroyed
     */
    void destroy(Spacer spacer);
}
