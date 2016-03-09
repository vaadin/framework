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

package com.vaadin.shared.communication;

/**
 * The mode of bidirectional ("push") communication that is in use.
 * 
 * @see com.vaadin.server.DeploymentConfiguration#getPushMode()
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public enum PushMode {
    /**
     * Push is disabled. Regular AJAX requests are used to communicate between
     * the client and the server. Asynchronous messages from the server are not
     * possible. {@link com.vaadin.ui.UI#push() ui.push()} throws
     * IllegalStateException.
     * <p>
     * This is the default mode unless
     * {@link com.vaadin.server.DeploymentConfiguration#getPushMode()
     * configured} otherwise.
     */
    DISABLED,

    /**
     * Push is enabled. A bidirectional channel is established between the
     * client and server and used to communicate state changes and RPC
     * invocations. The client is not automatically updated if the server-side
     * state is asynchronously changed; {@link com.vaadin.ui.UI#push()
     * ui.push()} must be explicitly called.
     */
    MANUAL,

    /**
     * Push is enabled. Like {@link #MANUAL}, but asynchronous changes to the
     * server-side state are automatically pushed to the client once the session
     * lock is released.
     */
    AUTOMATIC;

    /**
     * Checks whether the push mode is using push functionality
     * 
     * @return <code>true</code> if this mode requires push functionality;
     *         <code>false</code> if no push functionality is used for this
     *         mode.
     */
    public boolean isEnabled() {
        return this != DISABLED;
    }
}
