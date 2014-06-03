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

package com.vaadin.shared.ui.dd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation type used to point the server side counterpart for client side
 * a {@link com.vaadin.client.ui.dd.VAcceptCriterion} class.
 * <p>
 * Annotations are used at GWT compilation phase, so remember to rebuild your
 * widgetset if you do changes for {@link AcceptCriterion} mappings.
 * 
 * Prior to Vaadin 7, the mapping was done with an annotation on server side
 * classes.
 * 
 * @since 7.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AcceptCriterion {
    /**
     * @return the class of the server side counterpart for the annotated
     *         criterion
     */
    Class<?> value();

}
