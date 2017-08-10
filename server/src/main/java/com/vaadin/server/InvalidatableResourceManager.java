/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package main.java.com.vaadin.server;

import com.vaadin.server.ServerRpcMethodInvocation;

/**
 * A manager that allows clients to signal to the runtime that their resources are no longer valid.
 * A use case would be an OSGi bundle that is unloading.
 *
 * @since TBD
 */
public class InvalidatableResourceManager {
    static public void invalidateCachedResources(ClassLoader classLoader) {
        ServerRpcMethodInvocation.invalidateCachedResources(classLoader);
        //TODO invalidate EventRouter resources
        //TODO invalidate all session dependencies
        //session.getCommunicationManager().getDependencies()
    }
}
