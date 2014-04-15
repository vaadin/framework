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

package com.vaadin.ui;

import java.io.Serializable;

import com.vaadin.shared.ui.MarginInfo;

/**
 * Extension to the {@link ComponentContainer} interface which adds the
 * layouting control to the elements in the container. This is required by the
 * various layout components to enable them to place other components in
 * specific locations in the UI.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
public interface Layout extends ComponentContainer, Serializable {

    /**
     * AlignmentHandler is most commonly an advanced {@link Layout} that can
     * align its components.
     */
    public interface AlignmentHandler extends Serializable {

        /**
         * Set alignment for one contained component in this layout. Use
         * predefined alignments from Alignment class.
         * 
         * Example: <code>
         *      layout.setComponentAlignment(myComponent, Alignment.TOP_RIGHT);
         * </code>
         * 
         * @param childComponent
         *            the component to align within it's layout cell.
         * @param alignment
         *            the Alignment value to be set
         */
        public void setComponentAlignment(Component childComponent,
                Alignment alignment);

        /**
         * Returns the current Alignment of given component.
         * 
         * @param childComponent
         * @return the {@link Alignment}
         */
        public Alignment getComponentAlignment(Component childComponent);

        /**
         * Sets the alignment used for new components added to this layout. The
         * default is {@link Alignment#TOP_LEFT}.
         * 
         * @param defaultComponentAlignment
         *            The new default alignment
         */
        public void setDefaultComponentAlignment(
                Alignment defaultComponentAlignment);

        /**
         * Returns the alignment used for new components added to this layout
         * 
         * @return The default alignment
         */
        public Alignment getDefaultComponentAlignment();

    }

    /**
     * This type of layout supports automatic addition of space between its
     * components.
     * 
     */
    public interface SpacingHandler extends Serializable {
        /**
         * Enable spacing between child components within this layout.
         * 
         * <p>
         * <strong>NOTE:</strong> This will only affect the space between
         * components, not the space around all the components in the layout
         * (i.e. do not confuse this with the cellspacing attribute of a HTML
         * Table). Use {@link #setMargin(boolean)} to add space around the
         * layout.
         * </p>
         * 
         * <p>
         * See the reference manual for more information about CSS rules for
         * defining the amount of spacing to use.
         * </p>
         * 
         * @param enabled
         *            true if spacing should be turned on, false if it should be
         *            turned off
         */
        public void setSpacing(boolean enabled);

        /**
         * 
         * @return true if spacing between child components within this layout
         *         is enabled, false otherwise
         */
        public boolean isSpacing();
    }

    /**
     * This type of layout supports automatic addition of margins (space around
     * its components).
     */
    public interface MarginHandler extends Serializable {

        /**
         * Enable layout margins. Affects all four sides of the layout. This
         * will tell the client-side implementation to leave extra space around
         * the layout. The client-side implementation decides the actual amount,
         * and it can vary between themes.
         * 
         * @param enabled
         *            true if margins should be enabled on all sides, false to
         *            disable all margins
         */
        public void setMargin(boolean enabled);

        /**
         * Enable margins for this layout.
         * 
         * <p>
         * <strong>NOTE:</strong> This will only affect the space around the
         * components in the layout, not space between the components in the
         * layout. Use {@link #setSpacing(boolean)} to add space between the
         * components in the layout.
         * </p>
         * 
         * <p>
         * See the reference manual for more information about CSS rules for
         * defining the size of the margin.
         * </p>
         * 
         * @param marginInfo
         *            MarginInfo object containing the new margins.
         */
        public void setMargin(MarginInfo marginInfo);

        /**
         * 
         * @return MarginInfo containing the currently enabled margins.
         */
        public MarginInfo getMargin();
    }

}
