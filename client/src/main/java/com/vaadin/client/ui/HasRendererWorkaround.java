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

package com.vaadin.client.ui;

/**
 * For internal use only. May be removed or replaced in the future.
 * <p>
 * Renderers are sometimes too slow to work well with widgets by default. This
 * interface signifies that the Widget in question may need further refreshing
 * after Grid has finished its own handling.
 *
 * @author Vaadin Ltd.
 * @since
 */
public interface HasRendererWorkaround {

    /**
     * For internal use only. May be removed or replaced in the future.
     */
    public void rendererWorkaround();
}
