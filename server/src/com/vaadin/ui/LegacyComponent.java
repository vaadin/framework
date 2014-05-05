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

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.VariableOwner;

/**
 * Interface provided to ease porting of Vaadin 6 components to Vaadin 7. By
 * implementing this interface your Component will be able to use
 * {@link #paintContent(PaintTarget)} and
 * {@link #changeVariables(Object, java.util.Map)} just like in Vaadin 6.
 * 
 * @deprecated As of 7.0. This class is only intended to ease migration and
 *             should not be used for new projects.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
@Deprecated
public interface LegacyComponent extends VariableOwner, Component,
        ConnectorEventListener {

    /**
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
     * @param target
     *            the target UIDL stream where the component should paint itself
     *            to.
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void paintContent(PaintTarget target) throws PaintException;

    /**
     * (non-Javadoc) {@inheritDoc}
     * <p>
     * For a LegacyComponent, markAsDirty will also cause
     * {@link #paintContent(PaintTarget)} to be called before sending changes to
     * the client.
     * 
     * @see com.vaadin.server.ClientConnector#markAsDirty()
     */
    @Override
    public void markAsDirty();
}
