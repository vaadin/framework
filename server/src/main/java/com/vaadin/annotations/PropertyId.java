/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;

/**
 * Defines the custom property name to be bound to a {@link HasValue field
 * component} using {@link Binder}.
 * <p>
 * The automatic data binding in Binder relies on a naming convention by
 * default: properties of an item are bound to similarly named field components
 * in given a editor object. If you want to map a property with a different name
 * (ID) to a {@link HasValue}, you can use this annotation for the member
 * fields, with the name (ID) of the desired property as the parameter.
 * <p>
 * In following usage example, the text field would be bound to property "foo"
 * in the Entity class.
 * <pre>
    class Editor extends FormLayout {
        &#64;PropertyId("foo")
        TextField myField = new TextField();
    }

    class Entity {
        String foo;
    }

    {
        Editor editor = new Editor();
        Binder&lt;Entity&gt; binder = new Binder(Entity.class);
        binder.bindInstanceFields(editor);
    }
   </pre>
 *
 * @since 8.0
 * @author Vaadin Ltd
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyId {
    String value();
}
