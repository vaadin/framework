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
package com.vaadin.client.metadata;

import com.google.gwt.core.client.JavaScriptObject;
import com.vaadin.client.JsArrayObject;

/**
 * Special {@link Invoker} that uses JSNI to invoke methods with limited
 * visibility.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public abstract class JsniInvoker implements Invoker {

    @Override
    public Object invoke(Object target, Object... params) {
        JsArrayObject<Object> jsParams = JavaScriptObject.createArray().cast();
        for (Object object : params) {
            jsParams.add(object);
        }
        return jsniInvoke(target, jsParams);
    }

    /**
     * Abstract method that will be generated to contain JSNI for invoking the
     * actual method.
     * 
     * @param target
     *            the object upon which to invoke the method
     * @param params
     *            a js array with arguments to pass to the method
     * @return the value returned by the invoked method, or <code>null</code> if
     *         the target method return type is <code>void</code>.
     */
    protected abstract Object jsniInvoke(Object target,
            JsArrayObject<Object> params);

}
