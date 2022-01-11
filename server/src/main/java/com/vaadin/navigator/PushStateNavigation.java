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
package com.vaadin.navigator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.Page.PopStateEvent;
import com.vaadin.ui.UI;

/**
 * Annotation for {@link UI}s to enable the PushState navigation mode when
 * initializing a {@link Navigator} for it. PushState navigation is an
 * alternative way to handle URLs in the {@link Navigator}. It uses path info,
 * HTML5 push state and {@link PopStateEvent}s to track views and enable
 * listening to view changes.
 * <p>
 * <strong>Note:</strong> For PushState navigation to work, the
 * {@link DeploymentConfiguration} parameter
 * {@link DeploymentConfiguration#isSendUrlsAsParameters() SendUrlAsParameters}
 * must not be set to {@code false}.
 *
 * @since 8.2
 */
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
public @interface PushStateNavigation {
}
