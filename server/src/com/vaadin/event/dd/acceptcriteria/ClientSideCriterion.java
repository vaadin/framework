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
package com.vaadin.event.dd.acceptcriteria;

import java.io.Serializable;

import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;

/**
 * Parent class for criteria that can be completely validated on client side.
 * All classes that provide criteria that can be completely validated on client
 * side should extend this class.
 * 
 * It is recommended that subclasses of ClientSideCriterion re-validate the
 * condition on the server side in
 * {@link AcceptCriterion#accept(com.vaadin.event.dd.DragAndDropEvent)} after
 * the client side validation has accepted a transfer.
 * 
 * @since 6.3
 */
public abstract class ClientSideCriterion implements Serializable,
        AcceptCriterion {

    /*
     * All criteria that extend this must be completely validatable on client
     * side.
     * 
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.event.dd.acceptCriteria.AcceptCriterion#isClientSideVerifiable
     * ()
     */
    @Override
    public final boolean isClientSideVerifiable() {
        return true;
    }

    @Override
    public void paint(PaintTarget target) throws PaintException {
        target.startTag("-ac");
        target.addAttribute("name", getIdentifier());
        paintContent(target);
        target.endTag("-ac");
    }

    protected void paintContent(PaintTarget target) throws PaintException {
    }

    protected String getIdentifier() {
        return getClass().getCanonicalName();
    }

    @Override
    public final void paintResponse(PaintTarget target) throws PaintException {
        // NOP, nothing to do as this is client side verified criterion
    }

}
