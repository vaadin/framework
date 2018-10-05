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

package com.vaadin.util;

import java.io.Serializable;

/**
 * Fallback that is used to revolve current instances when they are not
 * available by regular means.
 * <p>
 * This interface is used internally by the framework and it's not meant for
 * public usage.
 *
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the type of the instances returned by this resolver
 *
 * @see CurrentInstance#get(Class)
 * @see CurrentInstance#defineFallbackResolver(Class,
 *      CurrentInstanceFallbackResolver)
 *
 * @since 8.5.2
 *
 */
public interface CurrentInstanceFallbackResolver<T> extends Serializable {

    /**
     * Resolves a current instance for the type {@code T}.
     *
     * @return the current instance, or <code>null</code> if none can be found
     */
    T resolve();

}
