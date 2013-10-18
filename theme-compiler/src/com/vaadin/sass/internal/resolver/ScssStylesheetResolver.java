/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.sass.internal.resolver;

import org.w3c.css.sac.InputSource;

import com.vaadin.sass.internal.ScssStylesheet;

public interface ScssStylesheetResolver {
    /**
     * Called with the "identifier" of a stylesheet that the resolver should try
     * to find. The identifier is basically a filename, like "runo.scss" or
     * "addon/styles.scss", but might exclude ".scss". The resolver must
     * {@link InputSource#setURI(String)} to the final location where the
     * stylesheet was found, e.g "runo.scss" might result in a URI like
     * "VAADIN/themes/runo/runo.scss".
     * 
     * @param parentStylesheet
     *            The parent style sheet
     * @param identifier
     *            used fo find stylesheet
     * @return InputSource for stylesheet (with URI set) or null if not found
     */
    public InputSource resolve(ScssStylesheet parentStylesheet,
            String identifier);
}