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
package com.vaadin.data.fieldgroup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the custom property name to be bound to a {@link Field} using
 * {@link FieldGroup} or {@link BeanFieldGroup}.
 * <p>
 * The automatic data binding in FieldGroup and BeanFieldGroup relies on a
 * naming convention by default: properties of an item are bound to similarly
 * named field components in given a editor object. If you want to map a
 * property with a different name (ID) to a {@link com.vaadin.client.ui.Field},
 * you can use this annotation for the member fields, with the name (ID) of the
 * desired property as the parameter.
 * <p>
 * In following usage example, the text field would be bound to property "foo"
 * in the Entity class. <code>
 * <pre>
 *    class Editor extends FormLayout {
        &#64;PropertyId("foo")
        TextField myField = new TextField();
    }
    
    class Entity {
        String foo;
    }
    
    {
        Editor e = new Editor();
        BeanFieldGroup.bindFieldsUnbuffered(new Entity(), e);
    }
   </pre>
 * </code>
 * 
 * @since 7.0
 * @author Vaadin Ltd
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyId {
    String value();
}
