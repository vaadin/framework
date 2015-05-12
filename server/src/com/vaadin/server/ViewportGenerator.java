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

/**
 * Callback for generating a viewport tag content based on a request.
 * 
 * @see ViewportGenerator
 * 
 * @since 7.4
 * 
 * @author Vaadin Ltd
 */
public interface ViewportGenerator extends Serializable {
    /**
     * Generates a viewport tag based on a request.
     * 
     * @param request
     *            the request for which to generate a viewport tag
     * @return the viewport tag content
     */
    public String getViewport(VaadinRequest request);
}
