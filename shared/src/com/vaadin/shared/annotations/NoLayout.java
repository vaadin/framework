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
package com.vaadin.shared.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotation used to mark client RPC methods, state fields, or state setter
 * methods that should not trigger an layout phase after changes have been
 * processed. Whenever there's at least one change that is not marked with this
 * annotation, the framework will assume some sizes might have changed an will
 * therefore start a layout phase after applying the changes.
 * <p>
 * This annotation can be used for any RPC method or state property that does
 * not cause the size of the component or its children to change. Please note
 * that almost anything related to CSS (e.g. adding or removing a stylename) has
 * the potential of causing sizes to change with appropriate style definitions
 * in the application theme.
 * 
 * @since 7.4
 * 
 * @author Vaadin Ltd
 */
@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface NoLayout {
    // Just an empty marker annotation
}
