/*
 * Copyright 2000-2018 Vaadin Ltd.
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

package com.vaadin.shared;

import java.util.List;

import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.communication.SharedState;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ErrorLevel;

/**
 * Default shared state implementation for AbstractComponent.
 *
 * State classes of components should typically extend this class.
 *
 * @since 7.0
 */
public class AbstractComponentState extends SharedState {
    public String height = "";
    public String width = "";
    @NoLayout
    public String description = "";
    @NoLayout
    public ContentMode descriptionContentMode = ContentMode.PREFORMATTED;
    // Note: for the caption, there is a difference between null and an empty
    // string!
    public String caption = null;
    public List<String> styles = null;
    public String id = null;
    public String primaryStyleName = null;

    /** HTML formatted error message for the component. */
    public String errorMessage = null;

    /**
     * Level of error.
     *
     * @since 8.2
     */
    public ErrorLevel errorLevel = null;

    public boolean captionAsHtml = false;
}
