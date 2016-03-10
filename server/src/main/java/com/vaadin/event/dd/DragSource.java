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
package com.vaadin.event.dd;

import java.util.Map;

import com.vaadin.event.Transferable;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;

/**
 * DragSource is a {@link Component} that builds a {@link Transferable} for a
 * drag and drop operation.
 * <p>
 * In Vaadin the drag and drop operation practically starts from client side
 * component. The client side component initially defines the data that will be
 * present in {@link Transferable} object on server side. If the server side
 * counterpart of the component implements this interface, terminal
 * implementation lets it create the {@link Transferable} instance from the raw
 * client side "seed data". This way server side implementation may translate or
 * extend the data that will be available for {@link DropHandler}.
 * 
 * @since 6.3
 * 
 */
public interface DragSource extends Component {

    /**
     * DragSource may convert data added by client side component to meaningful
     * values for server side developer or add other data based on it.
     * 
     * <p>
     * For example Tree converts item identifiers to generated string keys for
     * the client side. Vaadin developer don't and can't know anything about
     * these generated keys, only about item identifiers. When tree node is
     * dragged client puts that key to {@link Transferable}s client side
     * counterpart. In {@link Tree#getTransferable(Map)} the key is converted
     * back to item identifier that the server side developer can use.
     * <p>
     * 
     * @since 6.3
     * @param rawVariables
     *            the data that client side initially included in
     *            {@link Transferable}s client side counterpart.
     * @return the {@link Transferable} instance that will be passed to
     *         {@link DropHandler} (and/or {@link AcceptCriterion})
     */
    public Transferable getTransferable(Map<String, Object> rawVariables);

}
