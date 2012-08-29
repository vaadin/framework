/* 
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.client;

import java.util.Set;

public interface Console {

    public abstract void log(String msg);

    public abstract void log(Throwable e);

    public abstract void error(Throwable e);

    public abstract void error(String msg);

    public abstract void printObject(Object msg);

    public abstract void dirUIDL(ValueMap u, ApplicationConnection client);

    public abstract void printLayoutProblems(ValueMap meta,
            ApplicationConnection applicationConnection,
            Set<ComponentConnector> zeroHeightComponents,
            Set<ComponentConnector> zeroWidthComponents);

    public abstract void setQuietMode(boolean quietDebugMode);

    public abstract void init();

}