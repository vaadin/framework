/*
 * Copyright 2000-2021 Vaadin Ltd.
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

package com.vaadin.server;

import java.io.Serializable;

/**
 * <code>Resource</code> provided to the client terminal. Support for actually
 * displaying the resource type is left to the terminal.
 *
 * @author Vaadin Ltd.
 * @since 3.0
 */
public interface Resource extends Serializable {

    /**
     * Gets the MIME type of the resource.
     *
     * @return the MIME type of the resource.
     */
    public String getMIMEType();
}
