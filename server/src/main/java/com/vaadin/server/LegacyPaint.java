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
package com.vaadin.server;

import java.io.Serializable;

import com.vaadin.server.PaintTarget.PaintStatus;
import com.vaadin.ui.Component;
import com.vaadin.ui.LegacyComponent;

public class LegacyPaint implements Serializable {
    /**
     * 
     * <p>
     * Paints the Paintable into a UIDL stream. This method creates the UIDL
     * sequence describing it and outputs it to the given UIDL stream.
     * </p>
     * 
     * <p>
     * It is called when the contents of the component should be painted in
     * response to the component first being shown or having been altered so
     * that its visual representation is changed.
     * </p>
     * 
     * <p>
     * <b>Do not override this to paint your component.</b> Override
     * {@link #paintContent(PaintTarget)} instead.
     * </p>
     * 
     * 
     * @param target
     *            the target UIDL stream where the component should paint itself
     *            to.
     * @throws PaintException
     *             if the paint operation failed.
     */
    public static void paint(Component component, PaintTarget target)
            throws PaintException {
        // Only paint content of visible components.
        if (!LegacyCommunicationManager.isComponentVisibleToClient(component)) {
            return;
        }

        final String tag = target.getTag(component);
        final PaintStatus status = target.startPaintable(component, tag);
        if (PaintStatus.CACHED == status) {
            // nothing to do but flag as cached and close the paintable tag
            target.addAttribute("cached", true);
        } else {
            // Paint the contents of the component
            if (component instanceof LegacyComponent) {
                ((LegacyComponent) component).paintContent(target);
            }
        }
        target.endPaintable(component);

    }

}
