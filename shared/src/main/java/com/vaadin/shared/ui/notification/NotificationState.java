/*
 * Copyright 2000-2021 Vaadin Ltd.
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

package com.vaadin.shared.ui.notification;

import com.vaadin.shared.Position;
import com.vaadin.shared.communication.SharedState;

/**
 * Shared state for {@link com.vaadin.ui.Notification}.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.2
 */
public class NotificationState extends SharedState {

    /**
     * The {@link com.vaadin.ui.Notification} caption, can be {@code null}.
     */
    public String caption;

    /**
     * The description, can be {@code null}.
     */
    public String description;

    /**
     * Whether texts are interpreted as HTML ({@code true}) or not
     * ({@code false}).
     */
    public boolean htmlContentAllowed;

    /**
     * The style name, can be {@code null}.
     */
    public String styleName;

    /**
     * The {@link Position} of the {@link com.vaadin.ui.Notification}, can not
     * be {@code null}.
     */
    public Position position = Position.MIDDLE_CENTER;

    /**
     * The delay in milliseconds before disappearing, {@code -1} for forever.
     */
    public int delay;
}
