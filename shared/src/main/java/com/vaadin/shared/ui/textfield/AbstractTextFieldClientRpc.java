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
package com.vaadin.shared.ui.textfield;

import com.vaadin.shared.communication.ClientRpc;

/**
 * Server to client RPC interface for AbstractTextField.
 * 
 * @since 8.0
 */
public interface AbstractTextFieldClientRpc extends ClientRpc {
    /**
     * Selects the given range in the field.
     *
     * @param start
     *            the start of the range
     * @param length
     *            the length to select
     */
    void selectRange(int start, int length);

    /**
     * Selects everything in the field.
     */
    void selectAll();
}
