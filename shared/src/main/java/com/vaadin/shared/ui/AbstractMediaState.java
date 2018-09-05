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
package com.vaadin.shared.ui;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.communication.URLReference;

public class AbstractMediaState extends AbstractComponentState {
    public boolean showControls;

    @NoLayout
    public String altText;

    public boolean htmlContentAllowed;

    @NoLayout
    public boolean autoplay;

    @NoLayout
    public boolean muted;

    /**
     * Preload mode for the media.
     *
     * @since 7.7.11
     */
    @NoLayout
    public PreloadMode preload;

    /**
     * Looping of media active (true) or not.
     *
     * @since 7.7.11
     */
    @NoLayout
    public boolean loop;

    public List<URLReference> sources = new ArrayList<>();

    public List<String> sourceTypes = new ArrayList<>();
}
