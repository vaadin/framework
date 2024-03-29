/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.shared.ui.upload;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.shared.annotations.NoLayout;

/**
 * Shared state for the Upload component.
 *
 * @since 7.6
 */
public class UploadState extends AbstractComponentState {

    /** Is the upload component in immediate mode or not. */
    public boolean immediateMode = true;

    {
        primaryStyleName = "v-upload";
    }

    /** Upload component's list of accepted content-types. */
    @DelegateToWidget
    @NoLayout
    public String acceptMimeTypes;

    /** Caption of the button that fires uploading. */
    public String buttonCaption = "Upload";

    /** Style name of the button that fires uploading. */
    public String buttonStyleName = "v-button";

    /**
     * Should the caption of the button that fires uploading be rendered as
     * HTML.
     */
    public boolean buttonCaptionAsHtml;
}
